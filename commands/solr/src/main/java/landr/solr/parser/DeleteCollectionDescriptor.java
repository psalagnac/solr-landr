package landr.solr.parser;

import landr.solr.cmd.DeleteCollection;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class DeleteCollectionDescriptor extends SolrCommandDescriptor<DeleteCollection> {

    private static final String NAME = "delete-collection";

    private static final String COLLECTION_PARAM = "name";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_PARAM,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME)
        );
    }

    public DeleteCollectionDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public DeleteCollection buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        return new DeleteCollection(collection);
    }
}
