package converter.parsing;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class XMLParser extends Parser {
    public XMLParser(String input) {
        super(input);
        scanner.useDelimiter("\\s*((?<=[<>/])|(?=[<>/]))\\s*");
    }

    public Tree getTree() {
        checkNextToken("<", "XML document not opening with a tag!");
        return new Tree(List.of(getPair()));
    }

    private KeyValuePair getPair() {
        Tag openingTag = getTag();
        String name = openingTag.name;

        if (openingTag.type == TagType.EMPTY) {
            return new KeyValuePair(name, new NullElementary(), openingTag.attributes);
        }

        if (!scanner.hasNext("<")) {
            String content = scanner.next();

            checkNextToken("<", "Invalid bracket placement!");

            Tag closingTag = getTag();

            if (!(closingTag.name.equals(openingTag.name))) {
                throw new ParserException("Different opening and closing tags' names!");
            }

            if (!(closingTag.type == TagType.CLOSING)) {
                throw new ParserException("Closing tag expected!");
            }

            return new KeyValuePair(name, new StringElementary(content), openingTag.attributes);
        }

        scanner.next();

        if (scanner.hasNext("/")) {
            Tag closingTag = getTag();
            if (!closingTag.name.equals(openingTag.name)) {
                throw new ParserException("Different opening and closing tags' names!");
            }

            return new KeyValuePair(name, new StringElementary(""), openingTag.attributes);
        }

        List<KeyValuePair> content = new ArrayList<>();

        do {
            content.add(getPair());
            checkNextToken("<", "Invalid bracket placement!");
        } while (!scanner.hasNext("/"));

        Tag closingTag = getTag();

        if (!closingTag.name.equals(openingTag.name)) {
            throw new ParserException("Different opening and closing tags' names!");
        }

        if (content.size() > 1 && content.stream().map(KeyValuePair::getName).allMatch(content.get(0).getName()::equals)) {
            var arrayComponents = content.stream().map(pair -> new ArrayObj.ArrayComponent(
                    pair.getAttributes(),
                    pair.getContent()
            )).collect(Collectors.toList());
            return new KeyValuePair(name, new ArrayObj(arrayComponents), openingTag.attributes);
        }

        return new KeyValuePair(name, new PairListObj(content), openingTag.attributes);
    }

    private Tag getTag() {
        while (scanner.hasNext("\\?.*\\?")) {
            scanner.next();
            checkNextToken(">", "Invalid bracket placement!");
            checkNextToken("<", "Meta tags can't have a body!");
        }

        String tokens = scanner.next().trim();

        if (tokens.equals("/")) {
            String name = scanner.next();
            checkNextToken(">", "No closing bracket in closing tag!");
            return new Tag(TagType.CLOSING, Collections.emptyMap(), name);
        }

        Matcher tokensMatcher = Pattern.compile("([^\\s\"]+)(\\s+((\\s*[^\"]+?\\s*=\\s*(\"[^\"]*\"|'[^\"']*')\\s*)+))?")
                .matcher(tokens);

        if (tokensMatcher.matches()) {
            String name = tokensMatcher.group(1);

            Map<String, String> attributes = new LinkedHashMap<>();

            String attrTokens = tokensMatcher.group(3);

            if (attrTokens != null) {
                Matcher matcher = Pattern.compile("\\s*([^\"]+?)\\s*=\\s*(\"([^\"]*)\"|'([^\"']*)')\\s*")
                        .matcher(attrTokens);
                while (matcher.find()) {
                    String pairName = matcher.group(1);

                    if (attributes.containsKey(pairName)) {
                        throw new ParserException("Repeating attributes!");
                    }

                    String pairValue = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
                    attributes.put(pairName, pairValue);
                }
            }

            String closingToken = scanner.next();

            if (closingToken.equals("/")) {
                checkNextToken(">", "Invalid bracket placement!");
                return new Tag(TagType.EMPTY, attributes, name);
            } else if (closingToken.equals(">")){
                return new Tag(TagType.OPENING, attributes, name);
            } else {
                throw new ParserException("Invalid tag closure!");
            }
        } else {
            throw new ParserException("Invalid tag body format!");
        }
    }

    private static class Tag {
        private final TagType type;
        private final Map<String, String> attributes;
        private final String name;

        Tag(TagType type, Map<String, String> attributes, String name) {
            this.type = type;
            this.attributes = attributes;
            this.name = name;
        }
    }

    private enum TagType {
        OPENING, CLOSING, EMPTY
    }
}