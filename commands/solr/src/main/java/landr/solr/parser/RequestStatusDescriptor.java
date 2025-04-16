package landr.solr.parser;

import landr.solr.cmd.RequestStatus;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

/**
 * Syntax descriptor for {@link RequestStatus}.
 */
public class RequestStatusDescriptor extends SolrCommandDescriptor<RequestStatus> {

    private static final String NAME = "request-status";

    private static final String KEY_PARAM  = "key";
    private static final String KEEP_PARAM = "keep";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(KEY_PARAM, true),
            new Argument(KEEP_PARAM, "false")
        );
    }

    public RequestStatusDescriptor() {
        super(SYNTAX);
    }

    @Override
    public RequestStatus buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String key = getArgumentValue(KEY_PARAM, string, context);
        boolean keep = getArgumentBooleanValue(KEEP_PARAM, string, context);

        return new RequestStatus(key, keep);
    }
}
