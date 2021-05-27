package converter.printing;

import converter.parsing.*;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonOutputVisitor implements OutputVisitor {
    @Override
    public String getElement(KeyValuePair keyValuePair) {
        return "\"" + keyValuePair.getName() + "\" : " +
                getBody(keyValuePair);
    }

    private String getBody(KeyValuePair pair) {
        StringBuilder builder = new StringBuilder();

        if (pair.getAttributes().size() > 0) {
            builder.append("{\n    ");
            builder.append(pair.getAttributes().entrySet().stream()
                    .map(attr -> getElement(attr) + ",\n    ").collect(Collectors.joining()))
                    .append("\"#").append(pair.getName()).append("\" : ")
                    .append(pair.getContent().getOutput(this)
                            .replaceAll("\n(?=[^$])", "\n    "))
                    .append("\n}");
            return builder.toString();
        }

        builder.append(pair.getContent().getOutput(this));
        return builder.toString();
    }

    @Override
    public String getElement(NullElementary elementary) {
        return "null";
    }

    @Override
    public String getElement(StringElementary elementary) {
        return '\"' + elementary.getValue() + '\"';
    }

    @Override
    public String getElement(Tree tree) {
        return "{\n    " +
                tree.getElements().stream().map(this::getElement).collect(Collectors.joining(",\n"))
                    .replaceAll("\n(?=[^$])", "\n    ") +
                "\n}";
    }

    @Override
    public String getElement(PairListObj pairListObj) {
        return "{\n    " +
                pairListObj.getContent().stream().map(this::getElement).collect(Collectors.joining(",\n"))
                        .replaceAll("\n(?=[^$])", "\n    ") +
                "\n}";
    }

    @Override
    public String getElement(Map.Entry<String, String> attribute) {
        return "\"@" + attribute.getKey() + "\": \"" + attribute.getValue() + "\"";
    }

    @Override
    public String getElement(ArrayObj arrayObj) {
        StringBuilder builder = new StringBuilder("[");

        if (arrayObj.getComponents().size() == 0) {
            builder.append(" ]\n");
            return builder.toString();
        }

        builder.append("\n    ").append(arrayObj.getComponents().stream().map(comp ->
                            new KeyValuePair("element", comp.getContent(),comp.getAttributes()))
                .map(this::getBody).collect(Collectors.joining(",\n"))
                .replaceAll("\n(?=[^$])", "\n    ")
        )
        .append("\n]");
        return builder.toString();
    }
}