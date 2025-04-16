package landr.cmd;

import java.util.Random;

/**
 * Context shared between all command executions in the same sequence (script or interactive mode).
 */
public abstract class CommandExecutionContext {

    private final CommandEnvironment environment;

    private boolean verbose;
    private boolean debug;

    // a unique number for each context
    private final int session;
    private int nextId;

    protected CommandExecutionContext(CommandEnvironment environment) {
        this.environment = environment;

        Random random = environment.getRandom();
        session = random.nextInt(10000);
    }

    public CommandEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Whether commands should output verbose level output.
     */
    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Whether command execution is currently in debug mode.
     */
    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Random instance to be used, so script executions may be reproduced by specifying the seed.
     */
    public Random getRandom() {
        return environment.getRandom();
    }

    /**
     * Generate a unique ID, mostly by Solr async requests.
     */
    public String generateId() {

        nextId++;
        return String.format("%04d_%04d", session, nextId);

    }

    /**
     * Print an info message, or normal command output.
     */
    public void print(String message) {
        print(message, OutputStyle.DEFAULT);
    }

    /**
     * Print an info message with specified style.
     */
    public abstract void print(String message, OutputStyle style);

    /**
     * Print a new line feed.
     */
    public void println() {
        println("");
    }

    /**
     * Print an info message, or normal command output.
     */
    public void println(String message) {
        println(message, OutputStyle.DEFAULT);
    }

    /**
     * Print an info message with specified style.
     */
    public abstract void println(String message, OutputStyle style);

    /**
     * Print a message for verbose logging. Note this message is always
     *      * printed, even if verbose is not enabled. The call should check {@link #getVerbose()} first.
     */
    public void printVerbose(String message) {
        printVerbose(message, OutputStyle.DEFAULT);
    }

    /**
     * Print a message with specified style for verbose logging. Note this message is always
     * printed, even if verbose is not enabled. The call should check {@link #getVerbose()} first.
     */
    public abstract void printVerbose(String message, OutputStyle style);

    /**
     * Print a message for verbose logging. Note this message is always
     * printed, even if verbose is not enabled. The call should check {@link #getVerbose()} first.
     */
    public void printlnVerbose(String message) {
        printlnVerbose(message, OutputStyle.DEFAULT);
    }

    /**
     * Print a message with specified style for verbose logging. Note this message is always
     * printed, even if verbose is not enabled. The call should check {@link #getVerbose()} first.
     */
    public abstract void printlnVerbose(String message, OutputStyle style);

    /**
     * Print an error message.
     */
    public void printError(String message) {
        printlnError(message, OutputStyle.DEFAULT);
    }

    /**
     * Print an error message with specified style.
     */
    public abstract void printError(String message, OutputStyle style);

    /**
     * Print a new line feed in error output.
     */
    public void printlnError() {
        printlnError("");
    }

    /**
     * Print an error message.
     */
    public void printlnError(String message) {
        printlnError(message, OutputStyle.DEFAULT);
    }

    /**
     * Print an error message with specified style.
     */
    public abstract void printlnError(String message, OutputStyle style);

    /**
     * Print the full stack trace of an error. Called only when debug mode is enabled.
     */
    public abstract void printStackTrace(Throwable error);

    /**
     * Flush all outputs. This force all internals to be printed.
     *
     * @see #print(String)
     */
    public abstract void flush();

}
