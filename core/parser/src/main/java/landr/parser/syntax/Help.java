package landr.parser.syntax;

import java.util.List;

/**
 * User facing help in interactive command execution.
 */
public interface Help {

    /**
     * Returns the name of the parent section of the command.
     * All the commands with the same section are grouped together in the global help.
     */
    String getSection();

    /**
     * One line help with the command goal.
     */
    String getSummary();

    /**
     * Return full help text, when the user asks for help scoped on the command.
     * If {@code null} is returned, we use the summary instead.
     */
    String getDetails();

    /**
     * This method can be overridden to use another usage list that default generated one.
     *
     * <p>By default, or if implementations return null, we generate usages based on the syntax.
     *
     * @return List of usages to override the ones generated from syntax.
     */
    default List<String> getUsages() {
        return null;
    }

}
