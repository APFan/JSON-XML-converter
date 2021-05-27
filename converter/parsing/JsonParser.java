package converter.parsing;

import java.util.*;
import java.util.stream.Collectors;

public class JsonParser extends Parser{
    public JsonParser(String input) {
        super(input);
        scanner.useDelimiter("(?<=[]\\[{:},])\\s*|(?=[]\\[{:},])\\s*");
    }

    public Tree getTree() {
        return new Tree(getPairList());
    }

    private List<KeyValuePair> getPairList() {
        checkNextToken("{", "Json object doesn't start with '{'!");

        if (scanner.hasNext("}")){
            scanner.next();
            return Collections.emptyList();
        }

        List<KeyValuePair> elements = new ArrayList<>();

        while (true) {
            elements.add(getPair());

            String token = scanner.next();

            if (token.equals("}")) {
                break;
            } else if (!token.equals(",")) {
                throw new ParserException("Invalid name-value pair list separator!");
            }
        }

        return elements;
    }

    private KeyValuePair getPair() {
        String name = scanner.next().trim();
        if (!name.matches("\"[^\"]*\"")) {
            System.out.println(scanner.next());
            throw new ParserException("Invalid key-value pair key name!");
        }
        name = name.replaceAll("\"", "");

        checkNextToken(":", "No ':' symbol after key-value pair name!");

        return getPairBody(name);
    }

    private KeyValuePair getPairBody(String name) {
        if (scanner.hasNext("\\{")) {
            List<KeyValuePair> elements = getPairList();

            if (elements.size() == 0) {
                return new KeyValuePair(name, new StringElementary(""), Collections.emptyMap());
            }

            return constructPair(name, elements);
        } else if (scanner.hasNext("\\[")) {
            var array =  getArray();

            if (array.getComponents().size() == 0) {
                return new KeyValuePair(name, new StringElementary(""), Collections.emptyMap());
            }

            return new KeyValuePair(name, array, Collections.emptyMap());
        } else {
            String content = scanner.next().trim();
            if (content.equals("null")) {
                return new KeyValuePair(name, new NullElementary(), Collections.emptyMap());
            } else {
                if (!(content.matches("\"[^\"]*\"|true|false") || isDouble(content))) {
                    throw new ParserException("Invalid content type");
                }
                return new KeyValuePair(name, new StringElementary(content.replaceAll("\"", "")),
                        Collections.emptyMap());
            }
        }
    }

    private ArrayObj.ArrayComponent getArrayComponent() {
        var pair = getPairBody("element");
        return new ArrayObj.ArrayComponent(pair.getAttributes(), pair.getContent());
    }

    private ArrayObj getArray() {
        checkNextToken("[", "Array doesn't start with '['!");

        if (scanner.hasNext("]")){
            scanner.next();
            return new ArrayObj(Collections.emptyList());
        }

        List<ArrayObj.ArrayComponent> elements = new ArrayList<>();

        while (true) {
            elements.add(getArrayComponent());

            String token = scanner.next();

            if (token.equals("]")) {
                break;
            } else if (!token.equals(",")) {
                throw new ParserException("Invalid array separator!");
            }
        }

        return new ArrayObj(elements);
    }

    private KeyValuePair constructPair(String name, List<KeyValuePair> elements) {
        boolean hasAttributes = elements.stream()
                .allMatch(elem -> {
                    String elemName = elem.getName();
                    return (elemName.matches("@[^@]+") && elem.getAttributes().size() == 0
                                                            && !(elem.getContent() instanceof PairListObj ||
                                                                    elem.getContent() instanceof ArrayObj))
                            || (elemName.equals("#" + name));
                });

        if (hasAttributes && elements.stream().filter(obj -> obj.getName().equals("#" + name)).count() == 1) {
            Map<String, String> attributes = new LinkedHashMap<>();

            for (var elem: elements) {
                if (elem.getName().charAt(0) == '@') {
                    String attrName = elem.getName().replaceAll("@", "");
                    if (attributes.containsKey(attrName)) {
                        throw new ParserException();
                    }

                    String attrContent = elem.getContent() instanceof StringElementary ?
                            ((StringElementary) elem.getContent()).getValue()
                            : "";

                    attributes.put(attrName, attrContent);
                }
            }

            Obj content = elements.stream().filter(obj -> obj.getName().equals("#" + name)).findAny().orElseThrow().getContent();

            return new KeyValuePair(name, content, attributes);
        } else {
            Set<String> elementNames = elements.stream().map(KeyValuePair::getName).collect(Collectors.toSet());

            elements.removeIf(obj -> {
                String objName = obj.getName();
                return (objName.matches("[@#].+") && elementNames.contains(objName.substring(1))
                 || obj.getName().matches("[@#]") || obj.getName().equals(""));
            });

            elements.forEach(elem -> elem.setName(elem.getName().replaceAll("[@#]", "")));

            if (elements.size() == 0) {
                return new KeyValuePair(name, new StringElementary(""), Collections.emptyMap());
            }
            return new KeyValuePair(name, new PairListObj(elements), Collections.emptyMap());
        }
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}