package landr.console;

import landr.cmd.OutputStyle;

import java.io.Console;
import java.io.IOException;

/**
 * Simple implementation of the interactive console with JVM {@link java.io.Console}. All outputs, including errors,
 * go to the standard output {@link System#out}.
 */
class SystemTerminal extends ConsoleTerminal {

    private final Console console;

    SystemTerminal() throws IOException {

        console = System.console();

        if (console == null) {
            throw new IOException("JVM has no console");
        }
    }

    @Override
    public String readLine() {
        return console.readLine(getPrompt());
    }

    @Override
    public void print(String message, OutputStyle style) {
        System.out.print(message);
    }

    @Override
    public void println(String message, OutputStyle style) {
        System.out.println(message);
    }

    @Override
    public void printVerbose(String message, OutputStyle style) {
        System.out.print(message);
    }

    @Override
    public void printlnVerbose(String message, OutputStyle style) {
        System.out.println(message);
    }

    @Override
    public void printError(String message, OutputStyle style) {
        System.out.print(message);
    }

    @Override
    public void printlnError(String message, OutputStyle style) {
        System.out.println(message);
    }

    @Override
    public void printStackTrace(Throwable error) {
        error.printStackTrace(System.out);
    }

    @Override
    public void flush() {
        System.out.flush();
    }
}
