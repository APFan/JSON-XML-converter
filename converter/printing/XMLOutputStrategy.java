package converter.printing;

import converter.parsing.*;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLOutputStrategy implements OutputStrategy {
    @Override
    public String getElement(KeyValuePair keyValuePair) {
        StringBuilder builder = new StringBuilder("<" + keyValuePair.getName());
        if (keyValuePair.getAttributes().size() > 0) {
            builder.append(' ').append(keyValuePair.getAttributes().entrySet().stream()
                    .map(this::getElement)
                    .collect(Collectors.joining(" ")));
        }

        if (keyValuePair.getContent() instanceof NullElementary) {
            builder.append(" />\n");
        } else {
            builder.append(">");

            String content = keyValuePair.getContent().getOutput(this);
            if (content.length() > 0) {
                if (content.charAt(0) == '<') {
                    builder.append("\n    ");
                }
                builder.append(content.replaceAll("\n(?=[^$])", "\n    "));
            }

            builder.append("</").append(keyValuePair.getName()).append(">\n");
        }

        return builder.toString();
    }

    @Override
    public String getElement(NullElementary elementary) {
        return "";
    }

    @Override
    public String getElement(StringElementary elementary) {
        return elementary.getValue();
    }

    @Override
    public String getElement(Tree tree) {
        if (tree.getElements().size() == 0) {
            return "";
        }
        if (tree.getElements().size() > 1) {
            return getElement(new KeyValuePair("root", new PairListObj(tree.getElements()),
                    Collections.emptyMap())).stripTrailing();
        } else {
            return getElement(tree.getElements().get(0)).stripTrailing();
        }
    }

    @Override
    public String getElement(PairListObj pairListObj) {
        return pairListObj.getContent().stream().map(this::getElement).collect(Collectors.joining());
    }

    @Override
    public String getElement(Map.Entry<String, String> attribute) {
        return attribute.getKey() + " = \""
                + attribute.getValue() + "\"";
    }

    @Override
    public String getElement(ArrayObj arrayObj) {
        var pairList =  arrayObj.getComponents().stream().map(comp -> new KeyValuePair("element",
                comp.getContent(), comp.getAttributes()))
                .collect(Collectors.toList());
        return getElement(new PairListObj(pairList));
    }
}