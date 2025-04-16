package landr.parser.tokenizer;

import java.util.Iterator;

/**
 * This iterator removes whitespaces and blank tokens.
 */
class Trimmer extends AbstractTokenIterator {

    private final Iterator<Token> input;

    Trimmer(Iterator<Token> input) {
        this.input = input;
    }

    @Override
    Token nextToken() {

        while (input.hasNext()) {
            Token token = input.next();

            Token trimmed = token.trim();
            if (trimmed != null) {
                return trimmed;
            }
        }

        return null;
    }

}
