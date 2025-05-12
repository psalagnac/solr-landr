package landr.solr.parser;

import landr.solr.cmd.AddReplica;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import org.apache.solr.common.cloud.Replica;

import java.util.Map;

public class AddReplicaDescriptor extends AdminCommandDescriptor<AddReplica> {

    private static final String NAME = "add-replica";

    private static final String SHARD_PARAM = "shard";
    private static final String TYPE_PARAM  = "type";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME,
            COLLECTION_ARGUMENT,
            new Argument(SHARD_PARAM, true),
            new Argument(TYPE_PARAM),
            ASYNC_ARGUMENT
        );
    }

    public AddReplicaDescriptor() {
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
    public AddReplica buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        AddReplica.Builder builder = parseCommonParams(string, context, AddReplica.Builder::new);

        String shard = getArgumentValue(SHARD_PARAM, string, context);
        builder.setShard(shard);

        Replica.Type type = getArgumentEnumValue(TYPE_PARAM, string, context, Replica.Type.class);
        builder.setType(type);

        return new AddReplica(builder);
    }
}
