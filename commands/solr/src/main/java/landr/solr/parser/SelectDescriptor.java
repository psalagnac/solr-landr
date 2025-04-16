package landr.solr.parser;

import landr.solr.cmd.Select;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

import java.util.Map;

public class SelectDescriptor extends SolrCommandDescriptor<Select> {

    private static final String NAME = "select";

    private static final String COLLECTION_PARAM = "collection";
    private static final String QUERY_PARAM = "query";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, QUERY_PARAM,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(QUERY_PARAM, "*")
        );
    }

    public SelectDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public Select buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String query = getArgumentValue(QUERY_PARAM, string, context);

        return new Select(collection, query);
    }

}
