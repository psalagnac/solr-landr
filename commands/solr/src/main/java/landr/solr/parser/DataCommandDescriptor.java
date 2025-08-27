package landr.solr.parser;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.CommandSyntax;
import landr.solr.cmd.SolrDataCommand.DataCommandBuilder;
import landr.solr.cmd.SolrDataCommand;

import java.util.function.Function;

/**
 * Abstract descriptor for collection level data commands.
 */
public abstract class DataCommandDescriptor<T extends SolrDataCommand> extends SolrCommandDescriptor<T> {

    public static final String COLLECTION_PARAM = "collection";

    protected DataCommandDescriptor(CommandSyntax syntax) {
        super(syntax);
    }

    protected <B extends DataCommandBuilder> B parseCommonParams(CommandString string, ParserContext context, Function<String, B> factory)
            throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);

        return factory.apply(collection);
    }
}
