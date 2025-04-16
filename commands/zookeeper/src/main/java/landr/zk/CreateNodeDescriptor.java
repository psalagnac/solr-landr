package landr.zk;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

class CreateNodeDescriptor extends ZkCommandDescriptor<CreateNode> {

    private static final String NAME = "zk-create";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, PATH_PARAM,
            new Argument(PATH_PARAM, true)
        );
    }

    CreateNodeDescriptor() {
        super(SYNTAX);
    }

    @Override
    public CreateNode buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String path = getArgumentValue(PATH_PARAM, string, context);

        return new CreateNode(path);

    }

}
