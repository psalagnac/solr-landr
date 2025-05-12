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
public class DeleteShard extends SolrAdminCommand {

    private final String shard;

    public DeleteShard(String collection, String shard) {
        super(collection, false);
        this.shard = shard;
    }

    public DeleteShard(Builder builder) {
        super(builder);
        this.shard = builder.shard;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.DeleteShard request = CollectionAdminRequest.deleteShard(collection, shard);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        private String shard;

        public Builder(String collection) {
            super(collection);
        }

        public void setShard(String shard) {
            this.shard = shard;
        }
    }
}
