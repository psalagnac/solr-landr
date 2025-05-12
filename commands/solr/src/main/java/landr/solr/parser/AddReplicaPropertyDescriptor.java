package landr.solr.parser;

import landr.solr.cmd.AddReplicaProperty;

import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.CommandParseException;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

public class AddReplicaPropertyDescriptor extends AdminCommandDescriptor<AddReplicaProperty> {

    private static final String NAME = "add-replica-property";

    private static final String SHARD_PARAM      = "shard";
    private static final String REPLICA_PARAM    = "replica";
    private static final String NAME_PARAM       = "name";
    private static final String VALUE_PARAM      = "value";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            COLLECTION_ARGUMENT,
            new Argument(SHARD_PARAM, true),
            new Argument(REPLICA_PARAM, true),
            new Argument(NAME_PARAM, true),
            new Argument(VALUE_PARAM, true),
            ASYNC_ARGUMENT
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

        AddReplicaProperty.Builder builder = parseCommonParams(string, context, AddReplicaProperty.Builder::new);

        String shard = getArgumentValue(SHARD_PARAM, string, context);
        builder.setShard(shard);

        String replica = getArgumentValue(REPLICA_PARAM, string, context);
        builder.setReplica(replica);

        String name = getArgumentValue(NAME_PARAM, string, context);
        builder.setName(name);

        String value = getArgumentValue(VALUE_PARAM, string, context);
        builder.setValue(value);

        return new AddReplicaProperty(builder);
    }
}
