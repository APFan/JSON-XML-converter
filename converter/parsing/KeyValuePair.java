package converter.parsing;

import java.util.Map;

public class KeyValuePair {
    private String name;
    private final Obj content;
    private final Map<String, String> attributes;

    public KeyValuePair(String name, Obj content, Map<String, String> attributes) {
        this.name = name;
        this.content = content;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Obj getContent() {
        return content;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}