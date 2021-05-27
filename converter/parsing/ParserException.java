package converter.parsing;

public class ParserException extends RuntimeException {
    public ParserException(String text) {
        super(text);
    }
    public ParserException() {
    }
}