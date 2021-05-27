package converter.parsing;

import converter.printing.OutputStrategy;

public class NullElementary implements Obj {
    @Override
    public String getOutput(OutputStrategy strategy) {
        return strategy.getElement(this);
    }
}