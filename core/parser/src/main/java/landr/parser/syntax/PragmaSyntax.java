package landr.parser.syntax;

import java.util.Set;

public class PragmaSyntax extends Syntax {

    public static final char PRAGMA_PREFIX = '@';

    public PragmaSyntax(String name) {
        super(name);
    }

    @Override
    public Set<String> getArgumentNames() {

        // no argument by default
        return null;
    }
}
