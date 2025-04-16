package landr.parser;

public class Completion {

    private final String value;
    private final String display;
    private final boolean complete;

    public Completion(String value) {
        this(value, value, true);
    }

    public Completion(String value, String display, boolean complete) {
        this.value = value;
        this.display = display;
        this.complete = complete;
    }

    /**
     * Actual argument value in the command line.
     */
    public String getValue() {
        return value;
    }

    /**
     * What is shown in the dynamic menu, if any.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Whether the suggestion is ended, so we add a whitespace to the command line.
     */
    public boolean isComplete() {
        return complete;
    }
}
