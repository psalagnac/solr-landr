package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Simple command to delete a collection in SolrCloud.
 */
public class DeleteCollection extends SolrCommand {

    private final String collection;

    public DeleteCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(collection);

        processRequest(context, request);
    }
}
