package converter.parsing;

import converter.printing.OutputVisitor;

import java.util.List;

public class PairListObj implements Obj {

    private final List<KeyValuePair> content;

    public PairListObj(List<KeyValuePair> content) {
        this.content = content;
    }

    @Override
    public String getOutput(OutputVisitor strategy) {
        return strategy.getElement(this);
    }

    public List<KeyValuePair> getContent() {
        return content;
    }
}