package landr.parser;

import landr.parser.syntax.CommandSyntax;

/**
 * The descriptor does something, it creates a command that does nothing.
 */
class NullCommandDescriptor extends CommandDescriptor<NullCommand> {

    private static final String NAME = "no-op";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME);
    }

    NullCommandDescriptor() {
        super(SYNTAX);
    }

    @Override
    public NullCommand buildCommand(CommandString string, ParserContext context) throws CommandParseException {
        return new NullCommand();
    }

}
