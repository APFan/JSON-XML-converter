package converter.parsing;

import converter.printing.OutputStrategy;

public class StringElementary implements Obj {
    private final String value;

    public StringElementary(String value) {
        this.value = value;
    }

    @Override
    public String getOutput(OutputStrategy strategy) {
        return strategy.getElement(this);
    }

    public String getValue() {
        return value;
    }
}