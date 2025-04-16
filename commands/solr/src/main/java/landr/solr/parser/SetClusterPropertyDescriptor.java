package landr.solr.parser;

import landr.solr.cmd.SetClusterProperty;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

public class SetClusterPropertyDescriptor extends SolrCommandDescriptor<SetClusterProperty> {

    private static final String NAME = "set-cluster-property";

    private static final String NAME_PARAM  = "name";
    private static final String VALUE_PARAM = "value";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(NAME_PARAM, true),
            new Argument(VALUE_PARAM, true)
        );
    }

    public SetClusterPropertyDescriptor() {
        super(SYNTAX);
    }

    @Override
    public SetClusterProperty buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String name  = getArgumentValue(NAME_PARAM, string, context);
        String value = getArgumentValue(VALUE_PARAM, string, context);

        return new SetClusterProperty(name, value);
    }

}
