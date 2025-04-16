package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Delete a single replica in a collection.
 */
public class DeleteReplica extends SolrCommand {

    private final String collection;
    private final String shard;
    private final String replica;

    public DeleteReplica(String collection, String shard, String replica) {
        this.collection = collection;
        this.shard = shard;
        this.replica = replica;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.DeleteReplica request = CollectionAdminRequest.deleteReplica(collection, shard, replica);

        processRequest(context, request);
    }

}
