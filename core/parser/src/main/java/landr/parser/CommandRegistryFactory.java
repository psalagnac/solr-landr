package landr.parser;

import landr.cmd.CommandEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to build the command registry.
 * Mostly, we can for some {@link CommandRegistryEntry} in the classpath, so there is no
 * compile time dependency (low cost dependency injection).
 */
public class CommandRegistryFactory {

    private final boolean interactive;

    private final List<CommandRegistryEntry> entries;

    public CommandRegistryFactory(boolean interactive) {
        this.interactive = interactive;

        entries = new ArrayList<>();
    }

    /**
     * Create new descriptor instances for all the known commands and add them into a new
     * registry.
     */
    public CommandRegistry create(boolean registerDefaults) {

        CommandRegistry registry = new CommandRegistry();

        if (registerDefaults) {
            registerDefaults(registry);
        }

        for (CommandRegistryEntry entry : entries) {
            entry.register(registry);
        }

        return registry;
    }

    /**
     * Add all the default commands and pragmas to the registry.
     */
    private void registerDefaults(CommandRegistry registry) {

        // add help command
        if (interactive) {
            registry.registerCommand(new HelpCommandDescriptor(registry), HelpCommandDescriptor.HELP);
        }

        // default pragmas
        registry.registerPragma(DebugPragmaDescriptor.createDebugDescriptor());
        registry.registerPragma(DebugPragmaDescriptor.createVerboseDescriptor());
        registry.registerPragma(new ContextPragmaDescriptor());

        // trace pragma is available only when running a script
        if (!interactive) {
            registry.registerPragma(DebugPragmaDescriptor.createTraceDescriptor());
        }
    }

    /**
     * Register 'echo' command.
     * This is mostly for testing purposes.
     */
    public void addEchoCommand() {
        addCommand(new EchoCommandDescriptor());
    }

    private void addCommand(CommandDescriptor<?> descriptor) {
        SingleCommandEntry entry = new SingleCommandEntry(descriptor);
        addEntry(entry);
    }

    public void addEntry(CommandRegistryEntry entry) {
        entries.add(entry);
    }

    /**
     * Simple entry to register an already resolved command descriptor.
     * It is used for internal commands.
     */
    private static class SingleCommandEntry implements CommandRegistryEntry {

        private final CommandDescriptor<?> descriptor;

        private SingleCommandEntry(CommandDescriptor<?> descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public void init(CommandEnvironment environment) {
            // nothing to do
        }

        @Override
        public void register(CommandRegistry registry) {
            registry.registerCommand(descriptor);
        }
    }
}
