package landr.solr.cmd;

public abstract class SolrDataCommand extends SolrCommand {

    protected final String collection;

    protected SolrDataCommand(DataCommandBuilder builder) {
        this.collection = builder.collection;
    }

    protected SolrDataCommand(String collection) {
        this.collection = collection;
    }

    public static abstract class DataCommandBuilder {

        // The collection name
        public final String collection;

        protected DataCommandBuilder(String collection) {
            this.collection = collection;
        }
    }

}
