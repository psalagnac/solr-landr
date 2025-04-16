package landr.parser;

import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

/**
 * Simple command to print a string in the output.
 *
 */
class EchoCommandDescriptor extends CommandDescriptor<EchoCommand> {

    private static final String NAME = "echo";
    private static final String MESSAGE_PARAM = "message";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME, MESSAGE_PARAM,
            new Argument(MESSAGE_PARAM, "")
        );
    }

    EchoCommandDescriptor() {
        super(SYNTAX);
    }

    @Override
    public EchoCommand buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String message = getArgumentValue(MESSAGE_PARAM, string, context);
        return new EchoCommand(message);
    }
}
