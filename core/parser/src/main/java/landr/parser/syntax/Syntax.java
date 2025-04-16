package landr.parser.syntax;

import java.util.Set;

/**
 * Abstract classes for all syntax declarations.
 */
public abstract class Syntax {

    private final String name;

    Syntax(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Set<String> getArgumentNames();
}
