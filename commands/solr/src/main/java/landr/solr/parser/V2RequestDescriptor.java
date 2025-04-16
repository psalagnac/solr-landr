package landr.solr.parser;

import landr.solr.cmd.V2RequestCommand;
import landr.parser.CommandDescriptor;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

public class V2RequestDescriptor extends CommandDescriptor<V2RequestCommand> {

    private static final String NAME = "v2-request";

    private static final String PAYLOAD_PARAM = "payload";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(PAYLOAD_PARAM, true));
    }

    public V2RequestDescriptor() {
        super(SYNTAX);
    }

    @Override
    public V2RequestCommand buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String payload = getArgumentValue(PAYLOAD_PARAM, string, context);

        return new V2RequestCommand("/node", payload);
    }
}
