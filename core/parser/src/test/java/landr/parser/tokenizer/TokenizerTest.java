package landr.parser.tokenizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test for low level command line tokenization.
 */
public class TokenizerTest extends TokenizerTestBase {

    @Test
    public void testTokenize() {

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

        // The low level tokenizer does not recognize phrases. We keep all tokens as they appear
        assertTokens("\"foo bar\"",
            List.of("\"foo", "bar\""),
            List.of(0, 5),
            List.of(3, 8)
        );
    }

    @Test
    public void testIndexes() {

        // Simple tokens
        assertTokenIndexes("foo bar qux",
            List.of("foo", "bar", "qux"),
            List.of(0, 1, 2)
        );

        // Phrase
        assertTokenIndexes("\"foo bar\"",
            List.of("\"foo", "bar\""),
            List.of(0, 1)
        );
    }

    /**
     * Tokenizes an input larger than the internal buffer.
     */
    @Test
    public void testBufferOverflow() {

        // Create a string with 1000 small tokens !
        List<String> tokens = new ArrayList<>();
        for (int i=0; i<1000; i++) {
            tokens.add(String.valueOf(i));
        }

        String input = String.join(" ", tokens);
        assertTokens(input, tokens);
    }

    /**
     * Check only token values, ignoring positions.
     */
    private void assertTokens(String command, List<String> expected) {
        assertTokens(command, expected, null, null);
    }

    private void assertTokens(String command, List<String> expected, List<Integer> starts, List<Integer> ends) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        assertTokens(tokenizer, expected, starts, ends);
    }

    private void assertTokenIndexes(String command, List<String> expected, List<Integer> indexes) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        List<ExpectedToken> tokens = buildTokenList(expected, null, null, null, indexes, null);
        assertTokens(tokenizer, tokens);
    }

}
