package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;

/**
 * Simple query to Solr.
 */
public class Select extends SolrCommand {

    private final String collection;
    private final String query;

    public Select(String collection, String query) {
        this.collection = collection;
        this.query = query;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        SolrQuery query = new SolrQuery();
        query.setQuery(this.query);

        QueryRequest request = new QueryRequest(query);

        QueryResponse response = processRequest(context, request, collection);

        // Output number of hits
        long hits = response.getResults().getNumFound();
        context.println(String.format("Hits: %d", hits));
    }
}
