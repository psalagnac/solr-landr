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
public class DeleteCollection extends SolrAdminCommand {

    /**
     * Simple constructor for a synchronous request.
     */
    public DeleteCollection(String collection) {
        super(collection, false);
    }

    public DeleteCollection(Builder builder) {
        super(builder);
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(collection);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        public Builder(String collection) {
            super(collection);
        }
    }
}
