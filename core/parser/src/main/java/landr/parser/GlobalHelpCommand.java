package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandExecutionContext;
import landr.cmd.OutputStyle;
import landr.parser.syntax.Help;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Default help command, that displays a short for help for each registered command.
 */
class GlobalHelpCommand extends Command {

    private static final int NAME_COLUMN_LENGTH = 18;

    private final CommandRegistry registry;

    GlobalHelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(CommandExecutionContext context) {

        Set<String> names = registry.getCommandNames();

        context.println("Solr Landr " + getClass().getPackage().getImplementationVersion());
        context.println();

        // We want to preserve section order (linked map)
        Map<String, List<String>> sections = new LinkedHashMap<>();
        List<String> nakedCommands = new ArrayList<>();

        for (String name : names) {
            Help help = registry.getHelp(name);

            // Handle only commands with a specific help
            if (help == null || (help instanceof  GenericHelp)) {
                nakedCommands.add(name);
            } else {
                String section = help.getSection();

                if (section == null) {
                    section = "";
                }
                List<String> list = sections.computeIfAbsent(section, s -> new ArrayList<>());
                list.add(name);
            }
        }

        // Make sure commands with no section are at the end
        if (sections.containsKey("")) {
            sections.put("", sections.remove(""));
        }

        // Print help for all sections
        for (Map.Entry<String, List<String>> entry : sections.entrySet()) {
            context.println(entry.getKey(), OutputStyle.UNDERLINE);
            for (String name : entry.getValue()) {
                Help help = registry.getHelp(name);
                printCommandHelp(name, help, context);
            }
            context.println();
        }

        // Print the list of all commands with no documentation.
        if (!nakedCommands.isEmpty()) {
            context.println();
            context.println("Undocumented: " + nakedCommands);
            context.println();
        }

        context.println();
        context.println("Run 'help <command>' to get full help on a specific command.");
        context.println();
    }

    /**
     * Print help for a single command.
     */
    private void printCommandHelp(String name, Help help, CommandExecutionContext context) {

        if (name.length() > NAME_COLUMN_LENGTH) {
            context.println(name);
            context.println(pad(NAME_COLUMN_LENGTH + 1) + help.getSummary());
        } else {
            int padLength = NAME_COLUMN_LENGTH + 1 - name.length();
            context.println(name + pad(padLength) + help.getSummary());
        }
    }

    private static String pad(int length) {
        char[] pad = new char[length];
        Arrays.fill(pad, ' ');
        return new String(pad);
    }
}
