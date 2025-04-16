package landr.parser;

import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.Help;
import landr.parser.syntax.Syntax;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * The list of all commands and pragmas available to an environment.
 */
public class CommandRegistry {

    private final Map<String, CommandDescriptor<?>> commands;
    private final Map<String, PragmaDescriptor<?>> pragmas;
    private final Map<String, Help> helps;


    public CommandRegistry() {
        commands = new LinkedHashMap<>();
        pragmas = new HashMap<>();
        helps = new HashMap<>();
    }

    /**
     * Register command with no help. Generic help with syntax
     * usage is generated from the descriptor.
     */
    public void registerCommand(CommandDescriptor<?> descriptor) {
        Help help = new GenericHelp(descriptor);
        registerCommand(descriptor, help);
    }

    /**
     * Register command with specific help.
     */
    public void registerCommand(CommandDescriptor<?> descriptor, Help help) {

        String name = descriptor.getName();
        if (commands.containsKey(name)) {
            throw new IllegalArgumentException("command name already registered: " + name);
        }

        commands.put(name, descriptor);

        if (help != null) {
            helps.put(name, help);
        }
    }

    void registerPragma(PragmaDescriptor<?> descriptor) {
        pragmas.put(descriptor.getName(), descriptor);
    }

    public CommandDescriptor<?> getCommand(String name) {
        return commands.get(name);
    }

    public PragmaDescriptor<?> getPragma(String name) {
        return pragmas.get(name);
    }

    /**
     * Return any available syntax.
     */
    public Syntax getSyntax(String name) {
        return getStatementAttribute(name, StatementDescriptor::getSyntax);
    }

    /**
     * Return the syntax for specified command, or null if the command is unknown.
     */
    public CommandSyntax getCommandSyntax(String name) {
        return getCommandAttribute(name, CommandDescriptor::getSyntax);
    }

    /**
     * Return any available help.
     */
    public Help getHelp(String name) {
        return helps.get(name);
    }

    /**
     * Convenience method to get a statement attribute, command or pragma (in order).
     */
    private <T> T getStatementAttribute(String name, Function<StatementDescriptor, T> func) {
        CommandDescriptor<?> command = getCommand(name);
        if (command != null) {
            return func.apply(command);
        }

        PragmaDescriptor<?> pragma = getPragma(name);
        if (pragma != null) {
            return func.apply(pragma);
        }

        return null;
    }

    /**
     * Convenience method to get a command attribute.
     * Return null if no command with this name is known.
     */
    private <T> T getCommandAttribute(String name, Function<CommandDescriptor<?>, T> func) {
        CommandDescriptor<?> command = getCommand(name);
        if (command != null) {
            return func.apply(command);
        } else {
            return null;
        }
    }

    /**
     * Returns an immutable view on all command names. Iteration order is identical to the command registration
     * order.
     */
    public Set<String> getCommandNames() {
        return Collections.unmodifiableSet(commands.keySet());
    }

    /**
     * Returns an immutable view on all pragma names.
     */
    public Set<String> getPragmaNames() {
        return Collections.unmodifiableSet(pragmas.keySet());
    }

}
