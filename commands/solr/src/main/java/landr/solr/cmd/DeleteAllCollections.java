package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;
import java.util.List;

/**
 * Command to delete all collections from the cluster. We first retrieve the list of collection using admin API.
 */
public class DeleteAllCollections extends SolrCommand {

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        List<String> collections = CollectionAdminRequest.listCollections(client);

        for (String collection : collections) {
            DeleteCollection subCommand = new DeleteCollection(collection);
            subCommand.execute(context, client);
        }
    }

}
