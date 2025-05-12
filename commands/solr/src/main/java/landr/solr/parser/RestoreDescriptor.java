package landr.solr.parser;

import landr.solr.cmd.Restore;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

/**
 * Descriptor for command to restore a collection from repository.
 */
public class RestoreDescriptor extends AdminCommandDescriptor<Restore> {

    private static final String NAME = "restore";

    private static final String NAME_PARAM = "name";
    private static final String REPOSITORY_PARAM = "repository";
    private static final String LOCATION_PARAM = "location";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME,
            COLLECTION_ARGUMENT,
            new Argument(NAME_PARAM, true),
            new Argument(REPOSITORY_PARAM, false),
            new Argument(LOCATION_PARAM, false),
            new Argument(ASYNC_PARAM, "false"),
            ASYNC_ARGUMENT
        );
    }

    public RestoreDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public Restore buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        Restore.Builder builder = parseCommonParams(string, context, Restore.Builder::new);

        String name = getArgumentValue(NAME_PARAM, string, context);
        builder.setBackupName(name);

        String repository = getArgumentValue(REPOSITORY_PARAM, string, context);
        builder.setRepository(repository);

        String location = getArgumentValue(LOCATION_PARAM, string, context);
        builder.setLocation(location);

        return new Restore(builder);
    }
}
