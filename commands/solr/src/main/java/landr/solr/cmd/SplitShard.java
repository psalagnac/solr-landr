package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

public class SplitShard extends SolrCommand {

    private final String collection;
    private final String shard;

    public SplitShard(String collection, String shard) {
        this.collection = collection;
        this.shard = shard;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.SplitShard request = CollectionAdminRequest.splitShard(collection);
        request.setShardName(shard);

        processRequest(context, request);
    }

}
