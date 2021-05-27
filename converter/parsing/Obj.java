package converter.parsing;

import converter.printing.OutputStrategy;

public interface Obj {
    String getOutput(OutputStrategy strategy);
}