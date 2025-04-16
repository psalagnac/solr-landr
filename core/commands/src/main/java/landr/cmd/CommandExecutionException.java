package landr.cmd;

/**
 * Exception raised for all errors during command execution that can be
 * handled.
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(Throwable cause) {
        super(cause);
    }

    /**
     * This constructor should remain package private.
     * We don't want command modules to raise random exceptions.
     */
    CommandExecutionException(String message) {
        super(message);
    }

}
