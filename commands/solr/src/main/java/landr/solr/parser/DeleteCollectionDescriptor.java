package landr.solr.parser;

import landr.solr.cmd.DeleteCollection;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

public class DeleteCollectionDescriptor extends AdminCommandDescriptor<DeleteCollection> {

    private static final String NAME = "delete-collection";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_PARAM,
            COLLECTION_ARGUMENT,
            ASYNC_ARGUMENT
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

        DeleteCollection.Builder builder = parseCommonParams(string, context, DeleteCollection.Builder::new);

        return new DeleteCollection(builder);
    }
}
