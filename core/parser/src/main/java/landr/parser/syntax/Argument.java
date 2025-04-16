package landr.parser.syntax;

public class Argument {

    private final String name;
    private final boolean required;
    private final ContextKey contextKey;
    private final String defaultValue;

    public Argument(String name) {
        this(name, false);
    }

    public Argument(String name, boolean required) {
        this(name, required, null);
    }

    public Argument(String name, String defaultValue) {
        this(name, false, null, defaultValue);
    }

    public Argument(String name, boolean required, ContextKey contextKey) {
        this(name, required, contextKey, null);
    }

    public Argument(String name, boolean required, ContextKey contextKey, String defaultValue) {
        this.name = name;
        this.required = required;
        this.contextKey = contextKey;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    /**
     * Return the key used to get eventual default value from the environment.
     */
    public ContextKey getContextKey() {
        return contextKey;
    }

    /**
     * Return default value, or {@code null} when none is declared.
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
