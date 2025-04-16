package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import landr.parser.syntax.Help;

import java.util.List;

/**
 * Command to print command on a specific command.
 */
class CommandHelpCommand extends Command {

    private final String name;
    private final CommandRegistry registry;

    CommandHelpCommand(String name, CommandRegistry registry) {
        this.name = name;
        this.registry = registry;
    }

    @Override
    public void execute(CommandExecutionContext context) throws CommandExecutionException {

        // Check this command exists in the registry
        if (registry.getCommand(name) == null) {
            context.println(String.format("unknown command '%s'", name));
            return;
        }

        Help help = registry.getHelp(name);

        // if the command has no help, we can't do anything
        if (help == null) {
            context.println(String.format("no help for command '%s'", name));
            return;
        }

        // normal help
        printUsages(help, context);
        printSummary(help, context);
    }

    /**
     * Print command usages.
     * If the help does not specify usages, we generate them from the syntax.
     */
    public void printUsages(Help help, CommandExecutionContext context) {

        List<String> usages = help.getUsages();
        if (usages == null) {
            usages = GenericHelp.generateSyntaxUsages(registry.getCommandSyntax(name));
        }

        printUsages(usages, context);
    }

    /**
     * Print specified command usages.
     */
    private void printUsages(List<String> usages, CommandExecutionContext context) {
        context.print("usage: ");
        context.println(usages.get(0));

        List<String> remainingUsages = usages.subList(1, usages.size());
        for (String usage : remainingUsages) {
            context.print("       ");
            context.println(usage);
        }
    }

    /**
     * Print command summary, if any.
     */
    private void printSummary(Help help, CommandExecutionContext context) {

        String details = help.getDetails();
        String summary = help.getSummary();

        context.println();
        if (details != null) {
            context.print(details);
        }
        else if (summary != null) {
            context.println(help.getSummary());
        } else {
            context.println("No help found.");
        }
        context.println();
    }



}
