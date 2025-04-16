package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

public class RebalanceLeaders extends SolrCommand {

    private final String collection;

    public RebalanceLeaders(String collection) {
        this.collection = collection;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.RebalanceLeaders request = CollectionAdminRequest.rebalanceLeaders(collection);

        processRequest(context, request);
    }

}
