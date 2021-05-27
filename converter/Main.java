package converter;

import converter.parsing.*;
import converter.printing.JsonOutputVisitor;
import converter.printing.XMLOutputVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main *FILENAME*");
        } else {
            try {
                String input = String.join("\n", Files.readAllLines(Path.of(args[0])));
                try {
                    if (input.length() != 0) {
                        if (input.charAt(0) == '<') {
                            Tree tree = new XMLParser(input).getTree();
                            System.out.println(tree.getRepresentation(new JsonOutputVisitor()));
                        } else {
                            Tree tree = new JsonParser(input).getTree();
                            System.out.println(tree.getRepresentation(new XMLOutputVisitor()));
                        }
                    } else {
                        System.out.println("Empty file!");
                    }
                } catch (ParserException e) {
                    System.out.println(e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}