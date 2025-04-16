package landr.parser;

public class ArgumentFormatException extends CommandParseException {

    ArgumentFormatException(ParserContext context, String name, String error) {
        super(context, buildFormatMessage(name, error));
    }

    /**
     * Build the exception message for a bad format.
     */
    private static String buildFormatMessage(String name, String error) {
        return String.format("argument %s: %s", name, error);
    }
}
