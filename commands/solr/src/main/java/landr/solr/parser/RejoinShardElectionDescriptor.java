package landr.solr.parser;

import landr.solr.cmd.RejoinShardElection;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class RejoinShardElectionDescriptor extends SolrCommandDescriptor<RejoinShardElection> {

    private static final String NAME = "rejoin-shard-election";

    private static final String COLLECTION_PARAM = "collection";
    private static final String CORE_PARAM = "core";
    private static final String AT_HEAD_PARAM = "at-head";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(CORE_PARAM, true),
            new Argument(AT_HEAD_PARAM, "false")
        );
    }

    public RejoinShardElectionDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public RejoinShardElection buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String core       = getArgumentValue(CORE_PARAM, string, context);
        boolean atHead    = getArgumentBooleanValue(AT_HEAD_PARAM, string, context);
        return new RejoinShardElection(collection, core, atHead);
    }
}
