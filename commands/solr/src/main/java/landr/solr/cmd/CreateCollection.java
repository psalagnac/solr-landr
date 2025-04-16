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
public class CreateCollection extends SolrCommand {

    public enum Type {
        NRT,
        TLOG
    }

    private static final Type DEFAULT_TYPE = Type.NRT;

    private final String collection;
    private final String config;
    private final String router;
    private final int shards;
    private final Type type;
    private final int replicas;

    public CreateCollection(String collection, String config) {
        this(collection, config, 1, DEFAULT_TYPE, 1);
    }

    public CreateCollection(String collection, String config, int shards, Type type, int replicas) {
        this(collection, config, null, shards, type, replicas);
    }

    public CreateCollection(String collection, String config, String router, int shards, Type type, int replicas) {
        this.collection = collection;
        this.config = config;
        this.router = router;
        this.shards = shards;
        this.replicas = replicas;
        this.type = type;
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

        processRequest(context, request);
    }

}
