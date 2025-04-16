package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Set a single property on the cluster.
 * Solr Cloud Api: CLUSTERPROP.
 */
public class SetClusterProperty extends SolrCommand {

    private final String name;
    private final String value;

    public SetClusterProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.ClusterProp request = CollectionAdminRequest.setClusterProperty(name, value);

        processRequest(context, request);
    }

}
