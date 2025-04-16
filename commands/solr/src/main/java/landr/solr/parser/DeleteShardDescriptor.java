package landr.solr.parser;

import landr.solr.cmd.DeleteShard;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

/**
 * Descriptor for command to delete an inactive shard.
 */
public class DeleteShardDescriptor extends SolrCommandDescriptor<DeleteShard> {

    private static final String NAME = "delete-shard";

    private static final String COLLECTION_PARAM = "collection";
    private static final String SHARD_PARAM = "shard";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(SHARD_PARAM, true)
        );
    }

    public DeleteShardDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION,
            SHARD_PARAM, ClusterStateCompleter.CompletionType.SHARD
        );
    }

    @Override
    public DeleteShard buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String shard = getArgumentValue(SHARD_PARAM, string, context);

        return new DeleteShard(collection, shard);
    }
}
