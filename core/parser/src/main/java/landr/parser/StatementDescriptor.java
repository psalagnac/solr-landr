package landr.parser;

import landr.parser.syntax.Syntax;

public abstract class StatementDescriptor {

    private final Syntax syntax;

    protected StatementDescriptor(Syntax syntax) {
        this.syntax = syntax;
    }

    /**
     * Return the command name from syntax.
     */
    public String getName() {
        return syntax.getName();
    }

    public Syntax getSyntax() {
        return syntax;
    }
}
