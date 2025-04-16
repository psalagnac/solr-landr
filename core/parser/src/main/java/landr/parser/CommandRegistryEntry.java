package landr.parser;

import landr.cmd.CommandEnvironment;

/**
 * Interface to be implemented by submodules that define extended commands.
 *
 * @see CommandRegistryFactory
 */
public interface CommandRegistryEntry {

    /**
     * In case {@link #register(CommandRegistry) register()}  method is invoked multiple times to build
     * multiple command registries, the {@code init()} command is invoked only once.
     *
     * <p>Global properties are available in the environment when invoking this method.
     */
    void init(CommandEnvironment environment);

    /**
     * Register all the known commands in specified registry.
     */
    void register(CommandRegistry registry);

}
