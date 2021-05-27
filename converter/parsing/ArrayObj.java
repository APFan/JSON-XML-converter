package converter.parsing;

import converter.printing.OutputVisitor;

import java.util.List;
import java.util.Map;

public class ArrayObj implements Obj {
    private final List<ArrayComponent> array;

    ArrayObj(List<ArrayComponent> array) {
        this.array = array;
    }

    @Override
    public String getOutput(OutputVisitor strategy) {
        return strategy.getElement(this);
    }

    public List<ArrayComponent> getComponents() {
        return array;
    }

    public static class ArrayComponent {
        private final Map<String, String> attributes;
        private final Obj content;

        ArrayComponent(Map<String, String> attributes, Obj content) {
            this.attributes = attributes;
            this.content = content;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Obj getContent() {
            return content;
        }
    }
}