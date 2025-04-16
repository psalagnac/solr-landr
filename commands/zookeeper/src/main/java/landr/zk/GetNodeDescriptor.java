package landr.zk;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

/**
 * Command to print the content of a ZK node.
 */
class GetNodeDescriptor extends ZkCommandDescriptor<GetNode>  {

    private static final String NAME = "zk-get";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
                NAME, PATH_PARAM,
                new Argument(PATH_PARAM, true)
        );
    }

    GetNodeDescriptor() {
        super(SYNTAX);
    }

    @Override
    public GetNode buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String path = getArgumentValue(PATH_PARAM, string, context);

        return new GetNode(path);

    }
}
