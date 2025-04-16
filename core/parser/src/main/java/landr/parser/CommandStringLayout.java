package landr.parser;

import landr.parser.tokenizer.Token;

/**
 * This class has metadata describing an instance of {@link CommandString}.
 */
public class CommandStringLayout {

    private final int index;
    private final int commandNameIndex;
    private final int commandNameStartPosition;
    private final int commandNameEndPosition;

    CommandStringLayout(Builder builder) {
        this.index = builder.index;
        this.commandNameIndex = builder.commandNameIndex;
        this.commandNameStartPosition = builder.commandNameStartPosition;
        this.commandNameEndPosition = builder.commandNameEndPosition;
    }

    /**
     * Returns the position of the statement in the command line. This is always {@code 0} if there is a single
     * statement.
     *
     * <p>This is not equivalent to the position of the parsed command since this also includes empty statements.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Return the position in the input command string of the command name. This is always {@code 0} if
     * there is a single statement in input string.
     */
    public int getCommandNameIndex() {
        return commandNameIndex;
    }

    /**
     * Return the position of the first character of the command name in input string.
     */
    public int getCommandNameStartPosition() {
        return commandNameStartPosition;
    }

    /**
     * Return the position of the last character of the command name in input string.
     */
    public int getCommandNameEndPosition() {
        return commandNameEndPosition;
    }

    /**
     * Builder pattern.
     */
    static class Builder {

        private int index = -1;
        private int commandNameIndex = -1;
        private int commandNameStartPosition = -1;
        private int commandNameEndPosition = -1;

        CommandStringLayout build() {
            return new CommandStringLayout(this);
        }

        /**
         * Start a new statement during the parsing phase.
         */
        void startStatement(Token name, int index) {
            commandNameIndex = name.getIndex();
            commandNameStartPosition = name.getStartPosition();
            commandNameEndPosition = name.getEndPosition();
            this.index = index;
        }
    }
}
