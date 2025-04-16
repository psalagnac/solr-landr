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
public class MoveReplica extends SolrCommand {

    private final String collection;
    private final String replica;
    private final String node;

    public MoveReplica(String collection, String replica, String node) {
        this.collection = collection;
        this.replica = replica;
        this.node = node;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.MoveReplica request = CollectionAdminRequest.moveReplica(collection, replica, node);

        processRequest(context, request);
    }

}
