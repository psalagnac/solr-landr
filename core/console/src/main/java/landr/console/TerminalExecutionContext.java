package landr.console;

import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionContext;
import landr.cmd.OutputStyle;

/**
 * Execution context when running in interactive mode with a terminal.
 */
public class TerminalExecutionContext extends CommandExecutionContext {

    private final ConsoleTerminal terminal;

    public TerminalExecutionContext(CommandEnvironment environment, ConsoleTerminal terminal) {
        super(environment);
        this.terminal = terminal;
    }

    @Override
    public void print(String message, OutputStyle style) {
        terminal.print(message, style);
    }

    @Override
    public void println(String message, OutputStyle style) {
        terminal.println(message, style);
    }

    @Override
    public void printVerbose(String message, OutputStyle style) {
        terminal.printVerbose(message, style);
    }

    @Override
    public void printlnVerbose(String message, OutputStyle style) {
        terminal.printlnVerbose(message, style);
    }

    @Override
    public void printError(String message, OutputStyle style) {
        terminal.printError(message, style);
    }

    @Override
    public void printlnError(String message, OutputStyle style) {
        terminal.printlnError(message, style);
    }

    @Override
    public void printStackTrace(Throwable error) {
        terminal.printStackTrace(error);
    }

    @Override
    public void flush() {
        terminal.flush();
    }
}
