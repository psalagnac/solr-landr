package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Command to trigger a backup of a SolrCloud collection.
 */
public class Backup extends SolrCommand {

    private final String collection;
    private final String name;
    private final String repository;
    private final String location;
    private final boolean async;

    public Backup(String collection, String name, String repository, String location, boolean async) {
        this.collection = collection;
        this.name = name;
        this.repository = repository;
        this.location =  location;
        this.async = async;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException , CommandExecutionException {

        CollectionAdminRequest.Backup request = CollectionAdminRequest.backupCollection(collection, name);
        request.setRepositoryName(repository);
        request.setLocation(location);

        if (async) {
            processAsyncRequest(context, request);
        } else {
            processRequest(context, request);
        }
    }

}
