package landr.console;

import landr.cmd.OutputStyle;

/**
 * Abstraction on the low level terminal.
 * Default implementation {@link SystemTerminal} is just based on JVM console.
 */
public abstract class ConsoleTerminal {

    private static final String DEFAULT_PROMPT = "landr";
    private static final String PROMPT_SUFFIX = "> ";

    private String prompt;

    protected ConsoleTerminal() {
        setPrompt(DEFAULT_PROMPT);
    }

    public void setPrompt(String prompt) {

        if (prompt == null) {
            setPrompt(DEFAULT_PROMPT);
            return;
        }

        if (!prompt.endsWith(PROMPT_SUFFIX)) {
            prompt = prompt.trim();
        }

        // empty string, but not null
        if (prompt.isEmpty()) {
            this.prompt = PROMPT_SUFFIX;
            return;
        }

        // happy path: the suffix is specified
        if (prompt.endsWith(PROMPT_SUFFIX)) {
            this.prompt = prompt;
        }
        // otherwise, add suffix
        else {
            this.prompt = prompt + PROMPT_SUFFIX;
        }
    }

    public String getPrompt() {
        return prompt;
    }

    public abstract String readLine();

    /**
     * Print an info message, or normal command output.
     */
    public abstract void print(String message, OutputStyle style);

    /**
     * Print an info message, or normal command output.
     */
    public abstract void println(String message, OutputStyle style);

    /**
     * Print a message for verbose logging.
     */
    public abstract void printVerbose(String message, OutputStyle style);

    /**
     * Print a message for verbose logging.
     */
    public abstract void printlnVerbose(String message, OutputStyle style);

    /**
     * Print an error message.
     */
    public abstract void printError(String message, OutputStyle style);

    /**
     * Print an error message.
     */
    public abstract void printlnError(String message, OutputStyle style);

    /**
     * Print the full stack trace of an error. Called only when debug mode is enabled.
     */
    public abstract void printStackTrace(Throwable error);

    /**
     * Flush the terminal output. An incomplete line must be printed.
     */
    public abstract void flush();
}
