package landr.solr.parser;

import landr.solr.cmd.RebalanceLeaders;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class RebalanceLeadersDescriptor extends SolrCommandDescriptor<RebalanceLeaders> {

    private static final String NAME = "rebalance-leaders";

    private static final String COLLECTION_PARAM = "collection";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME)
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

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);

        return new RebalanceLeaders(collection);
    }
}
