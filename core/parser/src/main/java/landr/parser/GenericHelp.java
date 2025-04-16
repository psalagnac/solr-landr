package landr.parser;

import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.Help;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generic help based on the syntax for commands that does not define any help.
 */
class GenericHelp implements Help {

    private final List<String> usages;

    GenericHelp(CommandDescriptor<?> descriptor) {
        usages = generateSyntaxUsages(descriptor.getSyntax());
    }

    @Override
    public String getSection() {
        return null;
    }

    @Override
    public String getSummary() {
        return null;
    }

    @Override
    public String getDetails() {
        return null;
    }

    @Override
    public List<String> getUsages() {
        return usages;
    }

    static List<String> generateSyntaxUsages(CommandSyntax syntax) {

        String shortSyntax = generateMinimalSyntaxUsage(syntax);
        String longSyntax = generateExtendedSyntaxUsage(syntax);

        List<String> usages = new ArrayList<>();
        usages.add(shortSyntax);
        if (!longSyntax.equals(shortSyntax)) {
            usages.add(longSyntax);
        }

        return usages;
    }

    /**
     * Generate usage with only required parameter, in short form.
     */
    private static String generateMinimalSyntaxUsage(CommandSyntax syntax) {
        StringBuilder builder = new StringBuilder();

        builder.append(syntax.getName());

        // collect only required arguments
        List<Argument> arguments = new ArrayList<>();
        for (String arg : syntax.getArgumentNames()) {
            Argument argument = syntax.getArgument(arg);
            if (argument.isRequired()) {
                arguments.add(syntax.getArgument(arg));
            }
        }

        // sort them in an order relevant for search

        for (Argument argument : arguments) {

            if (!argument.getName().equals(syntax.getDefaultArgument())) {
                builder.append(' ');
                builder.append('-');
                builder.append(argument.getName());
            }
            builder.append(' ');
            builder.append(argument.getName());
        }

        return builder.toString();
    }

    /**
     * Generate usage with all expanded parameter parameters.
     */
    private static String generateExtendedSyntaxUsage(CommandSyntax syntax) {

        StringBuilder builder = new StringBuilder();

        builder.append(syntax.getName());

        // first collect all arguments
        List<Argument> arguments = new ArrayList<>();
        for (String arg : syntax.getArgumentNames()) {
            arguments.add(syntax.getArgument(arg));
        }

        // sort them in an order relevant for search
        Comparator<Argument> comparator = Comparator.comparing(a -> !a.isRequired());
        arguments.sort(comparator);

        for (Argument argument : arguments) {
            builder.append(' ');
            if (!argument.isRequired()) {
                builder.append('[');
            }

            builder.append('-').append(argument.getName());
            builder.append(' ').append(argument.getName());

            if (!argument.isRequired()) {
                builder.append(']');
            }
        }

        return builder.toString();
    }

}
