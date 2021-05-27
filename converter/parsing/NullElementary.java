package converter.parsing;

import converter.printing.OutputVisitor;

public class NullElementary implements Obj {
    @Override
    public String getOutput(OutputVisitor strategy) {
        return strategy.getElement(this);
    }
}