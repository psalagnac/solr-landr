package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Move a replica between Solr nodes.
 */
public class MoveReplica extends SolrAdminCommand {

    private final String replica;
    private final String node;

    public MoveReplica(String collection, String replica, String node) {
        super(collection, false);
        this.replica = replica;
        this.node = node;
    }

    public MoveReplica(Builder builder) {
        super(builder);
        this.replica = builder.replica;
        this.node = builder.node;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.MoveReplica request = CollectionAdminRequest.moveReplica(collection, replica, node);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        private String replica;
        private String node;

        public Builder(String collection) {
            super(collection);
        }

        public void setReplica(String replica) {
            this.replica = replica;
        }

        public void setNode(String node) {
            this.node = node;
        }
    }
}
