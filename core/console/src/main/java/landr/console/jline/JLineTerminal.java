package landr.console.jline;

import landr.cmd.CommandEnvironment;
import landr.cmd.FormatColor;
import landr.cmd.OutputStyle;
import landr.console.ConsoleTerminal;
import landr.parser.CommandRegistry;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JLineTerminal extends ConsoleTerminal {

    private final Terminal terminal;
    private final LineReader lineReader;

    /**
     * Create a new terminal that only support command outputs.
     */
    public JLineTerminal() throws IOException {
        terminal = createTerminal();
        lineReader = null;
    }

    /**
     * Create a new terminal for interactive execution.
     */
    public JLineTerminal(CommandEnvironment environment, CommandRegistry registry) throws IOException {
        terminal = createTerminal();
        lineReader = createLineReader(environment, registry);
    }

    /**
     * Create the instance of JLine terminal.
     */
    private Terminal createTerminal() throws IOException {

        return TerminalBuilder
                .builder()
                .build();
    }

    /**
     * Creates the instance of JLine line reader.
     */
    private LineReader createLineReader(CommandEnvironment environment, CommandRegistry registry) {

        Completer completer = new CommandCompleter(environment, registry);

        Path historyPath = prepareHistoryPath();

        LineReaderBuilder builder = LineReaderBuilder.builder();

        builder.terminal(terminal);
        builder.completer(completer);
        builder.option(LineReader.Option.MENU_COMPLETE, true);
        builder.option(LineReader.Option.AUTO_MENU, false);
        builder.option(LineReader.Option.AUTO_LIST, false);
        builder.option(LineReader.Option.COMPLETE_MATCHER_TYPO, false);

        // Skip history if we can't write use home dir
        if (historyPath != null) {
            builder.variable(LineReader.HISTORY_FILE, historyPath);
        }
        return builder.build();
    }

    /**
     * Create directory in user's home folder to store history.
     *
     * @return The path of the history file, or {@code null} to disable the feature.
     */
    private Path prepareHistoryPath() {

        String home = System.getProperty("user.home");
        Path historyPath = Path.of(home, ".solr-landr", "history");

        try {
            Files.createDirectories(historyPath.getParent());
            return historyPath;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String readLine() {
        try {
            return lineReader.readLine(getPrompt());
        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    @Override
    public void print(String message, OutputStyle style) {

        String output = createAnsiString(message, style);

        terminal.writer().print(output);
    }

    @Override
    public void println(String message, OutputStyle style) {

        String output = createAnsiString(message, style);

        terminal.writer().println(output);
        terminal.flush();
    }

    @Override
    public void printVerbose(String message, OutputStyle style) {

        AttributedStyle baseVerboseStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT);
        String output = createAnsiString(message, style, baseVerboseStyle);

        terminal.writer().print(output);
    }

    @Override
    public void printlnVerbose(String message, OutputStyle style) {

        AttributedStyle baseVerboseStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT);
        String output = createAnsiString(message, style, baseVerboseStyle);

        terminal.writer().println(output);
        terminal.writer().flush();
    }

    @Override
    public void printError(String message, OutputStyle style) {

        AttributedStyle baseErrorStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
        String output = createAnsiString(message, style, baseErrorStyle);

        terminal.writer().print(output);
    }

    @Override
    public void printlnError(String message, OutputStyle style) {

        AttributedStyle baseErrorStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
        String output = createAnsiString(message, style, baseErrorStyle);

        terminal.writer().println(output);
        terminal.writer().flush();
    }

    @Override
    public void printStackTrace(Throwable error) {
        error.printStackTrace(terminal.writer());
        terminal.flush();
    }

    @Override
    public void flush() {
        terminal.flush();
    }

    /**
     * Creates an ansi string to output a message with style into the terminal.
     */
    private String createAnsiString(String message, OutputStyle style) {

        if (style.isDefault()) {
            return message;
        } else {
            return createAnsiString(message, style, AttributedStyle.DEFAULT);
        }
    }

    /**
     * Creates an ansi string to output a message. The JLine style is used as a base. We then apply all
     * styles defined by the command.
     */
    private String createAnsiString(String message, OutputStyle style, AttributedStyle jlineStyle) {

        // apply custom style overrides
        if (style.isBold()) {
            jlineStyle = jlineStyle.bold();
        }
        if (style.isItalic()) {
            jlineStyle = jlineStyle.italic();
        }
        if (style.isUnderline()) {
            jlineStyle = jlineStyle.underline();
        }

        if (style.getColor() != FormatColor.DEFAULT) {
            jlineStyle = applyColor(style.getColor(), jlineStyle);
        }
        return new AttributedStringBuilder()
                .style(jlineStyle)
                .append(message)
                .toAnsi(terminal);
    }

    /**
     * Transform the formating style into an actual color.
     */
    private AttributedStyle applyColor(FormatColor color, AttributedStyle style) {

        switch (color) {

            // For now, colors are hard coded !
            case NULL:
                return style.foreground(0xd1, 0x2f, 0x1b);
            case STRING:
                return style.foreground(0x00, 0x65, 0x00);
            case NUMBER:
                return style.foreground(0x27, 0x2a, 0xd8);
            case BOOLEAN:
                return style.foreground(0xad, 0x3d, 0xa4);

            // no color
            case DEFAULT:
            default:
                return style;
        }

    }

}
