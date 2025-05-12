package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

public class RebalanceLeaders extends SolrAdminCommand {

    public RebalanceLeaders(String collection) {
        super(collection, false);
    }

    public RebalanceLeaders(Builder builder) {
        super(builder);
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.RebalanceLeaders request = CollectionAdminRequest.rebalanceLeaders(collection);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        public Builder(String collection) {
            super(collection);
        }
    }

}
