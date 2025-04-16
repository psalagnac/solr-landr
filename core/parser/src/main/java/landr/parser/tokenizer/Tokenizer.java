package landr.parser.tokenizer;

/**
 * Low level tokenization.
 */
class Tokenizer extends AbstractTokenIterator {

    private static final int BUFFER_SIZE = 256;

    private final CharSequence input;
    private int position;

    // Buffer for produced tokens
    // We fail parsing if a token is larger than the buffer
    private char[] buffer;
    private int bufferPosition;

    private final Token.Builder builder = new Token.Builder();

    Tokenizer(CharSequence input) {
        this.input = input;

        buffer = new char[BUFFER_SIZE];
        builder.index(-1);
    }

    @Override
    Token nextToken() {
        int bufferStart = bufferPosition;
        builder.startPosition(position);

        boolean empty = true;
        while (position < input.length()) {
            char c = input.charAt(position++);
            boolean whitespace = Character.isWhitespace(c);
            if (whitespace && !empty) {
                break;
            } else {
                if (bufferPosition + 1 == buffer.length) {
                    // We're at end of the buffer. If the token is not too big, restart from the beginning
                    if (bufferStart > 0) {
                        char[] oldBuffer = buffer;
                        buffer = new char[BUFFER_SIZE];
                        System.arraycopy(oldBuffer, bufferStart, buffer, 0, bufferPosition - bufferStart);
                        bufferPosition = bufferPosition - bufferStart;
                        bufferStart = 0;
                    } else {
                        throw new RuntimeException("too large token");
                    }
                }

                buffer[bufferPosition++] = c;
                empty = empty && whitespace;
            }
        }

        if (bufferPosition > bufferStart) {
            int end = position == input.length() ? position - 1 : position - 2;
            builder.endPosition(end);
            builder.value(buffer, bufferStart, bufferPosition - 1);
            builder.index(builder.index + 1);
            return builder.build();
        } else {
            return null;
        }
    }

}
