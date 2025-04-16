package landr.solr.parser;

import landr.solr.cmd.CreateCollection;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

public class CreateCollectionDescriptor extends SolrCommandDescriptor<CreateCollection> {

    private static final String NAME = "create-collection";

    private static final String COLLECTION_PARAM = "name";
    private static final String CONFIG_SET_PARAM = "config";
    private static final String SHARDS_PARAM = "shards";
    private static final String TYPE_PARAM = "type";
    private static final String REPLICAS_PARAMS = "replicas";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_PARAM,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(CONFIG_SET_PARAM),
            new Argument(SHARDS_PARAM, "1"),
            new Argument(TYPE_PARAM, CreateCollection.Type.NRT.name()),
            new Argument(REPLICAS_PARAMS, "1")
        );
    }

    public CreateCollectionDescriptor() {
        super(SYNTAX);
    }

    @Override
    public CreateCollection buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        String config = getArgumentValue(CONFIG_SET_PARAM, string, context);
        int shards = getArgumentIntegerValue(SHARDS_PARAM, string, context);
        CreateCollection.Type type = getArgumentEnumValue(TYPE_PARAM, string, context, CreateCollection.Type.class);
        int replicas = getArgumentIntegerValue(REPLICAS_PARAMS, string, context);

        return new CreateCollection(collection, config, shards, type, replicas);
    }

}
