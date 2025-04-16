package landr.parser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringParserTest {

    /**
     * Check the layout does not blow up with an empty input.
     */
    @Test
    public void testEmptyLayout() {
        StringParser parser = new StringParser();
        List<CommandString> strings = parser.parse("");
        assertTrue(strings.isEmpty());
    }

    @Test
    public void testSingleCommandLayout() {

        String command = "layout param1 -key1 value1 -key2 value2 param2";

        CommandString string = parseSingle(command);
        CommandStringLayout layout = string.getLayout();

        assertEquals(0, layout.getIndex());
        assertEquals(0, layout.getCommandNameIndex());

        // Check command name position
        assertEquals(0, layout.getCommandNameStartPosition());
        assertEquals(5, layout.getCommandNameEndPosition());
    }

    /**
     * Ensure empty statements are somehow visible in the layout of a non-empty statement.
     */
    @Test
    public void testEmptyStatements() {

        String command = ";   ; echo foo";

        CommandString string = parseSingle(command);
        CommandStringLayout layout = string.getLayout();

        // The non-empty statement is the 3rd one (index 2)
        assertEquals(2, layout.getIndex());
        assertEquals(2, layout.getCommandNameIndex());
    }

    @Test
    public void testMultipleStatements() {

        String command = "echo foo ; echo bar";

        StringParser parser = new StringParser();
        List<CommandString> strings = parser.parse(command);

        assertEquals(2, strings.size());
        CommandStringLayout layout1 = strings.get(0).getLayout();
        CommandStringLayout layout2 = strings.get(1).getLayout();

        assertEquals(0, layout1.getIndex());
        assertEquals(1, layout2.getIndex());
        assertEquals(0, layout1.getCommandNameIndex());
        assertEquals(3, layout2.getCommandNameIndex());
    }

    /**
     * Parse a single statement for the test.
     */
    private static CommandString parseSingle(String command) {
        StringParser parser = new StringParser();
        List<CommandString> strings = parser.parse(command);
        assertEquals(1, strings.size());
        return strings.get(0);
    }
}
