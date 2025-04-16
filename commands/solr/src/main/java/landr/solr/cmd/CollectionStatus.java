package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;

import java.io.IOException;

/**
 * This command display the raw status of a collection.
 * This invokes COLSTATUS Solr API.
 */
public class CollectionStatus extends SolrCommand {

    private final String collection;

    public CollectionStatus(String collection) {
        this.collection = collection;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.ColStatus request = CollectionAdminRequest.collectionStatus(collection);
        CollectionAdminResponse response = processRequest(context, request);
        Object status = response.getResponse().get(collection);

        printNamedObject(context, collection, status);
        context.println();
    }

}
