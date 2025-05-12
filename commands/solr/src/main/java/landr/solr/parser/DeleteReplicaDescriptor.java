package landr.solr.parser;

import landr.solr.cmd.DeleteReplica;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

public class DeleteReplicaDescriptor extends AdminCommandDescriptor<DeleteReplica> {

    private static final String NAME = "delete-replica";

    private static final String SHARD_PARAM      = "shard";
    private static final String REPLICA_PARAM    = "replica";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_ARGUMENT,
            new Argument(SHARD_PARAM, true),
            new Argument(REPLICA_PARAM, true),
            ASYNC_ARGUMENT
        );
    }

    public DeleteReplicaDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION,
            SHARD_PARAM, ClusterStateCompleter.CompletionType.SHARD,
            REPLICA_PARAM, ClusterStateCompleter.CompletionType.REPLICA
        );
    }

    @Override
    public DeleteReplica buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        DeleteReplica.Builder builder = parseCommonParams(string, context, DeleteReplica.Builder::new);

        String shard = getArgumentValue(SHARD_PARAM, string, context);
        builder.setShard(shard);

        String replica = getArgumentValue(REPLICA_PARAM, string, context);
        builder.setReplica(replica);

        return new DeleteReplica(builder);
    }

}
