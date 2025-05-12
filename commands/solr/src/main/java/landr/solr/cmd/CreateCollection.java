package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

import static org.apache.solr.client.solrj.request.CollectionAdminRequest.*;

/**
 * Simple command to create a new collection in SolrCloud.
 */
public class CreateCollection extends SolrAdminCommand {

    public enum Type {
        NRT,
        TLOG
    }

    private static final int DEFAULT_SHARDS = 1;
    private static final int DEFAULT_REPLICAS = 1;
    private static final Type DEFAULT_TYPE = Type.NRT;

    private final String config;
    private final String router;
    private final int shards;
    private final int replicas;
    private final Type type;

    /**
     * Simple constructor to create a collection with default parameters.
     */
    public CreateCollection(String collection) {
        super(collection, false);
        this.config = null;
        this.router = null;
        this.shards = DEFAULT_SHARDS;
        this.replicas = DEFAULT_REPLICAS;
        this.type = DEFAULT_TYPE;
    }

    public CreateCollection(Builder builder) {
        super(builder);

        this.config = builder.config;
        this.router = builder.router;
        this.shards = builder.shards;
        this.replicas = builder.replicas;
        this.type = builder.type;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        int nrtReplicas = type == Type.NRT ? replicas : 0;
        int tlogReplicas = type == Type.TLOG ? replicas : 0;

        Create request = CollectionAdminRequest.createCollection(collection, config, shards, nrtReplicas, tlogReplicas, null);

        if (router != null) {
            request.setRouterName(router);
        }

        processAdminRequest(context, request);
    }

    /**
     * Builder pattern for create-collection command.
     */
    public static class Builder extends AdminCommandBuilder {

        private String config;
        private String router;
        private int shards = DEFAULT_SHARDS;
        private int replicas = DEFAULT_REPLICAS;
        private Type type = DEFAULT_TYPE;

        public Builder(String collection) {
            super(collection);
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public void setRouter(String router) {
            this.router = router;
        }

        /**
         * Set number of shards for the collection being created. If the parameter
         * is zero or negative, we use {@link #DEFAULT_SHARDS} instead.
         */
        public void setShards(int shards) {
            if (shards <= 0) {
                this.shards = DEFAULT_SHARDS;
            } else {
                this.shards = shards;
            }
        }

        /**
         * Set number of replicas for the collection being created. If the parameter
         * is zero or negative, we use {@link #DEFAULT_REPLICAS} instead.
         */
        public void setReplicas(int replicas) {
            if (replicas <= 0) {
                this.replicas = DEFAULT_REPLICAS;
            } else {
                this.replicas = replicas;
            }
        }

        public void setType(Type type) {
            this.type = type != null ? type : DEFAULT_TYPE;
        }

    }
}
