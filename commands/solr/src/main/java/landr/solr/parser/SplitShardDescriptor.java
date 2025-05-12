package landr.solr.parser;

import landr.solr.cmd.SplitShard;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

public class SplitShardDescriptor extends AdminCommandDescriptor<SplitShard> {

    private static final String NAME = "split-shard";

    private static final String SHARD_PARAM = "shard";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            COLLECTION_ARGUMENT,
            new Argument(SHARD_PARAM, true),
            ASYNC_ARGUMENT
        );
    }

    public SplitShardDescriptor() {
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
    public SplitShard buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        SplitShard.Builder builder = parseCommonParams(string, context, SplitShard.Builder::new);

        String shard = getArgumentValue(SHARD_PARAM, string, context);
        builder.setShard(shard);

        return new SplitShard(builder);
    }
}
