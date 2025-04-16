package landr.parser;

import landr.cmd.Command;

/**
 * Pragma is a statement that is invoked during parser execution.
 */
abstract class Pragma {

    abstract void execute(ParserContext context);

    /**
     * Most of the time, pragmas will only change the parser behavior.
     * Id we also want to change the execution context later (when we actually run the command), a pragma can also
     * override this method to introduce a command in the execution chain.
     */
    Command getCommand() {
        return null;
    }
}
