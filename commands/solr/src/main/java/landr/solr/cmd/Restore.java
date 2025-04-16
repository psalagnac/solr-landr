package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

public class Restore extends SolrCommand {

    private final String collection;
    private final String backupName;
    private final String repository;
    private final String location;
    private final boolean async;

    public Restore(String collection, String backupName, String repository, String location, boolean async) {
        this.collection = collection;
        this.backupName = backupName;
        this.repository = repository;
        this.location =  location;
        this.async = async;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.Restore request = CollectionAdminRequest.restoreCollection(collection, backupName);
        request.setRepositoryName(repository);
        request.setLocation(location);

        if (async) {
            processAsyncRequest(context, request);
        } else {
            processRequest(context, request);
        }
    }

}
