package converter.parsing;

import converter.printing.OutputVisitor;

public interface Obj {
    String getOutput(OutputVisitor strategy);
}