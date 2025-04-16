package landr.parser.syntax;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Declarative class for the syntax of a single command.
 */
public class CommandSyntax extends Syntax {

    private final String defaultArgument;
    private final Map<String, Argument> arguments;

    public CommandSyntax(String name, Argument... arguments) {
        this(name, null, arguments);
    }

    public CommandSyntax(String name, String defaultArgument, Argument... arguments) {
        super(name);

        this.defaultArgument = defaultArgument;

        // keep order for syntax generic help
        this.arguments = new LinkedHashMap<>();
        for (Argument argument : arguments) {
            this.arguments.put(argument.getName(), argument);
        }
    }

    /**
     * Check if given argument name is defined.
     */
    public boolean hasArgument(String name) {
        return arguments.containsKey(name);
    }

    public Argument getArgument(String name) {
        return arguments.get(name);
    }

    public String getDefaultArgument() {
        return defaultArgument;
    }

    @Override
    public Set<String> getArgumentNames() {
        return arguments.keySet();
    }
    
}
