package landr.solr.parser;

import landr.solr.cmd.Backup;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

/**
 * Descriptor for command to back up a collection.
 */
public class BackupDescriptor extends AdminCommandDescriptor<Backup> {

    private static final String NAME = "backup";

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
            ASYNC_ARGUMENT
     );
    }

    public BackupDescriptor() {
        super(SYNTAX);
    }

    @Override
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return Map.of(
            COLLECTION_PARAM, ClusterStateCompleter.CompletionType.COLLECTION
        );
    }

    @Override
    public Backup buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        Backup.Builder builder = parseCommonParams(string, context, Backup.Builder::new);

        String name = getArgumentValue(NAME_PARAM, string, context);
        builder.setName(name);

        String repository = getArgumentValue(REPOSITORY_PARAM, string, context);
        builder.setRepository(repository);

        String location = getArgumentValue(LOCATION_PARAM, string, context);
        builder.setLocation(location);

        return new Backup(builder);
    }

}
