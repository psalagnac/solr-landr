package landr.parser.tokenizer;

import java.util.Arrays;

/**
 * Simple record class for a token in the parser.
 *
 * <p>Note that methods with bounds like {@link #subToken(int, int) subToken()} or
 * {@link #append(Token, int, int) append()} behaves differently from similar method in {@link String}. The parameter
 * for end position is always inclusive.
 */
public class Token {

    static final char STATEMENT_SEPARATOR = ';';
    static final char QUOTE = '"';

    //final String value;
    final char[] buffer;
    final int bufferStart;
    final int bufferEnd;

    // position are inclusive (not like java String)
    final int startPosition;
    final int endPosition;
    final int index;
    final int statementIndex;

    Token(Builder builder) {
        this.buffer = builder.buffer;
        this.bufferStart = builder.start;
        this.bufferEnd = builder.end;
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.index = builder.index;
        this.statementIndex = builder.statementIndex;
    }

    /**
     * Return the number of characters in the token value.
     */
    public int getLength() {
        return bufferEnd - bufferStart + 1;
    }

    /**
     * Return the position of the first character of this token in the initial input.
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Return the position of the last character of this token in the initial input.
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Return the position of the token in the command line.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Return the position of the token in its parent statement. If the command line has a single statement,
     * the returned value is the same as {@link #getIndex()}.
     */
    int getStatementIndex() {
        return statementIndex;
    }

    /**
     * Return whether the token was identified as a phrase.
     *
     * @see Phrase
     */
    boolean isPhrase() {
        return false;
    }

    /**
     * Returns whether this token is a separator, not part of any statement?
     */
    public boolean isStatementSeparator() {
        return getLength() == 1 && buffer[bufferStart] == STATEMENT_SEPARATOR;
    }

    /**
     * Return single char at specified position.
     */
    public char charAt(int index) {
        return buffer[bufferStart + index];
    }

    /**
     * Return first position on the given character, or -1 if not present.
     */
    int indexOf(char ch) {
        for (int i=bufferStart; i<=bufferEnd; i++) {
            if (buffer[i] == ch) {
                return i - bufferStart;
            }
        }
        return -1;
    }

    /**
     * Remove leading and trailing whitespaces. Positions are updated.
     */
    Token trim() {

        if (!Character.isWhitespace(buffer[bufferStart]) && !Character.isWhitespace(buffer[bufferEnd])) {
            return this;
        }

        int start = 0;
        int end = getLength() - 1;
        while (Character.isWhitespace(buffer[bufferStart + start]) && start < end) {
            start++;
        }

        while (end >= start && Character.isWhitespace(buffer[bufferStart + end])) {
            end--;
        }

        return subToken(start, end);
    }

    /**
     * Create a sub-token, starting at the specified position (inclusive).
     */
    public Token subToken(int start) {
        return subToken(start, getLength() - 1);
    }

    /**
     * Create a sub-token. Both start and end positions are inclusive (unlike java String).
     */
    public Token subToken(int start, int end) {

        // Optimization: positions are for the full token
        if (start == 0 && end == getLength() - 1) {
            return this;
        }

        if (start <= end) {
            Builder builder = new Builder(this);
            builder.value(buffer, bufferStart + start,bufferStart + end);
            builder.positions(startPosition + start, startPosition + end);
            return builder.build();
        } else {
            return null;
        }
    }

    /**
     * Append the content of another token at the end of this one. A whitespace is put as separator.
     */
    Token append(Token token) {
        // Unfortunately, we can't reuse the buffer. It may be used by another token after our end position
        char[] newBuffer = new char[getLength() + token.getLength() + 1];
        System.arraycopy(buffer, bufferStart, newBuffer, 0, getLength());
        newBuffer[getLength()] = ' ';
        System.arraycopy(token.buffer, token.bufferStart, newBuffer, getLength() + 1, token.getLength());

        Builder builder = new Builder(this);
        builder.value(newBuffer, 0, newBuffer.length - 1);
        builder.positions(startPosition, token.endPosition);
        return builder.build();
    }

    /**
     * Append a subpart of another token at the end of this one. A whitespace is put as separator.
     */
    Token append(Token token, int start, int end) {
        // Unfortunately, we can't reuse the buffer. It may be used by another token after our end position
        char[] newBuffer = new char[getLength() + end - start + 2];
        System.arraycopy(buffer, bufferStart, newBuffer, 0, getLength());
        newBuffer[getLength()] = ' ';
        System.arraycopy(token.buffer, token.bufferStart + start, newBuffer, getLength() + 1, end - start + 1);

        int dropped = token.getLength() - end - 1;

        Builder builder = new Builder(this);
        builder.value(newBuffer, 0, newBuffer.length - 1);
        builder.positions(startPosition, token.endPosition - dropped);
        return builder.build();
    }

    /**
     * Concert this token into a phrase with same values.
     */
    Phrase asPhrase() {
        Phrase.Builder builder = new Phrase.Builder(this);
        builder.positions(startPosition - 1, endPosition + 1);
        return builder.build();
    }

    /**
     * Return the value of the token as a string.
     */
    public String getValue() {
        return new String(buffer, bufferStart, bufferEnd - bufferStart + 1);
    }

    /**
     * Create a new token builder, with all fields pre-populated with values for this token.
     */
    Builder builder() {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        // Ignore offsets when computing hashcode
        return Arrays.hashCode(buffer);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Token.class) return false;
        Token other = (Token) obj;

        boolean contentEquals = Arrays.equals(buffer, bufferStart, bufferEnd, other.buffer, other.bufferStart, other.bufferEnd);
        return contentEquals
                && startPosition == other.startPosition
                && endPosition == other.endPosition
                && index == other.index
                && statementIndex == other.statementIndex;
    }

    @Override
    public String toString() {
        return String.format("%s [start=%d, end=%d, index=%d, statementIndex=%d]",
                getValue(), startPosition, endPosition, index, statementIndex);
    }

    /**
     * Builder pattern.
     */
    static class Builder {

        char[] buffer;
        int start;
        int end;

        int startPosition;
        int endPosition;
        int index;
        int statementIndex;

        Builder() {
        }

        Builder(Token token) {
            this.buffer = token.buffer;
            this.start = token.bufferStart;
            this.end = token.bufferEnd;

            this.startPosition = token.startPosition;
            this.endPosition = token.endPosition;
            this.index = token.index;
            this.statementIndex = token.statementIndex;
        }

        Builder value(String value) {
            buffer = value.toCharArray();
            start = 0;
            end = buffer.length - 1;
            return this;
        }

        Builder value(char[] buffer, int start, int end) {
            this.buffer = buffer;
            this.start = start;
            this.end = end;
            return this;
        }

        Builder startPosition(int startPosition) {
            this.startPosition = startPosition;
            return this;
        }

        Builder endPosition(int endPosition) {
            this.endPosition = endPosition;
            return this;
        }

        Builder positions(int startPosition, int endPosition) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            return this;
        }

        Builder index(int index) {
            this.index = index;
            return this;
        }

        Builder statementIndex(int statementIndex) {
            this.statementIndex = statementIndex;
            return this;
        }

        Token build() {
            return new Token(this);
        }
    }
}
