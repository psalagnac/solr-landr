package landr.parser;

class UnknownPragmaException extends CommandParseException {

    UnknownPragmaException(ParserContext context, String pragma) {
        super(context, String.format("no such pragma %s", pragma));
    }

}
