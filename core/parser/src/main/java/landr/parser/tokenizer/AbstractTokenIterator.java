package landr.parser.tokenizer;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstract class for token iterators.
 * It implements ahead fetching of the next token.
 */
abstract class AbstractTokenIterator implements Iterator<Token> {

    // next token ready to be returned
    private Token next;

    @Override
    public boolean hasNext() {

        if (next != null) {
            return true;
        }

        next = nextToken();
        return next != null;
    }

    @Override
    public Token next() {

        Token result;
        if (next != null) {
            result = next;
            next = null;
        } else {
            result = nextToken();
            if (result == null) {
                throw new NoSuchElementException();
            }
        }
        return result;
    }

    /**
     * Process and return the next token.
     *
     * @return The next token, or {@code null} when no token is available.
     */
    abstract Token nextToken();

}
