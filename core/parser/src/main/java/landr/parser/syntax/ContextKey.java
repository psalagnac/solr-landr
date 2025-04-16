package landr.parser.syntax;

public enum ContextKey {

    COLLECTION_NAME("collection");

    public final String name;

    ContextKey(String name) {
        this.name = name;
    }

}
