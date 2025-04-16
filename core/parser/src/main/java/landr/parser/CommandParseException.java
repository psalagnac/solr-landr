package landr.parser;

public class CommandParseException extends Exception {

    private final ParserContext context;

    public CommandParseException(ParserContext context, String message) {
        super(message);

        this.context = context;
    }

    public CommandParseException(ParserContext context, String message, Throwable cause) {
        super(message, cause);

        this.context = context;
    }
}
