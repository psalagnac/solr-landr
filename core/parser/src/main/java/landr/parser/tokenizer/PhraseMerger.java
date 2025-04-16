package landr.parser.tokenizer;

import java.util.Iterator;

/**
 * Iterator to merge subsequent components of a phrase into a single token.
 */
class PhraseMerger extends AbstractTokenIterator {

    private final Iterator<Token> input;

    // a token already retrieve from parent iterator, bt not yet processed
    private Token nextToken;

    PhraseMerger(Iterator<Token> input) {
        this.input = input;
    }

    @Override
    Token nextToken() {
        // don't read the parent iterator when we already have a token
        if (nextToken != null) {
            Token result = nextToken;
            nextToken = null;
            return splitToken(result);
        }

        if (!input.hasNext()) {
            return null;
        } else {
            Token token = input.next();
            return splitToken(token);
        }
    }

    private Token splitToken(Token token) {

        int quoteIndex = token.indexOf(Token.QUOTE);

        // Read all phrase tokens
        if (quoteIndex == 0) {
            Token appender = token.subToken(1);
            int index = appender.indexOf(Token.QUOTE);
            if (index == -1) {
                while (input.hasNext()) {
                    Token nextToken = input.next();
                    index = nextToken.indexOf(Token.QUOTE);
                    if (index == -1) {
                        appender = appender.append(nextToken);
                    } else {
                        appender = appender.append(nextToken, 0, index - 1);
                        this.nextToken = nextToken.subToken(index + 1);
                        break;
                    }
                }
            } else {
                nextToken = appender.subToken(index + 1);
                appender = appender.subToken(0, index - 1);
            }
            return appender.asPhrase();

        } else if (quoteIndex > 0) {
            nextToken = token.subToken(quoteIndex);
            return token.subToken(0, quoteIndex - 1);
        }
        else {
            return token;
        }
    }

}
