package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Abstract base class for Solr admin commands that handle collection operations.
 */
public abstract class SolrAdminCommand extends SolrCommand {

    protected final String collection;
    protected final boolean async;

    protected SolrAdminCommand(AdminCommandBuilder builder) {
        this.collection = builder.collection;
        this.async = builder.async;
    }

    protected SolrAdminCommand(String collection, boolean async) {
        this.collection = collection;
        this.async = async;
    }

    @Override
    public abstract void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException;

    /**
     * Process a collection admin request, handling both synchronous and asynchronous cases.
     */
    protected void processAdminRequest(CommandExecutionContext context, CollectionAdminRequest.AsyncCollectionAdminRequest request)
        throws SolrServerException, IOException, CommandExecutionException {
        
        if (async) {
            processAsyncRequest(context, request);
        } else {
            processRequest(context, request);
        }
    }

    /**
     * Utility method to send an asynchronous command to the Solr cluster. The async ID is generated and printed.
     * Using this method instead of directly calling SolrJ is preferred.
     */
    protected void processAsyncRequest(CommandExecutionContext context, CollectionAdminRequest.AsyncCollectionAdminRequest request)
            throws SolrServerException, IOException, CommandExecutionException {

        String id = context.generateId();
        request.setAsyncId(id);
        context.println("ID: " + id);

        processRequest(context, request);
    }

    public static abstract class AdminCommandBuilder {

        // The collection name
        public final String collection;

        protected boolean async;

        protected AdminCommandBuilder(String collection) {
            this.collection = collection;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }
}