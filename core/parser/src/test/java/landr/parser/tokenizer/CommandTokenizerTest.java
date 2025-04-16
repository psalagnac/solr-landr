package landr.parser.tokenizer;

import org.junit.Test;

import java.util.List;

/**
 * Test for high level tokenization.
 */
public class CommandTokenizerTest extends TokenizerTestBase {

    /**
     * Happy path parsing.
     */
    @Test
    public void testSimpleTokens() {

        // happy path
        assertTokens("echo foo bar",
            List.of("echo", "foo", "bar"),
            List.of(0,      5,      9),
            List.of(3,      7,      11)
        );

        // additional whitespaces
        assertTokens("   echo    foo    bar   ",
            List.of("echo", "foo", "bar"),
            List.of(3,      11,     18),
            List.of(6,      13,     20)
        );
    }

    /**
     * Test aggregating tokens into a phrase.
     */
    @Test
    public void testPhrase() {

        // just a phrase
        assertTokens("\"the phrase\"",
            List.of("the phrase"),
            List.of(0),
            List.of(11)
        );

        // single token phrase
        assertTokens("\"single\"",
            List.of("single"),
            List.of(0),
            List.of(7)
        );

        // token and phrase
        assertTokens("token \"and phrase\"",
            List.of("token", "and phrase"),
            List.of(0,      6),
            List.of(4,      17)
        );

        // with whitespaces
        // we keep spaces in the phrase
        assertTokens("   \"the   phrase\"   ",
            List.of("the   phrase"),
            List.of(3),
            List.of(16)
        );
    }

    /**
     * Test real commands with arguments.
     */
    @Test
    public void testArguments() {

        // happy path
        assertTokens("command -arg value",
            List.of("command", "-arg", "value"),
            List.of(0,         8,      13),
            List.of(6,         11,     17)
        );

        // spaces
        assertTokens("  spaces   param1  -key1 value1  ",
            List.of("spaces", "param1", "-key1", "value1"),
            List.of(2,        11,       19,      25),
            List.of(7,        16,       23,      30)
        );
    }

    /**
     * Check we ignore empty statements as expected.
     */
    @Test
    public void testEmptyStatements() {
        assertTokens(";;echo foo;;",
            List.of(";", ";", "echo", "foo", ";", ";"),
            List.of(0,   1,    2,     7,     10, 11),
            List.of(0,   1,    5,     9,     10, 11)
        );
    }

    /**
     * Check we can select a statement among others for completion.
     */
    @Test
    public void testFilteringStatement() {
        assertTokens("echo foo ; echo bar",
            12,
            List.of("echo", "bar"),
            List.of(11, 16),
            List.of(14, 18)
        );
    }

    private void assertTokens(String command, List<String> expected, List<Integer> starts, List<Integer> ends) {
        assertTokens(command, -1, expected, starts, ends);
    }

    private void assertTokens(String command, int cursor, List<String> expected, List<Integer> starts, List<Integer> ends) {

        CommandTokenizer tokenizer;
        if (cursor >= 0) {
            tokenizer = new CommandTokenizer(command, cursor);
        } else {
            tokenizer = new CommandTokenizer(command);
        }

        assertTokens(tokenizer, expected, starts, ends);
    }
}
