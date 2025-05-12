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
public class DeleteReplica extends SolrAdminCommand {

    private final String shard;
    private final String replica;

    public DeleteReplica(String collection, String shard, String replica) {
        super(collection, false);
        this.shard = shard;
        this.replica = replica;
    }

    public DeleteReplica(Builder builder) {
        super(builder);
        this.shard = builder.shard;
        this.replica = builder.replica;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.DeleteReplica request = CollectionAdminRequest.deleteReplica(collection, shard, replica);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        private String shard;
        private String replica;

        public Builder(String collection) {
            super(collection);
        }

        public void setShard(String shard) {
            this.shard = shard;
        }

        public void setReplica(String replica) {
            this.replica = replica;
        }
    }
}
