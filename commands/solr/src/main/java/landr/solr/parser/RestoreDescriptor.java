package landr.solr.parser;

import landr.solr.cmd.Restore;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

/**
 * Descriptor for command to restore a collection from repository.
 */
public class RestoreDescriptor extends SolrCommandDescriptor<Restore> {

    private static final String NAME = "restore";

    private static final String COLLECTION_PARAM = "collection";
    private static final String NAME_PARAM = "name";
    private static final String REPOSITORY_PARAM = "repository";
    private static final String LOCATION_PARAM = "location";
    private static final String ASYNC_PARAM = "async";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(NAME_PARAM, true),
            new Argument(REPOSITORY_PARAM, false),
            new Argument(LOCATION_PARAM, false),
            new Argument(ASYNC_PARAM, "false")
        );
    }

    public RestoreDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public Restore buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String name = getArgumentValue(NAME_PARAM, string, context);
        String repository = getArgumentValue(REPOSITORY_PARAM, string, context);
        String location = getArgumentValue(LOCATION_PARAM, string, context);
        boolean async = getArgumentBooleanValue(ASYNC_PARAM, string, context);

        return new Restore(collection, name, repository, location, async);
    }
}
