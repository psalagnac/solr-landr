package landr.parser.tokenizer;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class PhraseMergerTest extends TokenizerTestBase {

    /**
     * Test commands with a simple tokens. No phrase should be injected.
     */
    @Test
    public void testTokens() {

        // Just two words !
        assertTokens("foo bar",
            List.of("foo", "bar"),
            List.of(0, 4),
            List.of(2, 6),
            List.of(false, false)
        );

        // With spaces, blank tokens are kept
        // Space are kept before a token, and discarded after.
        assertTokens("    foo     bar    ",
            List.of("    foo", "    bar", "   "),
            List.of(0, 8,  16),
            List.of(6, 14, 18),
            List.of(false, false, false)
        );
    }

    /**
     * Happy path for merging phrases.
     */
    @Test
    public void testMergePhrases() {

        // Simple phrase
        assertTokens("\"foo bar\"",
            List.of("foo bar"),
            List.of(0),
            List.of(8),
            List.of(true)
        );

        // single token phrase
        assertTokens("\"single\"",
            List.of("single"),
            List.of(0),
            List.of(7),
            List.of(true)
        );

        // token and phrase
        assertTokens("token \"and phrase\"",
            List.of("token", "and phrase"),
            List.of(0,      6),
            List.of(4,      17),
            List.of(false, true)
        );

        // With whitespaces. We keep blank token before and after the phrase, but not the first space 'separator'
        // We keep spaces in the phrase
        assertTokens("   \"the   phrase\"   ",
            List.of("   ", "the   phrase", "  "),
            List.of(0, 3,  18),
            List.of(2, 16, 19),
            List.of(false, true, false)
        );

        // A phrase followed by a token, with missing space
        assertTokens("\"the phrase\"token",
            List.of("the phrase", "token"),
            List.of(0,  12),
            List.of(11, 16),
            List.of(true, false)
        );
    }

    @Test
    public void testIndexes() {

        // Simple tokens. We should not touch what is returned by tokenizer
        assertTokenIndexes("foo bar qux",
            List.of("foo", "bar", "qux"),
            List.of(0, 1, 2),
            List.of(false, false, false)
        );

        // Phrase
        assertTokenIndexes("\"foo bar\"",
            List.of("foo bar"),
            List.of(0),
            List.of(true)
        );

        // Phrase followed by a token. There is nothing at position 1
        assertTokenIndexes("\"foo bar\" token",
            List.of("foo bar", "token"),
            List.of(0, 2),
            List.of(true, false)
        );
    }

    private void assertTokens(String command, List<String> expected, List<Integer> starts, List<Integer> ends, List<Boolean> phrases) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        PhraseMerger merger = new PhraseMerger(tokenizer);
        assertTokens(merger, expected, starts, ends, phrases);
    }

    private void assertTokenIndexes(String command, List<String> expected, List<Integer> indexes, List<Boolean> phrases) {
        Iterator<Token> tokenizer = new Tokenizer(command);
        PhraseMerger merger = new PhraseMerger(tokenizer);
        List<ExpectedToken> tokens = buildTokenList(expected, null, null, phrases, indexes, null);
        assertTokens(merger, tokens);
    }
}