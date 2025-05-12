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
public class Backup extends SolrAdminCommand {

    private final String name;
    private final String repository;
    private final String location;

    public Backup(Builder builder) {
        super(builder);

        this.name = builder.name;
        this.repository = builder.repository;
        this.location =  builder.location;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.Backup request = CollectionAdminRequest.backupCollection(collection, name);
        request.setRepositoryName(repository);
        request.setLocation(location);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {
        private String name;
        private String repository;
        private String location;

        public Builder(String collection) {
            super(collection);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
