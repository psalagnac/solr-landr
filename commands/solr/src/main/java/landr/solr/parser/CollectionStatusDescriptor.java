package landr.solr.parser;

import landr.solr.cmd.CollectionStatus;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class CollectionStatusDescriptor extends SolrCommandDescriptor<CollectionStatus> {

    private static final String NAME = "collection-status";

    private static final String COLLECTION_PARAM = "name";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_PARAM,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME)
        );
    }

    public CollectionStatusDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public CollectionStatus buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        return new CollectionStatus(collection);
    }

}
