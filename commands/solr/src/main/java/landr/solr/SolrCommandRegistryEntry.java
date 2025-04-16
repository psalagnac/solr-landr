package landr.solr;

import landr.cmd.CommandEnvironment;
import landr.parser.CommandRegistry;
import landr.parser.JacksonCommandRegistryEntry;
import landr.solr.cmd.DeleteAllCollections;
import landr.solr.cmd.ListCollections;

import landr.solr.parser.AddReplicaDescriptor;
import landr.solr.parser.AddReplicaPropertyDescriptor;
import landr.solr.parser.CreateCollectionDescriptor;
import landr.solr.parser.DeleteCollectionDescriptor;
import landr.solr.parser.DeleteReplicaDescriptor;
import landr.solr.parser.DeleteShardDescriptor;
import landr.solr.parser.MoveReplicaDescriptor;
import landr.solr.parser.RebalanceLeadersDescriptor;
import landr.solr.parser.RejoinShardElectionDescriptor;
import landr.solr.parser.RequestStatusDescriptor;
import landr.solr.parser.SetClusterPropertyDescriptor;
import landr.solr.parser.SplitShardDescriptor;
import landr.solr.parser.UpdateDescriptor;
import landr.solr.parser.BackupDescriptor;
import landr.solr.parser.CollectionStatusDescriptor;
import landr.solr.parser.RestoreDescriptor;
import landr.solr.parser.SelectDescriptor;
import landr.solr.parser.NoArgumentDescriptor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import java.util.Collections;
import java.util.Optional;

/**
 * Entry for all the base Solr commands.
 * This entry is always loaded when initializing the console.
 */
public class SolrCommandRegistryEntry extends JacksonCommandRegistryEntry {

    private static final String HELP_FOLDER = "command-help";

    public SolrCommandRegistryEntry() {
        super(HELP_FOLDER);
    }

    @Override
    public void init(CommandEnvironment environment) {

        String zkHost = environment.getProperty("zkhost");
        String zkRoot = environment.getProperty("zkroot");

        SolrClient client = createSolrClient(zkHost, zkRoot);
        environment.addObject(client);
    }

    @Override
    public void register(CommandRegistry registry) {

        // collection management
        registerCommand(registry, new CreateCollectionDescriptor());
        registerCommand(registry, new DeleteCollectionDescriptor());
        registerCommand(registry, new NoArgumentDescriptor<>("list-collections", ListCollections.class));
        registerCommand(registry, new NoArgumentDescriptor<>("delete-all-collections", DeleteAllCollections.class));
        registerCommand(registry, new BackupDescriptor());
        registerCommand(registry, new RestoreDescriptor());
        registerCommand(registry, new CollectionStatusDescriptor());

        // shard management
        registerCommand(registry, new DeleteShardDescriptor());
        registerCommand(registry, new SplitShardDescriptor());

        // replica management
        registerCommand(registry, new AddReplicaDescriptor());
        registerCommand(registry, new MoveReplicaDescriptor());
        registerCommand(registry, new DeleteReplicaDescriptor());
        registerCommand(registry, new AddReplicaPropertyDescriptor());
        registerCommand(registry, new SetClusterPropertyDescriptor());
        registerCommand(registry, new RebalanceLeadersDescriptor());

        registerCommand(registry, new RequestStatusDescriptor());

        // core level commands, not exposed in public API
        registerCommand(registry, new RejoinShardElectionDescriptor());

        // basic Solr usage
        registerCommand(registry, new UpdateDescriptor());
        registerCommand(registry, new SelectDescriptor());
    }

    private static SolrClient createSolrClient(String zkHost, String zkRoot) {

        // Set system properties to configure ZK security for Solr
        // Even if we don't add security rules, this is sufficient to silent warnings
        System.setProperty("zkACLProvider", "org.apache.solr.common.cloud.DefaultZkACLProvider");
        System.setProperty("zkCredentialsInjector", "org.apache.solr.common.cloud.DefaultZkCredentialsInjector");

        return new CloudSolrClient.Builder(
            Collections.singletonList(zkHost),
            Optional.ofNullable(zkRoot)
        ).build();
    }

}
