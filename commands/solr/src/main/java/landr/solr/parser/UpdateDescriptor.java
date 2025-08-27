package landr.solr.parser;

import landr.solr.cmd.Update;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class UpdateDescriptor extends DataCommandDescriptor<Update> {

    private static final String NAME = "update";

    private static final String COUNT_PARAM       = "count";
    private static final String BATCHES_PARAM     = "batches";
    private static final String COMMIT_PARAM      = "commit";
    private static final String SOFT_COMMIT_PARAM = "soft-commit";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(COUNT_PARAM, "1"),
            new Argument(BATCHES_PARAM, "1"),
            new Argument(COMMIT_PARAM, "false"),
            new Argument(SOFT_COMMIT_PARAM, "false")
        );
    }

    public UpdateDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public Update buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        Update.Builder builder = parseCommonParams(string, context, Update.Builder::new);

        int count = getArgumentIntegerValue(COUNT_PARAM, string, context);
        builder.setCount(count);

        int batches = getArgumentIntegerValue(BATCHES_PARAM, string, context);
        builder.setBatches(batches);

        boolean commit = getArgumentBooleanValue(COMMIT_PARAM, string, context);
        builder.setCommit(commit);

        boolean softCommit = getArgumentBooleanValue(SOFT_COMMIT_PARAM, string, context);
        builder.setSoftCommit(softCommit);

        return new Update(builder);
    }

}
