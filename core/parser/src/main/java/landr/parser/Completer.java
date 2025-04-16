package landr.parser;

import landr.cmd.CommandEnvironment;

import java.util.List;
import java.util.Map;

/**
 * The completer can be optionally specified by a {@link CommandDescriptor} to dynamically
 * help the user when running in interactive mode.
 */
public interface Completer {

    /**
     * Return list of suggestions of an argument value.
     *
     * @param argument The resolved name of the argument being specified.
     * @param value    Current value of the argument in the command line. This the exact text
     *                 entered by the user, with no guarantee on data integrity.
     * @param others   The value of all other valid arguments specified on the command line. They may be used as
     *                 a context to complete the current completion.
     */
    List<Completion> completeArgument(CommandEnvironment env, String argument, String value, Map<String, String> others);

}
