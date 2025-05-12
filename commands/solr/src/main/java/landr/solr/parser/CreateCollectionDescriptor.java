package landr.solr.parser;

import landr.solr.cmd.CreateCollection;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;

public class CreateCollectionDescriptor extends AdminCommandDescriptor<CreateCollection> {

    private static final String NAME = "create-collection";

    private static final String COLLECTION_PARAM = "name";
    private static final String CONFIG_SET_PARAM = "config";
    private static final String ROUTER_PARAM = "router";
    private static final String SHARDS_PARAM = "shards";
    private static final String TYPE_PARAM = "type";
    private static final String REPLICAS_PARAMS = "replicas";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COLLECTION_PARAM,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(CONFIG_SET_PARAM),
            new Argument(ROUTER_PARAM),
            new Argument(SHARDS_PARAM, "1"),
            new Argument(REPLICAS_PARAMS, "1"),
            new Argument(TYPE_PARAM, CreateCollection.Type.NRT.name()),
            new Argument(ASYNC_PARAM, "false"),
            ASYNC_ARGUMENT
        );
    }

    public CreateCollectionDescriptor() {
        super(SYNTAX);
    }

    @Override
    public CreateCollection buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String collection = getArgumentValue(COLLECTION_PARAM, string, context);
        CreateCollection.Builder builder = new CreateCollection.Builder(collection);

        parseCommonParams(string, context, builder);

        String config = getArgumentValue(CONFIG_SET_PARAM, string, context);
        builder.setConfig(config);

        String router = getArgumentValue(ROUTER_PARAM, string, context);
        builder.setRouter(router);

        int shards = getArgumentIntegerValue(SHARDS_PARAM, string, context);
        builder.setShards(shards);

        int replicas = getArgumentIntegerValue(REPLICAS_PARAMS, string, context);
        builder.setReplicas(replicas);

        CreateCollection.Type type = getArgumentEnumValue(TYPE_PARAM, string, context, CreateCollection.Type.class);
        builder.setType(type);

        return new CreateCollection(builder);
    }

}
