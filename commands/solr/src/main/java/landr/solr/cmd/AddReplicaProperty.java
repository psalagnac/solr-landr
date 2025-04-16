package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Add a property to a single replica.
 * This invokes ADDREPLICAPROP Solr API.
 */
public class AddReplicaProperty extends SolrCommand {

    private final String collection;
    private final String shard;
    private final String replica;

    // the property to set
    private final String name;
    private final String value;

    public AddReplicaProperty(String collection, String shard, String replica, String name, String value) {
        this.collection = collection;
        this.shard = shard;
        this.replica = replica;
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.AddReplicaProp request = CollectionAdminRequest.addReplicaProperty(collection, shard, replica, name, value);

        processRequest(context, request);
    }

}
