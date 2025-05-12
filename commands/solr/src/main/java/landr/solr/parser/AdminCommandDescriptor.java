package landr.solr.parser;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;
import landr.solr.cmd.SolrAdminCommand;
import landr.solr.cmd.SolrAdminCommand.AdminCommandBuilder;

import java.util.function.Function;

/**
 * Abstract descriptor for collection level admin commands. All commands support collection
 * name and async parameters.
 */
public abstract class AdminCommandDescriptor<T extends SolrAdminCommand> extends SolrCommandDescriptor<T> {

    public static final String COLLECTION_PARAM = "collection";
    public static final String ASYNC_PARAM = "async";

    /**
     * Argument for collection name. To be added to all admin descriptors.
     */
    public static final Argument COLLECTION_ARGUMENT;

    /**
     * Argument for async flag. To be added to all admin descriptors.
     */
    public static final Argument ASYNC_ARGUMENT;

    static {
        COLLECTION_ARGUMENT = new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME);
        ASYNC_ARGUMENT = new Argument(ASYNC_PARAM, "false");
    }

    protected AdminCommandDescriptor(CommandSyntax syntax) {
        super(syntax);
    }

    protected <B extends AdminCommandBuilder> B parseCommonParams(CommandString string, ParserContext context, Function<String, B> factory)
            throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);

        B builder = factory.apply(collection);

        parseCommonParams(string, context, builder);
        return builder;
    }

    protected void parseCommonParams(CommandString string, ParserContext context, AdminCommandBuilder builder)
    throws CommandParseException {

        CommandSyntax syntax = getSyntax();

        if (syntax.hasArgument(ASYNC_PARAM)) {
            boolean async = getArgumentBooleanValue(ASYNC_PARAM, string, context);
            builder.setAsync(async);
        }
    }
}
