package converter.parsing;

import converter.printing.OutputStrategy;

import java.util.List;

public class PairListObj implements Obj {

    private final List<KeyValuePair> content;

    public PairListObj(List<KeyValuePair> content) {
        this.content = content;
    }

    @Override
    public String getOutput(OutputStrategy strategy) {
        return strategy.getElement(this);
    }

    public List<KeyValuePair> getContent() {
        return content;
    }
}