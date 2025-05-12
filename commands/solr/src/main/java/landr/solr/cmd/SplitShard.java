package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

public class SplitShard extends SolrAdminCommand {

    private final String shard;

    public SplitShard(String collection, String shard) {
        super(collection, false);
        this.shard = shard;
    }

    public SplitShard(Builder builder) {
        super(builder);
        this.shard = builder.shard;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.SplitShard request = CollectionAdminRequest.splitShard(collection);
        request.setShardName(shard);

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
