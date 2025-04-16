package landr.parser.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Abstract class for tokenizer tests.
 */
public abstract class TokenizerTestBase {

    /**
     * Assert values of created tokens. Positions are not checked.
     */
    void assertTokenValues(Iterator<Token> iterator, List<String> expected) {

        // We don't check positions. Create lists with null entries
        List<Integer> positions = new ArrayList<>();
        expected.forEach(t -> positions.add(null));

        assertTokens(iterator, expected, positions, positions);
    }

    /**
     * Assert values and positions for the expected tokens.
     */
    void assertTokens(Iterator<Token> iterator, List<String> expected, List<Integer> starts, List<Integer> ends) {
        assertTokens(iterator, expected, starts, ends, null);
    }

    /**
     * Assert values and positions for the expected tokens.
     *
     * @param phrases For each non {@code null} element, we check the type of the item (token or phrase). The check
     *                is skipped for null elements.
     */
    void assertTokens(Iterator<Token> iterator, List<String> expected, List<Integer> starts, List<Integer> ends, List<Boolean> phrases) {

        List<ExpectedToken> tokens = buildTokenList(expected, starts, ends, phrases, null, null);
        assertTokens(iterator, tokens);
    }

    /**
     * Checks all tokens from the tokenizer result are similar to what is expected by the test. Any field
     * in a {@link ExpectedToken} that is {@code null} is skipped and not validated by the test.
     */
    void assertTokens(Iterator<Token> iterator, List<ExpectedToken> expectedTokens) {

        int counter = 0;
        int lastIndex = -1;

        while (iterator.hasNext()) {
            Token token = iterator.next();

            if (counter == expectedTokens.size()) {
                fail("Spurious token: " + token);
            }

            ExpectedToken expected = expectedTokens.get(counter);
            boolean isPhrase = false;

            // If we know the expected type, actually check it
            if (expected.isPhrase != null) {
                if (expected.isPhrase) {
                    isPhrase = true;
                    String message = String.format("phrase expected %s", token);
                    assertEquals(message, Phrase.class, token.getClass());
                } else {
                    String message = String.format("token expected %s", token);
                    assertEquals(message, Token.class, token.getClass());
                }
            } else {
                // We don't know the expected type. Guess it, so the assertion of the value is not too strict
                isPhrase = token instanceof Phrase;
            }

            // Make sure the token has a position strictly greater than the previous token
            assertTrue(token.toString(), token.getIndex() >= lastIndex);
            lastIndex = token.getIndex();

            Token.Builder builder;
            if (isPhrase) {
                builder = new Phrase.Builder();
            } else {
                builder = new Token.Builder();
            }
            Token expectedToken = builder
                .value(expected.getExpectedValue())
                .startPosition(expected.getExpectedStartPosition(token))
                .endPosition(expected.getExpectedEndPosition(token))
                .index(expected.getExpectedIndex(token))
                .statementIndex(expected.getExpectedStatementIndex(token))
                .build();
            assertEquals(expectedToken, token);
            counter++;
        }

        assertEquals("missing tokens", expectedTokens.size(), counter);
    }

    /**
     * Build a list of expected tokens for assertion in this class.
     * 
     * @see #assertTokens(Iterator, List) 
     */
    static List<ExpectedToken> buildTokenList(List<String> values, List<Integer> starts, List<Integer> ends,
            List<Boolean> phrases, List<Integer> indexes, List<Integer> statementIndexes) {

        List<Integer> emptyInteger = new ArrayList<>(values.size());
        List<Boolean> emptyBooleans = new ArrayList<>(values.size());
        values.forEach(t -> emptyInteger.add(null));
        values.forEach(t -> emptyBooleans.add(null));


        // Sanity check all lists have same size
        if (starts != null) {
            assertEquals("test error", values.size(), starts.size());
        } else {
            starts = emptyInteger;
        }
        if (ends != null) {
            assertEquals("test error", values.size(), ends.size());
        } else {
            ends = emptyInteger;
        }

        if (phrases != null) {
            assertEquals("test error", values.size(), phrases.size());
        } else {
            phrases = emptyBooleans;
        }
        if (indexes != null) {
            assertEquals("test error", values.size(), indexes.size());
        } else {
            indexes = emptyInteger;
        }
        if (statementIndexes != null) {
            assertEquals("test error", values.size(), statementIndexes.size());
        } else {
            statementIndexes = emptyInteger;
        }

        List<ExpectedToken> tokens = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            ExpectedToken token = new ExpectedToken(values.get(i), phrases.get(i), starts.get(i),
                    ends.get(i), indexes.get(i), statementIndexes.get(i));
            tokens.add(token);
        }

        return tokens;
    }

    static class ExpectedToken {

        private final String value;
        private final Boolean isPhrase;
        private final Integer startPosition;
        private final Integer endPosition;
        private final Integer index;
        private final Integer statementIndex;

        ExpectedToken(String value, Boolean isPhrase, Integer startPosition, Integer endPosition, Integer index, Integer statementIndex) {
            this.value = value;
            this.isPhrase = isPhrase;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.index = index;
            this.statementIndex = statementIndex;
        }

        String getExpectedValue() {
            assertNotNull(value);
            return value;
        }

        int getExpectedStartPosition(Token token) {
            if (startPosition != null) {
                return startPosition;
            } else {
                return token.getStartPosition();
            }
        }

        int getExpectedEndPosition(Token token) {
            if (endPosition != null) {
                return endPosition;
            } else {
                return token.getEndPosition();
            }
        }

        int getExpectedIndex(Token token) {
            if (index != null) {
                return index;
            } else {
                return token.getIndex();
            }
        }

        int getExpectedStatementIndex(Token token) {
            if (statementIndex != null) {
                return statementIndex;
            } else {
                return token.getStatementIndex();
            }
        }
    }
}
