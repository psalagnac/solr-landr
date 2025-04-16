package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Delete an inactive shard from a collection.
 */
public class DeleteShard extends SolrCommand {

    private final String collection;
    private final String shard;

    public DeleteShard(String collection, String shard) {
        this.collection = collection;
        this.shard = shard;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.DeleteShard request = CollectionAdminRequest.deleteShard(collection, shard);

        processRequest(context, request);
    }

}
