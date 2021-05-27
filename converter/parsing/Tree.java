package converter.parsing;

import converter.printing.OutputStrategy;

import java.util.List;

public class Tree {
    private final List<KeyValuePair> elements;

    Tree(List<KeyValuePair> elements) {
        this.elements = elements;
    }

    public String getRepresentation(OutputStrategy strategy) {
        return strategy.getElement(this);
    }

    public List<KeyValuePair> getElements() {
        return elements;
    }
}