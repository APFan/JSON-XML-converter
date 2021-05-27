package converter.parsing;

import java.util.*;

public abstract class Parser {
    protected final Scanner scanner;

    Parser(String input) {
        scanner = new Scanner(input);
    }

    public abstract Tree getTree();

    protected void checkNextToken(String token, String failMessage) {
        if (!scanner.hasNext()) {
            throw new ParserException("Unexpected EOF!");
        }
        if (!scanner.next().equals(token)) {
            throw new ParserException(failMessage);
        }
    }
}