package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.RequestStatusState;

import java.io.IOException;

/**
 * Command to check status of an async request in SolrCloud.
 */
public class RequestStatus extends SolrCommand {

    private final String id;
    private final boolean keep;

    public RequestStatus(String id, boolean keep) {
        this.id = id;
        this.keep = keep;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        RequestStatusState state = readState(context);
        context.println(state.getKey());

        // if the request is complete, delete the status
        if (!keep && isEnded(state)) {
            deleteState(client);
        }
    }

    /**
     * Read the request state from Solr.
     */
    private RequestStatusState readState(CommandExecutionContext context) throws SolrServerException, IOException, CommandExecutionException {
        CollectionAdminRequest.RequestStatus request = CollectionAdminRequest.requestStatus(id);

        CollectionAdminRequest.RequestStatusResponse response = processRequest(context, request);
        return response.getRequestStatus();
    }

    /**
     * Delete the request state in Solr.
     */
    private void deleteState(SolrClient client) throws SolrServerException, IOException {
        CollectionAdminRequest.DeleteStatus request = CollectionAdminRequest.deleteAsyncId(id);

        request.process(client);
    }

    private static boolean isEnded(RequestStatusState state) {
        return state == RequestStatusState.COMPLETED || state == RequestStatusState.FAILED;
    }
}
