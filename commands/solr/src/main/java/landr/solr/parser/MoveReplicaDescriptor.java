package landr.solr.parser;

import landr.solr.cmd.MoveReplica;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

/**
 * Simple command to move a replica from a Solr node to another.
 */
public class MoveReplicaDescriptor extends SolrCommandDescriptor<MoveReplica> {

    private static final String NAME = "move-replica";

    private static final String COLLECTION_PARAM = "collection";
    private static final String REPLICA_PARAM    = "replica";
    private static final String NODE_PARAM       = "node";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(REPLICA_PARAM, true),
            new Argument(NODE_PARAM, true)
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

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String replica = getArgumentValue(REPLICA_PARAM, string, context);
        String node = getArgumentValue(NODE_PARAM, string, context);

        return new MoveReplica(collection, replica, node);
    }
}
