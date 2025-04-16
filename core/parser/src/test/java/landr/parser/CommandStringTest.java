package landr.parser;

import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * High level test for command line parsing. This does not take layout into account.
 */
public class CommandStringTest {

    @Test
    public void testSingleTokenCommand() {
        String command = "single-command";
        CommandString string = parseSingle(command);

        assertEquals("single-command", string.getCommandName());
        assertFalse(string.isPragma());
    }

    @Test
    public void testSingleTokenPragma() {
        String command = "@single-pragma";
        CommandString string = parseSingle(command);

        assertEquals("single-pragma", string.getCommandName());
        assertTrue(string.isPragma());
    }

    @Test
    public void testArguments() {
        String command = "arguments -key1 value1 -key2 value2";
        CommandString string = parseSingle(command);

        assertEquals("value1", string.getArgumentValue("key1"));
        assertEquals("value2", string.getArgumentValue("key2"));
    }

    @Test
    public void testArgumentWithoutValue() {
        String command = "no-value -key";
        CommandString string = parseSingle(command);

        assertNull(string.getArgumentValue("key"));
    }

    @Test
    public void testPhraseArgument() {
        String command = "phrase -param \"one two three\"";

        CommandString string = parseSingle(command);
        assertEquals("one two three", string.getArgumentValue("param"));
    }

    @Test
    public void testIncompletePhraseArgument() {
        String command = "phrase -param \"one two three";

        CommandString string = parseSingle(command);
        assertEquals("one two three", string.getArgumentValue("param"));
    }

    @Test
    public void testPhraseParameter() {
        String command = "phrase \"one two three\"";

        CommandString string = parseSingle(command);
        assertEquals("one two three", string.getParameter(0));
    }

    @Test
    public void testParameters() {
        String command = "parameters param1 param2 param3";
        CommandString string = parseSingle(command);

        assertEquals(3, string.getParameterCount());
        assertEquals("param1", string.getParameter(0));
        assertEquals("param2", string.getParameter(1));
        assertEquals("param3", string.getParameter(2));
    }

    @Test
    public void testMixedParameters() {
        String command = "parameters param1 -key1 value1 -key2 value2 param2";
        CommandString string = parseSingle(command);

        assertEquals("value1", string.getArgumentValue("key1"));
        assertEquals("value2", string.getArgumentValue("key2"));

        assertEquals(2, string.getParameterCount());
        assertEquals("param1", string.getParameter(0));
        assertEquals("param2", string.getParameter(1));
    }

    @Test
    public void testSpaces() {
        String command = "  spaces   param1  -key1 value1  ";
        CommandString string = parseSingle(command);

        assertEquals("spaces", string.getCommandName());
        assertEquals("value1", string.getArgumentValue("key1"));

        assertEquals(1, string.getParameterCount());
        assertEquals("param1", string.getParameter(0));
    }

    @Test
    public void testReverseParsing() {
        String command = "parameters -key1 value1 -key2 value2 param1 \"the phrase\"";
        CommandString string = parseSingle(command);

        assertEquals(command, string.toString());
    }

    @Test
    public void testMultipleStatements() {
        String commands = "echo foo; echo bar";

        List<CommandString> strings = CommandString.parse(commands);
        assertEquals(2, strings.size());
    }

    @Test
    public void testEmptyStatements() {
        String commands = ";;echo foo;;";
        CommandString string = parseSingle(commands);
        assertEquals("echo", string.getCommandName());
    }

    @Test
    public void testCursorScopedParsing() {
        String commands = "first foo; second bar";

        // first statement
        assertEquals("first", Objects.requireNonNull(CommandString.parse(commands, 0)).getCommandName());
        assertEquals("first", Objects.requireNonNull(CommandString.parse(commands, 3)).getCommandName());
        assertEquals("first", Objects.requireNonNull(CommandString.parse(commands, 7)).getCommandName());

        // second statement
        assertEquals("second", Objects.requireNonNull(CommandString.parse(commands, 11)).getCommandName());

        // no statement in the middle
        assertNull(CommandString.parse(commands, 9));
    }

    /**
     * Parse a single statement for the test.
     */
    private static CommandString parseSingle(String command) {
        List<CommandString> strings = CommandString.parse(command);
        assertEquals(1, strings.size());
        return strings.get(0);
    }

}
