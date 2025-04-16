package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.V2Request;

import java.io.IOException;

/**
 * Class for a generic API V2 call.
 */
public class V2RequestCommand extends SolrCommand {

    private final String resource;
    private final String payload;

    public V2RequestCommand(String resource, String payload) {
        this.resource = resource;
        this.payload = payload;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client) throws SolrServerException, IOException, CommandExecutionException {

        V2Request.Builder builder = new V2Request.Builder(resource);
        builder.withMethod(SolrRequest.METHOD.POST);
        builder.withPayload(payload);

        V2Request request = builder.build();
        request.process(client);
    }

}
