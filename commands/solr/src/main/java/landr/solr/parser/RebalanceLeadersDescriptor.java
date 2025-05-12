package landr.solr.parser;

import landr.solr.cmd.RebalanceLeaders;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

public class RebalanceLeadersDescriptor extends AdminCommandDescriptor<RebalanceLeaders> {

    private static final String NAME = "rebalance-leaders";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            COLLECTION_ARGUMENT,
            ASYNC_ARGUMENT
        );
    }

    public RebalanceLeadersDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public RebalanceLeaders buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        RebalanceLeaders.Builder builder = parseCommonParams(string, context, RebalanceLeaders.Builder::new);

        return new RebalanceLeaders(builder);
    }
}
