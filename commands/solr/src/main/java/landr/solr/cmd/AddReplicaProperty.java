package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Add a property to a single replica.
 * This invokes ADDREPLICAPROP Solr API.
 */
public class AddReplicaProperty extends SolrAdminCommand {

    private final String shard;
    private final String replica;

    // the property to set
    private final String name;
    private final String value;

    public AddReplicaProperty(Builder builder) {
        super(builder);
        this.shard = builder.shard;
        this.replica = builder.replica;
        this.name = builder.name;
        this.value = builder.value;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.AddReplicaProp request = CollectionAdminRequest.addReplicaProperty(collection, shard, replica, name, value);

        processAdminRequest(context, request);
    }

    /**
     * Not a great builder since all parameters are required.
     */
    public static class Builder extends AdminCommandBuilder {

        private String shard;
        private String replica;
        private String name;
        private String value;

        public Builder(String collection) {
            super(collection);
        }

        public void setShard(String shard) {
            this.shard = shard;
        }

        public void setReplica(String replica) {
            this.replica = replica;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
