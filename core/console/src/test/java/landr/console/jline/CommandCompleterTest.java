package landr.console.jline;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.parser.CommandDescriptor;
import landr.parser.CommandRegistry;
import landr.parser.CommandRegistryFactory;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.DefaultParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CommandCompleterTest {

    /**
     * Check completion for the command name (first token in command line).
     */
    @Test
    public void testSimpleCommandName() {

        CommandRegistry registry = createRegistry(false);
        registerCommand(registry, "delete-shard");
        registerCommand(registry, "delete-replica");

        assertCandidates(
            registry, "delete-s",
            "delete-shard"
        );

        assertCandidates(
            registry, "delete-",
            "delete-shard", "delete-replica"
        );

        // All command names (no pragma is registered)
        assertCandidates(
            registry, "",
            "delete-shard", "delete-replica"
        );
    }

    /**
     * Check we suggest another command name when first statement is completed.
     */
    @Test
    public void testCursorAfterStatement() {
        CommandRegistry registry = createRegistry(false);
        registerCommand(registry, "foo");
        registerCommand(registry, "bar");

        // Simple statement ended with semicolon
        assertCandidates(
            registry, "cmd;",
            "foo", "bar"
        );

        // Simple pragma ended with semicolon
        assertCandidates(
            registry, "@pragma;",
            "foo", "bar"
        );

        // With a space between statements
        assertCandidates(
            registry, "foo; ",
            "foo", "bar"
        );
    }

    /**
     * Check completion when cursor is before entered input.
     */
    @Test
    public void testCursorBeforeBounds() {
        CommandRegistry registry = createRegistry(false);
        registerCommand(registry, "foo");
        registerCommand(registry, "bar");

        // Input has arguments but no command name
        assertCandidates(
            registry, "  -arg value", 0,
            "foo", "bar"
        );

        // Cursor is before arguments, but for the second statement
        assertCandidates(
            registry, "f;  -arg value", 3,
            "foo", "bar"
        );
    }

    /**
     * Test completion of the command name with multiple statements.
     */
    @Test
    public void testMultipleCommandName() {

        CommandRegistry registry = createRegistry(false);
        registerCommand(registry, "cmd-left");
        registerCommand(registry, "cmd-right1");
        registerCommand(registry, "cmd-right2");

        // Complete name of the second command
        assertCandidates(
            registry, "cmd ; cmd-r",
            "cmd-right1", "cmd-right2"
        );

        // Complete the second command with no space between statements
        assertCandidates(
            registry, "cmd;cmd-r",
            "cmd-right1", "cmd-right2"
        );


        // Complete first command, with explicit cursor position
        assertCandidates(
            registry, "cmd ; cmd-r", 3,
            "cmd-left", "cmd-right1", "cmd-right2"
        );
    }

    /**
     * Check completion for pragma names.
     */
    @Test
    public void testPragmaName() {

        CommandRegistry registry = createRegistry(true);
        registerCommand(registry, "no-op");

        // All pragmas
        assertCandidates(
            registry, "@",
            "@debug", "@trace", "@verbose", "@context"
        );

        // Specific pragma name
        assertCandidates(
            registry, "@d",
            "@debug"
        );
    }

    /**
     * Check completion on argument names.
     */
    @Test
    public void testArgumentNames() {

        CommandRegistry registry = createRegistry(false);
        registerCommand(registry, "cmd1", "arg1");
        registerCommand(registry, "cmd2", "arg2", "arg3");

        // Command 1 has a single argument
        assertCandidates(
            registry, "cmd1 -a",
            "-arg1"
        );

        // Command 2 has two arguments
        assertCandidates(
            registry, "cmd2 -a",
            "-arg2", "-arg3"
        );
    }

    private ParsedLine parseJLine(String line, int cursor) {
        Parser parser = new DefaultParser();
        return parser.parse(line, cursor, Parser.ParseContext.COMPLETE);
    }

    /**
     * Create a new registry with no command. Default pragmas are optionally added.
     */
    private CommandRegistry createRegistry(boolean pragmas) {
        CommandRegistryFactory factory = new CommandRegistryFactory(false);
        return factory.create(pragmas);
    }

    private void registerCommand(CommandRegistry registry, String name, String... arguments) {
        CommandDescriptor<?> descriptor = new NoOpCommandDescriptor(name, arguments);
        registry.registerCommand(descriptor);
    }

    /**
     * We assume cursor is at the end of input.
     */
    private void assertCandidates(CommandRegistry registry, String command, String... expected) {
        int cursor = command.length();
        assertCandidates(registry, command, cursor, expected);
    }

    private void assertCandidates(CommandRegistry registry, String command, int cursor, String... expected) {
        CommandEnvironment env = new CommandEnvironment();
        CommandCompleter completer = new CommandCompleter(env, registry);

        List<Candidate> candidates = new ArrayList<>();
        ParsedLine line = parseJLine(command, cursor);
        completer.complete(null, line, candidates);

        Set<String> actual = candidates.stream().map(Candidate::value).collect(Collectors.toSet());
        assertEquals(Set.of(expected), actual);
    }

    private static class NoOpCommandDescriptor extends CommandDescriptor<Command> {

        protected NoOpCommandDescriptor(String name, String... arguments) {
            super(buildSyntax(name, arguments));
        }

        @Override
        public Command buildCommand(CommandString string, ParserContext context) {
            return null;
        }

        private static CommandSyntax buildSyntax(String name, String... arguments) {

            Argument[] array = new Argument[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                array[i] = new Argument(arguments[i]);
            }

            return new CommandSyntax(name, array);
        }
    }

}
