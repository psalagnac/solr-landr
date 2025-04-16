package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.common.cloud.DocCollection;
import org.apache.solr.common.cloud.Replica;

import java.io.IOException;

/**
 * Add one or more replicas to a shard.
 */
public class AddReplica extends SolrCommand {

    private final String collection;
    private final String shard;
    private final Replica.Type type;

    public AddReplica(String collection, String shard) {
        this(collection, shard, null);
    }

    public AddReplica(String collection, String shard, Replica.Type type) {
        this.collection = collection;
        this.shard = shard;
        this.type = type;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        Replica.Type probedType = type != null ? type : probeDefaultReplicaType(client);
        CollectionAdminRequest.AddReplica request = CollectionAdminRequest.addReplicaToShard(collection, shard, probedType);

        processRequest(context, request);
    }

    /**
     * Probe default replica type for the collection.
     */
    private Replica.Type probeDefaultReplicaType(SolrClient client) {

        // This is a cloud command.
        // Let's assume the client is a SolrCloud one
        CloudSolrClient cloudClient = (CloudSolrClient) client;

        DocCollection state = cloudClient.getClusterState().getCollectionOrNull(collection);
        if (state == null) {
            // Solr will fail for us later
            return null;
        }

        return Replica.Type.NRT;
    }

}
