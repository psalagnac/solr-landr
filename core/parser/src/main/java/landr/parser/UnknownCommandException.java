package landr.parser;

class UnknownCommandException extends CommandParseException {

    UnknownCommandException(ParserContext context, String command) {
        super(context, String.format("no such command %s", command));
    }

}
