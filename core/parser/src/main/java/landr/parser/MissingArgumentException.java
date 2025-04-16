package landr.parser;

public class MissingArgumentException extends CommandParseException {

    MissingArgumentException(ParserContext context, String argument) {
        super(context, String.format("missing required argument: %s", argument));
    }

}
