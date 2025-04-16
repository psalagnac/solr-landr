package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.Help;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Descriptor for the help command.
 */
class HelpCommandDescriptor extends CommandDescriptor<Command> {

    private static final String NAME = "help";

    private static final String COMMAND_PARAM = "command";

    private static final CommandSyntax SYNTAX;
    static {
        SYNTAX = new CommandSyntax(
            NAME, COMMAND_PARAM,
            new Argument(COMMAND_PARAM)
        );
    }

    /**
     * Singleton for the special help on the 'help' command.
     */
    static final Help HELP = new HelpHelp();

    private final CommandRegistry registry;

    HelpCommandDescriptor(CommandRegistry registry) {
        super(SYNTAX);

        this.registry = registry;
    }

    @Override
    public Completer getCompleter() {
        return new HelpCompleter();
    }

    @Override
    public Command buildCommand(CommandString string, ParserContext context) throws CommandParseException {

        String command = getArgumentValue(COMMAND_PARAM, string, context);

        if (command == null) {
            // No command name was specified. Run global help for all command
            return new GlobalHelpCommand(registry);
        } else {
            // Run help for a specific command
            return new CommandHelpCommand(command, registry);
        }
    }

    private class HelpCompleter implements Completer {

        @Override
        public List<Completion> completeArgument(CommandEnvironment env, String argument, String value, Map<String, String> others) {

            if (argument.equals(COMMAND_PARAM)) {

                // don't list all commands when input is empty
                if (value == null || value.isEmpty()) {
                    return null;
                }

                List<Completion> completions = new ArrayList<>();
                Set<String> names = registry.getCommandNames();
                for (String name : names) {
                    if (name.startsWith(value)) {
                        completions.add(new Completion(name));
                    }
                }
                return completions;
            }

            return null;
        }

    }

    /**
     * Help on 'help' command :-)
     */
    private static class HelpHelp implements Help {

        @Override
        public String getSection() {
            return null;
        }

        @Override
        public String getSummary() {
            return "Show this help.";
        }

        @Override
        public String getDetails() {
            return null;
        }

        @Override
        public List<String> getUsages() {
            return List.of("help", "help command");
        }
    }
}
