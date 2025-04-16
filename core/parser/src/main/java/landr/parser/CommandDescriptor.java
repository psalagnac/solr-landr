package landr.parser;

import landr.cmd.Command;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

import java.util.*;

public abstract class CommandDescriptor<T extends Command> extends StatementDescriptor {

    private final CommandSyntax syntax;

    protected CommandDescriptor(CommandSyntax syntax) {
        super(syntax);
        this.syntax = syntax;
    }

    @Override
    public CommandSyntax getSyntax() {
        return syntax;
    }

    /**
     * Can be overridden by subclasses to add custom completion to the command.
     */
    public Completer getCompleter() {
        return null;
    }

    protected String getArgumentValue(String name, CommandString string, ParserContext context) throws CommandParseException {

        Argument argument = syntax.getArgument(name);

        // Unknown argument (bad command implementation)
        if (argument == null) {
            throw new CommandParseException(context, String.format("undeclared argument: %s", name));
        }

        // get the argument value from the command line
        String value = string.getArgumentValue(name);
        if (value != null) {
            return value;
        }

        // if the argument is the default argument, also check the first parameter (implicit argument)
        if (name.equals(syntax.getDefaultArgument())) {
            value = string.getParameter(0);
            if (value != null) {
                return value;
            }
        }

        // get the value from the context
        value = context.getProperty(argument.getContextKey());
        if (value != null) {
            return value;
        }

        // get the argument argument default value when nothing else is defined
        value = argument.getDefaultValue();
        if (value != null) {
            return value;
        }

        if (argument.isRequired()) {
            throw new MissingArgumentException(context, name);
        }

        return null;
    }

    /**
     * Returns an argument value as a boolean. Recognized values are <i>true</i>, <i>false</i>, <i>yes</i>
     * and <i>no</i>.
     * An {@link ArgumentFormatException} is raised if the actual value is not a valid boolean.
     */
    protected boolean getArgumentBooleanValue(String name, CommandString string, ParserContext context) throws CommandParseException {

        String value = getArgumentValue(name, string, context);

        // Don't use Boolean.parseBoolean() since it ignores mal formatted values
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
            return true;
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
            return false;
        }

        throw new ArgumentFormatException(context, name, "boolean expected");
    }

    /**
     * Returns an argument value as an integer.
     * An {@link ArgumentFormatException} is raised if the actual value is not a valid integer.
     */
    protected int getArgumentIntegerValue(String name, CommandString string, ParserContext context) throws CommandParseException {

        String value = getArgumentValue(name, string, context);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentFormatException(context, name, "integer expected");
        }
    }


    /**
     * Returns an argument value converted into an enum instance.
     * An {@link ArgumentFormatException} is raised if the actual value cannot be converted in an instance of
     * the specified enum class.
     */
    protected <E extends Enum<E>> E getArgumentEnumValue(String name, CommandString string, ParserContext context, Class<E> clazz) throws CommandParseException {

        String value = getArgumentValue(name, string, context);
        if (value == null) {
            return null;
        }

        E[] enumValues = clazz.getEnumConstants();
        for (E enumValue : enumValues) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }

        throw new ArgumentFormatException(context, name, "bad value: " + value);
    }

    /**
     * Returns canonical version of the parsed string. All arguments values from the context and/or defaults
     * values are added to the canonical form.
     *
     * @param context  When not {@code null}, arguments values that are not in the original command string, but
     *                 are available in the context are added to the canonical string using the context value.
     * @param defaults When {@code true), argument values that are not in the original command string, but that
     *                 have a default value in the syntax, are added to the canonical string with default value.
     */
    CommandString getCanonicalString(CommandString string, ParserContext context, boolean defaults) {

        // Don't expand anything for pragmas
        if (string.isPragma()) {
            return string;
        }

        Map<String, String> arguments = new LinkedHashMap<>();
        for (String argument : syntax.getArgumentNames()) {
            String value = getCanonicalArgumentValue(argument, string, context, defaults);
            if (value != null) {
                arguments.put(argument, value);
            }
        }

        List<String> parameters = new ArrayList<>();
        for (int i=0; i<string.getParameterCount(); i++) {

            if (i == 0) {
                String defaultArgument =  syntax.getDefaultArgument();
                if (defaultArgument != null && string.getArgumentValue(defaultArgument) == null) {
                    continue;
                }
            }
            parameters.add(string.getParameter(i));
        }

        return new CommandString(string.getCommandName(), false, arguments, parameters, string.getLayout());
    }

    /**
     * Get an argument value for canonical string, or {@code null} of no value is available.
     */
    private String getCanonicalArgumentValue(String name, CommandString string, ParserContext context, boolean defaults) {

        Argument argument = syntax.getArgument(name);

        // get the argument value from the command line
        String value = string.getArgumentValue(name);
        if (value != null) {
            return value;
        }

        // if the argument is the default argument, also check the first parameter (implicit argument)
        if (name.equals(syntax.getDefaultArgument())) {
            value = string.getParameter(0);
            if (value != null) {
                return value;
            }
        }

        // get the value from the context
        if (context != null) {
            value = context.getProperty(argument.getContextKey());
            if (value != null) {
                return value;
            }
        }

        // get the argument argument default value when nothing else is defined
        if (defaults) {
            value = argument.getDefaultValue();
        }

        return value;
    }

    public abstract T buildCommand(CommandString string, ParserContext context) throws CommandParseException;

}
