package landr.console;

import landr.cmd.CommandEnvironment;
import landr.console.jline.JLineTerminal;
import landr.parser.CommandRegistry;

import java.io.IOException;

/**
 * Utility class to build the output terminal.
 */
public class TerminalFactory {

    private boolean jline;
    private String prompt;

    public TerminalFactory(boolean jline) {
        this.jline = jline;
    }

    /**
     * Enable JLine. This is the default when running in interactive mode.
     */
    public void enableJLine() {
        jline = true;
    }

    /**
     * Disable JLine. This is the default when running a script.
     */
    public void disableJLine() {
        jline = false;
    }

    /**
     * Set a custom prompt to the terminal, or {@code null} to disable it. It is used only is the terminal
     * is created in interactive mode.
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Create a new terminal that can only print command outputs. User inputs cannot be read.
     */
    public ConsoleTerminal createOutputTerminal() throws IOException {
        if (jline) {
            return new JLineTerminal();
        } else {
            return new SystemTerminal();
        }
    }

    /**
     * Create a new terminal for interactive command execution. It supports both printing command outputs and
     * reading user inputs.
     */
    public ConsoleTerminal createInteractiveTerminal(CommandEnvironment environment, CommandRegistry registry) throws IOException {

        ConsoleTerminal terminal;
        if (jline) {
            terminal = new JLineTerminal(environment, registry);
        } else {
            terminal = new SystemTerminal();
        }

        if (prompt != null) {
            terminal.setPrompt(prompt);
        }

        return terminal;
    }

}
