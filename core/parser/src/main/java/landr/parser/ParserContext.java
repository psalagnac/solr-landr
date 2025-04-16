package landr.parser;

import landr.parser.syntax.ContextKey;

import java.util.EnumMap;

public class ParserContext {

    private final boolean interactive;
    private final EnumMap<ContextKey, String> properties;

    private boolean debug;
    private boolean verbose;
    private boolean trace;

    public ParserContext(boolean interactive) {
        this.interactive = interactive;
        properties = new EnumMap<>(ContextKey.class);
    }

    /**
     * Whether we are directly interacting with user (console mode).
     */
    boolean isInteractive() {
        return interactive;
    }

    void setProperty(ContextKey key, String value) {
        properties.put(key, value);
    }

    String getProperty(ContextKey key) {
        return properties.get(key);
    }

    /**
     * Whether we are currently in verbose mode.
     */
    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Whether we are currently in debug mode.
     */
    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * When enabled, the parser adds an ewho command to print the name all the arguments
     * of the following command. This may be used only when executing a script.
     */
    void setTrace(boolean trace) {
        this.trace = trace;
    }

    boolean getTrace() {
        return trace;
    }

    static ContextKey resolveKey(ParserContext context, String name) throws CommandParseException {

        for (ContextKey key : ContextKey.values()) {

            if (key.name.equals(name)) {
                return key;
            }
        }

        throw new CommandParseException(context, String.format("unknown context key: %s", name));
    }
}
