package landr.parser;

import java.util.Map;
import java.util.List;

public class CommandString {

    private final String commandName;
    private final boolean pragma;
    private final Map<String, String> arguments;
    private final List<String> parameters;

    private final CommandStringLayout layout;

    CommandString(String commandName, boolean pragma, Map<String, String> arguments, List<String> parameters, CommandStringLayout layout) {
        this.commandName = commandName;
        this.pragma = pragma;
        this.arguments = arguments;
        this.parameters = parameters;
        this.layout = layout;
    }

    /**
     * Return the command name, which just the first token.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Return an argument value, or {@code null} is unspecified in the command line. Context is ignored.
     */
    public String getArgumentValue(String argument) {
        return arguments.get(argument);
    }

    /**
     * Return the number of ordered parameters.
     */
    public int getParameterCount() {
        return parameters.size();
    }

    /**
     * Return value of ordered parameter.
     */
    public String getParameter(int index) {

        if (parameters.size() <= index) {
            return null;
        } else {
            return parameters.get(index);
        }
    }

    /**
     * Check whether this command is a directive for the parser.
     */
    public boolean isPragma() {
        return pragma;
    }

    /**
     * Return the layout of the tokens.
     */
    public CommandStringLayout getLayout() {
        return layout;
    }

    /**
     * Reverse parsing :-)
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        // command/pragma name
        if (pragma) {
            builder.append("@ ");
        }
        builder.append(commandName);

        // all argument names and values
        arguments.forEach((name, value) -> {
                builder.append(" -");
                builder.append(name);
                builder.append(" ");
                if (value.indexOf(' ') != -1) {
                    builder.append('"');
                }
                builder.append(value);
            if (value.indexOf(' ') != -1) {
                builder.append('"');
            }
            }
        );

        // all parameter values
        parameters.forEach(p -> {
            builder.append(" ");
            if (p.indexOf(' ') != -1) {
                builder.append('"');
            }
            builder.append(p);
            if (p.indexOf(' ') != -1) {
                builder.append('"');
            }
        });

        return builder.toString();
    }

    /**
     * Parse all the available statements, in order.
     */
    public static List<CommandString> parse(String string) {
        StringParser parser = new StringParser();
        return parser.parse(string);
    }

    /**
     * Parse the command line at the cursor position. If the input contains multiple statements, other ones are
     * ignored.
     */
    public static CommandString parse(String string, int cursor) {
        StringParser parser = new StringParser();
        return parser.parse(string, cursor);
    }

}
