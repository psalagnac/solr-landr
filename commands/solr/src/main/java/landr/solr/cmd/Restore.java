package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

import java.io.IOException;

/**
 * Command to restore a SolrCloud collection from a snapshot.
 */
public class Restore extends SolrAdminCommand {

    private final String backupName;
    private final String repository;
    private final String location;

    public Restore(Builder builder) {
        super(builder);

        this.backupName = builder.backupName;
        this.repository = builder.repository;
        this.location =  builder.location;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        CollectionAdminRequest.Restore request = CollectionAdminRequest.restoreCollection(collection, backupName);
        request.setRepositoryName(repository);
        request.setLocation(location);

        processAdminRequest(context, request);
    }

    public static class Builder extends AdminCommandBuilder {

        private String backupName;
        private String repository;
        private String location;

        public Builder(String collection) {
            super(collection);
        }

        public void setBackupName(String backupName) {
            this.backupName = backupName;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
