package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.ClusterStateProvider;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.GenericSolrRequest;
import org.apache.solr.common.cloud.DocCollection;
import org.apache.solr.common.cloud.Replica;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;

/**
 * Command to invoke {@link CoreAdminParams.CoreAdminAction#REJOINLEADERELECTION} on a replica.
 * This requires the Solr client to be a cloud aware client.
 */
public class RejoinShardElection extends SolrCommand {

    private final String collection;
    private final String core;
    private final boolean atHead;

    public RejoinShardElection(String collection, String core, boolean atHead) {
        this.collection = collection;
        this.core = core;
        this.atHead = atHead;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        CloudSolrClient cloudClient = toCloudClient(client);

        // Retrieve collection state from Zookeeper using the cloud aware client.
        ClusterStateProvider stateProvider = cloudClient.getClusterStateProvider();
        DocCollection docCollection = stateProvider.getClusterState().getCollection(collection);

        Replica replica = docCollection.getReplica(core);

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(CoreAdminParams.ACTION, CoreAdminParams.CoreAdminAction.REJOINLEADERELECTION.toString());
        params.set(CommonParams.QT, CommonParams.CORES_HANDLER_PATH);
        params.set("collection", collection);
        params.set("core_node_name", replica.getName());
        params.set("core", replica.getCoreName());
        params.set("rejoinAtHead", atHead);

        GenericSolrRequest request = new GenericSolrRequest(SolrRequest.METHOD.GET, CommonParams.CORES_HANDLER_PATH, params);

        // Build a new Solr client send a request directly to the replica
        try (SolrClient localClient = new Http2SolrClient.Builder(replica.getBaseUrl()).build()) {
            request.process(localClient);
        }
    }
}
