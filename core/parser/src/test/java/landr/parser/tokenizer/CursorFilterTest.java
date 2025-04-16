package landr.parser.tokenizer;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class CursorFilterTest extends TokenizerTestBase {

    /**
     * When we have a single statement, we should return it, whatever the cursor position is.
     */
    @Test
    public void testSingleStatement() {

        String command = "echo foo";
        List<String> tokens = List.of("echo", "foo");

        assertTokens(command, tokens, 0);
        assertTokens(command, tokens, 1);
        assertTokens(command, tokens, 5);
        assertTokens(command, tokens, 7);

        // out of bounds
        assertTokens(command, tokens, -1);
        assertTokens(command, tokens, -1000);
        assertTokens(command, tokens, 8);
        assertTokens(command, tokens, 1000);
    }

    /**
     * Happy path to select a statement.
     */
    @Test
    public void testFilterStatement() {

        String command = "echo foo; echo bar; echo qix";

        List<String> tokens1 = List.of("echo", "foo");
        List<String> tokens2 = List.of("echo", "bar");
        List<String> tokens3 = List.of("echo", "qix");

        // first statement
        assertTokens(command, tokens1, 0);
        assertTokens(command, tokens1, 7);

        // second statement
        assertTokens(command, tokens2, 10);
        assertTokens(command, tokens2, 17);

        // last statement
        assertTokens(command, tokens3, 20);
        assertTokens(command, tokens3, 27);

        // no statements returned at separator positions
        assertTokens(command, List.of(), 8);
        assertTokens(command, List.of(), 9);
        assertTokens(command, List.of(), 18);
        assertTokens(command, List.of(), 19);
    }

    /**
     * Check we don't return anything when the cursor is in an empty statement.
     */
    @Test
    public void testEmptyStatements() {

        // The cursor is before or after a non-empty statement
        assertTokens("  ; echo bar;  ", List.of("  "), 0);
        assertTokens("  ; echo bar;  ", List.of("  "), 1);
        assertTokens("  ; echo bar;  ", List.of(" "), 14);
        assertTokens("  ; echo bar;  ", List.of(" "), 15);

        assertTokens("foo;", List.of(), 4);
    }

    private void assertTokens(String command, List<String> expected, int cursor) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        StatementSplitter splitter = new StatementSplitter(tokenizer);
        CursorFilter filter = new CursorFilter(splitter, cursor);
        assertTokenValues(filter, expected);
    }

}
