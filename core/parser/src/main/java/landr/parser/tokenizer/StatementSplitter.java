package landr.parser.tokenizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This token iterator splits in case of a statement separator.
 */
class StatementSplitter extends AbstractTokenIterator {

    private final Iterator<Token> delegate;

    private final Queue<Token> queue;
    private int statementIndex;

    StatementSplitter(Iterator<Token> delegate) {
        this.delegate = delegate;

        queue = new LinkedList<>();
    }

    @Override
    Token nextToken() {

        if (!queue.isEmpty()) {
            Token token = queue.remove();

            return splitToken(token);
        }

        if (!delegate.hasNext()) {
            return null;
        }

        Token token = delegate.next();
        return splitToken(token);
    }

    /**
     * Split the token in case it contains a statement separator.
     */
    private Token splitToken(Token token) {

        // That's already a well-formed statement separator. It may come from the queue or directly
        // from the input. We just return it.
        if (token.isStatementSeparator()) {
            token = updateIndex(token);
            statementIndex = 0;
            return token;
        }

        // That a phrase, we don't split
        if (token.isPhrase()) {
            return updateIndex(token);
        }

        int separatorIndex = token.indexOf(Token.STATEMENT_SEPARATOR);

        if (separatorIndex == -1) {
            // no separator in the token
            return updateIndex(token);
        } else {
            // We found a statement separator
            Token left = splitSeparator(token, separatorIndex);
            return updateIndex(left);
        }
    }

    /**
     * The first non-regular character is a statement separator.
     */
    private Token splitSeparator(Token token, int index) {

        Token left = token.subToken(0, index - 1);
        Token separator = token.subToken(index, index);
        Token right = token.subToken(index + 1);

        // separator is at the beginning of the token
        if (left == null) {
            queue.add(right);
            return separator;
        }

        queue.add(separator);
        if (right != null) {
            queue.add(right);
        }

        return left;
    }

    /**
     * Create a new similar token with updated statement index.
     */
    private Token updateIndex(Token token) {

        Token result;

        // no update required
        if (token.getStatementIndex() == statementIndex) {
            result = token;
        } else {
            Token.Builder builder = token.builder();
            builder.statementIndex(statementIndex);
            result = builder.build();
        }

        statementIndex++;
        return result;
    }
}
