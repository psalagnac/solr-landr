package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;
import java.util.List;

/**
 * Solr command to print all collections in the cluster.
 */
public class ListCollections extends SolrCommand {

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException {

        List<String> collections = CollectionAdminRequest.listCollections(client);

        for (String collection : collections) {
            context.println(collection);
        }

    }

}
