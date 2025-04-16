package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionContext;
import landr.cmd.DefaultExecutionContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.PragmaSyntax;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DebugPragmaTest {

    private enum Flag {
        DEBUG,
        VERBOSE
    }

    /**
     * Test we flip the debug flag in parser and execution contexts.
     */
    @Test
    public void testDebugExecutionSequence() throws Exception {

        CommandRegistry registry = createRegistry();

        String script =
                "assert-debug-command -value false \n" +
                "@assert-debug-pragma false \n" +

                // enable debug (implicit parameter)
                "@debug \n" +
                "assert-debug-command -value true \n" +
                "@assert-debug-pragma true \n" +

                // disable debug
                "@debug false\n" +
                "assert-debug-command -value false \n" +
                "@assert-debug-pragma false";

        CommandParser parser = new CommandParser(registry);
        List<Command> commands = parser.parse(new StringReader(script));

        // the debug pragma should inject a command. So we have a total of 5 commands
        assertEquals(5, commands.size());

        // execute commands (with assertions)
        CommandEnvironment environment = new CommandEnvironment();
        CommandExecutionContext context = new DefaultExecutionContext(environment);
        for (Command command : commands) {
            command.execute(context);
        }
    }

    /**
     * Test flipping the verbose flag.
     */
    @Test
    public void testVerboseExecutionSequence() throws Exception {

        CommandRegistry registry = createRegistry();

        String script =
                // enable verbose
                "@verbose true \n" +
                "assert-verbose-command true \n" +
                "@assert-verbose-pragma true \n" +

                // disable verbose
                "@verbose false\n" +
                "assert-verbose-command false \n" +
                "@assert-verbose-pragma false";

        CommandParser parser = new CommandParser(registry);
        List<Command> commands = parser.parse(new StringReader(script));

        // the debug pragma should inject a command. So we have a total of 4 commands
        assertEquals(4, commands.size());

        // execute commands (with assertions)
        CommandEnvironment environment = new CommandEnvironment();
        CommandExecutionContext context = new DefaultExecutionContext(environment);
        for (Command command : commands) {
            command.execute(context);
        }
    }

    /**
     * Test tracing command executions.
     */
    @Test
    public void testTracing() throws Exception {

        CommandRegistry registry = createRegistry();

        String script =
                // enable verbose
                "@trace true \n" +

                // does nothing, but we trace it anyway
                "no-op";

        CommandParser parser = new CommandParser(registry);
        List<Command> commands = parser.parse(new StringReader(script));

        // with trace enabled, we should inject an echo command, so we have 2 commands
        assertEquals(2, commands.size());
        assertTrue(commands.get(0) instanceof EchoCommand);
        assertTrue(commands.get(1) instanceof NullCommand);
    }


    /**
     * Create a command registry with all the commands and pragmas for the tests.
     */
    private static CommandRegistry createRegistry() {
        CommandRegistry registry = new CommandRegistry();

        registry.registerCommand(new NullCommandDescriptor());
        registry.registerCommand(new AssertDebugCommandDescriptor(Flag.DEBUG));
        registry.registerPragma(new AssertDebugPragmaDescriptor(Flag.DEBUG));
        registry.registerCommand(new AssertDebugCommandDescriptor(Flag.VERBOSE));
        registry.registerPragma(new AssertDebugPragmaDescriptor(Flag.VERBOSE));
        registry.registerPragma(DebugPragmaDescriptor.createDebugDescriptor());
        registry.registerPragma(DebugPragmaDescriptor.createVerboseDescriptor());
        registry.registerPragma(DebugPragmaDescriptor.createTraceDescriptor());

        return registry;
    }

    private static class AssertDebugCommandDescriptor extends CommandDescriptor<AssertDebugCommand> {

        private final Flag flag;

        protected AssertDebugCommandDescriptor(Flag flag) {
            super(new CommandSyntax(getDebugCommandName(flag), "value",
                    new Argument("value", true)));
            this.flag = flag;
        }

        @Override
        public AssertDebugCommand buildCommand(CommandString string, ParserContext context) throws CommandParseException {
            boolean value = getArgumentBooleanValue("value", string, context);
            return new AssertDebugCommand(flag, value);
        }

        static String getDebugCommandName(Flag flag) {
            switch (flag) {
                case DEBUG:
                    return "assert-debug-command";
                case VERBOSE:
                    return "assert-verbose-command";
                default:
                    return null;
            }
        }
    }

    private static class AssertDebugPragmaDescriptor extends PragmaDescriptor<AssertDebugPragma> {

        private final Flag flag;

        protected AssertDebugPragmaDescriptor(Flag flag) {
            super(new PragmaSyntax(getDebugPragmaName(flag)));
            this.flag = flag;
        }

        @Override
        AssertDebugPragma buildPragma(ParserContext context, CommandString string) {
            boolean value = Boolean.parseBoolean(string.getParameter(0));
            return new AssertDebugPragma(flag, value);
        }

        static String getDebugPragmaName(Flag flag) {
            switch (flag) {
                case DEBUG:
                    return "assert-debug-pragma";
                case VERBOSE:
                    return "assert-verbose-pragma";
                default:
                    return null;
            }
        }
    }

    private static class AssertDebugCommand extends Command {
        private final Flag flag;
        private final boolean value;

        protected AssertDebugCommand(Flag flag, boolean value) {
            this.flag = flag;
            this.value = value;
        }

        @Override
        public void execute(CommandExecutionContext context) {
            switch (flag) {
                case DEBUG:
                    assertEquals(value, context.getDebug());
                    break;
                case VERBOSE:
                    assertEquals(value, context.getVerbose());
                    break;
                default:
                    fail();
            }
        }
    }

    private static class AssertDebugPragma extends Pragma {
        private final Flag flag;
        private final boolean value;

        protected AssertDebugPragma(Flag flag, boolean value) {
            this.flag = flag;
            this.value = value;
        }

        @Override
        public void execute(ParserContext context) {
            switch (flag) {
                case DEBUG:
                    assertEquals(value, context.getDebug());
                    break;
                case VERBOSE:
                    assertEquals(value, context.getVerbose());
                    break;
                default:
                    fail();
            }
        }
    }

}
