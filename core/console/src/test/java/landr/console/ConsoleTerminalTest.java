package landr.console;

import landr.cmd.OutputStyle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsoleTerminalTest {

    /**
     * Check we always add correct suffix to a custom prompt.
     */
    @Test
    public void testPrompt() {

        ConsoleTerminal terminal = new NullTerminal();

        // empty prompt
        terminal.setPrompt("");
        assertEquals("> ", terminal.getPrompt());

        // no suffix
        terminal.setPrompt("test");
        assertEquals("test> ", terminal.getPrompt());

        // with useless whitespaces
        terminal.setPrompt("  test  ");
        assertEquals("test> ", terminal.getPrompt());

        // with suffix
        terminal.setPrompt("test> ");
        assertEquals("test> ", terminal.getPrompt());
    }

    /**
     * Terminal that does nothing, just for the test.
     */
    private static class NullTerminal extends ConsoleTerminal {

        @Override
        public String readLine() {
            return null;
        }

        @Override
        public void print(String message, OutputStyle style) {
        }

        @Override
        public void println(String message, OutputStyle style) {
        }

        @Override
        public void printVerbose(String message, OutputStyle style) {
        }

        @Override
        public void printlnVerbose(String message, OutputStyle style) {
        }

        @Override
        public void printError(String message, OutputStyle style) {
        }

        @Override
        public void printlnError(String message, OutputStyle style) {
        }

        @Override
        public void printStackTrace(Throwable error) {
        }

        @Override
        public void flush() {
        }
    }
}
