package landr.zk;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

/**
 * Syntax for {@link ListNodes}.
 */
class ListNodesDescriptor extends ZkCommandDescriptor<ListNodes> {

    private static final String NAME = "zk-ls";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, PATH_PARAM,
            new Argument(PATH_PARAM, true)
        );
    }

    protected ListNodesDescriptor() {
        super(SYNTAX);
    }

    @Override
    public ListNodes buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String path = getArgumentValue(PATH_PARAM, string, context);

        return new ListNodes(path);
    }
}
