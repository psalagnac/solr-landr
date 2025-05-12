package landr.solr.parser;

import landr.solr.cmd.MoveReplica;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

/**
 * Simple command to move a replica from a Solr node to another.
 */
public class MoveReplicaDescriptor extends AdminCommandDescriptor<MoveReplica> {

    private static final String NAME = "move-replica";

    private static final String REPLICA_PARAM    = "replica";
    private static final String NODE_PARAM       = "node";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME,
            COLLECTION_ARGUMENT,
            new Argument(REPLICA_PARAM, true),
            new Argument(NODE_PARAM, true),
            ASYNC_ARGUMENT
        );
    }

    public MoveReplicaDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION,
            REPLICA_PARAM, ClusterStateCompleter.CompletionType.REPLICA,
            NODE_PARAM, ClusterStateCompleter.CompletionType.NODE
        );
    }

    @Override
    public MoveReplica buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        MoveReplica.Builder builder = parseCommonParams(string, context, MoveReplica.Builder::new);

        String replica = getArgumentValue(REPLICA_PARAM, string, context);
        builder.setReplica(replica);

        String node = getArgumentValue(NODE_PARAM, string, context);
        builder.setNode(node);

        return new MoveReplica(builder);
    }
}
