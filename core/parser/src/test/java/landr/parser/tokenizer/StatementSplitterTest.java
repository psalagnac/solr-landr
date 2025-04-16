package landr.parser.tokenizer;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class StatementSplitterTest extends TokenizerTestBase {

    /**
     * Test commands with a single statement. No token split happens.
     */
    @Test
    public void testSingleStatement() {

        // Just two words !
        assertTokens("foo bar",
            List.of("foo", "bar"),
            List.of(0, 4),
            List.of(2, 6)
        );

        // With spaces, blank tokens are kept
        // Space are kept before a token, and discarded after.
        assertTokens("    foo     bar    ",
            List.of("    foo", "    bar", "   "),
            List.of(0, 8,  16),
            List.of(6, 14, 18)
        );

        // Simple phrase. We keep the token as it appears
        assertTokens("\"foo bar\"",
            List.of("foo bar"),
            List.of(0),
            List.of(8),
            List.of(true)
        );

    }

    @Test
    public void testSplitStatements() {
        assertTokens("echo foo;echo bar",
            List.of("echo", "foo", ";", "echo", "bar"),
            List.of(0,      5,      8,  9,       14),
            List.of(3,      7,      8,  12,      16)
        );
    }

    /**
     * Check we return all tokens in case of empty statements.
     */
    @Test
    public void testEmptyStatements() {

        // Just separators
        assertTokens("; ;;",
            List.of(";", ";", ";"),
            List.of(0,   2,    3),
            List.of(0,   2,    3)
        );

        // Useless leading and trailing separators
        assertTokens(";;echo foo;;",
            List.of(";", ";", "echo", "foo", ";", ";"),
            List.of(0,   1,    2,     7,     10, 11),
            List.of(0,   1,    5,     9,     10, 11)
        );
    }

    /**
     * Make sure we don't split in the middle of a phrase.
     */
    @Test
    public void testPhrase() {

        // A phrase with no space
        assertTokens("echo \"foo;bar\"",
            List.of("echo", "foo;bar"),
            List.of(0,   5),
            List.of(3,   13),
            List.of(false, true)
        );

        // A phrase made by multiple tokens
        assertTokens("echo \"foo ; bar\"",
            List.of("echo", "foo ; bar"),
            List.of(0,   5),
            List.of(3,   15),
            List.of(false, true)
        );
    }

    /**
     * Make sure we keep leader and trailing whitespaces.
     */
    @Test
    public void testWhitespaces() {

        assertTokens("   echo   \"foo;bar\"   ;    echo qix",
            List.of("   echo", "  ", "foo;bar", "  ", ";", "   echo", "qix"),
            List.of(0, 8, 10, 20, 22, 24, 32),
            List.of(6, 9, 18, 21, 22, 30, 34)
        );
    }

    /**
     * Check token statements positions.
     */
    @Test
    public void testStatementIndexes() {

        // single statement, indexes should be consecutive
        assertTokensInStatement("echo foo bar qux",
            List.of("echo", "foo", "bar", "qux"),
            List.of(0, 1, 2, 3)
        );

        // multiple statement, we should reset the index
        assertTokensInStatement("echo foo; echo bar ; echo qux",
                List.of("echo", "foo", ";", "echo", "bar", ";", "echo", "qux"),
                List.of(0, 1, 2, 0, 1, 2, 0, 1)
        );
    }

    private void assertTokens(String command, List<String> expected, List<Integer> starts, List<Integer> ends) {
        StatementSplitter splitter = createTokenizer(command);
        assertTokens(splitter, expected, starts, ends);
    }

    private void assertTokens(String command, List<String> expected, List<Integer> starts, List<Integer> ends, List<Boolean> phrases) {
        StatementSplitter splitter = createTokenizer(command);
        assertTokens(splitter, expected, starts, ends, phrases);
    }

    /**
     * Check tokens with indexes in statements. This assertion does not check positions.
     */
    private void assertTokensInStatement(String command, List<String> expected, List<Integer> indexes) {
        StatementSplitter splitter = createTokenizer(command);
        List<ExpectedToken> tokens = buildTokenList(expected, null, null, null, null, indexes);
        assertTokens(splitter, tokens);
    }

    private StatementSplitter createTokenizer(String command) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        PhraseMerger merger = new PhraseMerger(tokenizer);
        return new StatementSplitter(merger);
    }
}
