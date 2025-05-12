package landr.solr.parser;

import landr.cmd.Command;
import landr.parser.CommandParseException;
import landr.parser.CommandParser;
import landr.parser.CommandRegistry;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.solr.cmd.SolrAdminCommand;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Generic test for pasring of Solr admin commands.
 */
public abstract class AdminDescriptorTestBase {

    /**
     * Generic test to check we fail if collection parameter was not specified.
     */
    @Test
    public void testMissingCollection()  {

        String name = createDescriptor().getName();

        CommandParseException exception = assertThrows(CommandParseException.class,
                () -> parseSingleCommand(name, SolrAdminCommand.class));

        String message = String.format("missing required argument: %s", getCollectionParameterName());
        assertEquals(message, exception.getMessage());
    }

    /**
     * Generate a minimal statement with required arguments only to make sure we can parse the command.
     */
    @Test
    public void testMinimalStatement() throws CommandParseException {

        AdminCommandDescriptor<?> descriptor = createDescriptor();
        CommandSyntax syntax = descriptor.getSyntax();

        StringBuilder statement = new StringBuilder();
        statement.append(syntax.getName());

        for (String argName : syntax.getArgumentNames()) {
            Argument arg = syntax.getArgument(argName);

            if (arg.isRequired()) {
                statement.append(" -").append(argName);
                statement.append(" ");
                statement.append(argName.charAt(0));
            }
        }

        Command command = parseSingleCommand(statement.toString(), SolrAdminCommand.class);
        assertNotNull(command);
    }

    /**
     * Utility method to parse a single statement.
     */
    protected <T extends SolrAdminCommand> T parseSingleCommand(String statement, Class<T> clazz) throws CommandParseException {

        CommandRegistry registry = createRegistry();
        CommandParser parser = new CommandParser(registry);

        StringReader reader = new StringReader(statement);
        try {
            List<Command> commands = parser.parse(reader);

            assertEquals("single command expected", 1, commands.size());
            Command command = commands.get(0);

            assertThat("not an admin command", command, instanceOf(clazz));
            return clazz.cast(command);

        } catch (IOException e) {
            // should not happen
            fail(e.getMessage());
            return null;
        }
    }

    /**
     * Create a command registry with all the commands and pragmas for the tests.
     */
    private CommandRegistry createRegistry() {
        CommandRegistry registry = new CommandRegistry();
        registry.registerCommand(createDescriptor());
        return registry;
    }

    /**
     * Some commands might have a different name for the collection parameter. In this case, this
     * method should be overridden to support genetic tests.
     */
    protected String getCollectionParameterName() {
        return AdminCommandDescriptor.COLLECTION_PARAM;
    }

    /**
     * Creates the specialized instance of the descriptor to be tested.
     */
    protected abstract AdminCommandDescriptor<?> createDescriptor();

}
