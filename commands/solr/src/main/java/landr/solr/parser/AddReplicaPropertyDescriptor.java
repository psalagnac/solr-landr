package landr.solr.parser;

import landr.solr.cmd.AddReplicaProperty;

import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.CommandParseException;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class AddReplicaPropertyDescriptor extends SolrCommandDescriptor<AddReplicaProperty> {

    private static final String NAME = "add-replica-property";

    private static final String COLLECTION_PARAM = "collection";
    private static final String SHARD_PARAM      = "shard";
    private static final String REPLICA_PARAM    = "replica";
    private static final String NAME_PARAM       = "name";
    private static final String VALUE_PARAM      = "value";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(SHARD_PARAM, true),
            new Argument(REPLICA_PARAM, true),
            new Argument(NAME_PARAM, true),
            new Argument(VALUE_PARAM, true)
        );
    }


    public AddReplicaPropertyDescriptor() {
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
    public AddReplicaProperty buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String shard      = getArgumentValue(SHARD_PARAM, string, context);
        String replica    = getArgumentValue(REPLICA_PARAM, string, context);
        String name       = getArgumentValue(NAME_PARAM, string, context);
        String value      = getArgumentValue(VALUE_PARAM, string, context);

        return new AddReplicaProperty(collection, shard, replica, name, value);
    }
}
