package landr.parser.tokenizer;

import java.util.Arrays;

/**
 * Subclass when we have a phrase (with quotes) in the input.
 *
 * @see Token#QUOTE
 */
class Phrase extends Token {

    Phrase(Builder builder) {
        super(builder);
    }

    @Override
    boolean isPhrase() {
        return true;
    }

    @Override
    Builder builder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Phrase.class) return false;
        Phrase other = (Phrase) obj;

        boolean contentEquals = Arrays.equals(buffer, bufferStart, bufferEnd, other.buffer, other.bufferStart, other.bufferEnd);
        return contentEquals
                && startPosition == other.startPosition
                && endPosition == other.endPosition
                && index == other.index
                && statementIndex == other.statementIndex;
    }

    @Override
    public String toString() {
        return String.format("\"%s\" [start=%d, end=%d, index=%d]", getValue(), startPosition, endPosition, index);
    }

    /**
     * Builder pattern.
     */
    static class Builder extends Token.Builder {

        Builder() {

        }

        Builder(Token token) {
            super(token);
        }

        @Override
        Phrase build() {
            return new Phrase(this);
        }
    }
}
