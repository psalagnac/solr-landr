package landr.solr.parser;

import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionException;
import landr.parser.Completer;
import landr.parser.Completion;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.cloud.ClusterState;
import org.apache.solr.common.cloud.DocCollection;
import org.apache.solr.common.cloud.Replica;
import org.apache.solr.common.cloud.Slice;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom completer to resolve names from the Solr {@link ClusterState}, like collection names, shard names,
 * replica names...
 */
public class ClusterStateCompleter implements Completer {

    public enum CompletionType {

        /**
         * Completion based on all collection names in the cluster.
         */
        COLLECTION,

        /**
         * Completion based on names of shards from the selected collection. Completion is enabled only if a
         * valid collection name is already specified.
         */
        SHARD,

        /**
         * Completion based on the name of the replicas from the selected collection.
         */
        REPLICA,

        /**
         * Completion based on names of all live nodes in the cluster
         */
        NODE

    }

    private final Map<String, CompletionType> types;

    public ClusterStateCompleter(Map<String, CompletionType> types) {
        this.types = types;
    }

    @Override
    public List<Completion> completeArgument(CommandEnvironment env, String argument, String value, Map<String, String> others) {

        ClusterState clusterState = getClusterState(env);
        if (clusterState == null) {
            // skip if we can't fetch cluster state
            return null;
        }

        CompletionType type = types.get(argument);
        if (type == null) {
            // this is not something the command descriptor wants us to complete
            return null;
        }

        switch (type) {

            case COLLECTION:
                return completeCollection(clusterState, value);

            case SHARD:
                return completeShard(clusterState, value, others);

            case REPLICA:
                return completeReplica(clusterState, value, others);

            case NODE:
                return completeNode(clusterState, value);

            default:
                return null;
        }

    }

    /**
     * Complete an argument value based on collection names.
     */
    private List<Completion> completeCollection(ClusterState clusterState, String value) {
        Collection<String> allCollections = clusterState.getCollectionNames();
        return filterFromPrefix(allCollections, value);
    }

    /**
     * Complete an argument value based on shard names. We must resolve the collection name
     * first to get list of shards.
     */
    private List<Completion> completeShard(ClusterState clusterState, String value, Map<String, String> others) {

        String collectionName = probeCollectionName(others);
        DocCollection collection = clusterState.getCollectionOrNull(collectionName);
        if (collection == null) {
            return null;
        }

        Set<String> allNames = collection.getSlicesMap().keySet();
        return filterFromPrefix(allNames, value);
    }

    /**
     * Complete an argument value based on replica names. We must resolve the collection name
     * first to get list of replicas.
     */
    private List<Completion> completeReplica(ClusterState clusterState, String value, Map<String, String> others) {

        String collectionName = probeCollectionName(others);
        DocCollection collection = clusterState.getCollectionOrNull(collectionName);
        if (collection == null) {
            return null;
        }

        // Maybe the command also has a shard parameter. If we find one, we should only complete
        // with the replicas of this specific shard
        String shardName = probeShardName(others);

        // If we successfully found a shard name, filter on the replicas of this shard
        Collection<Replica> replicas;
        Slice slice = collection.getSlice(shardName);
        if (slice != null) {
            replicas = slice.getReplicas();
        } else {
            replicas = collection.getReplicas();
        }

        Set<String> allNames = replicas.stream().map(Replica::getName).collect(Collectors.toSet());
        return filterFromPrefix(allNames, value);
    }

    /**
     * Complete an argument value based on Solr node names.
     */
    private List<Completion> completeNode(ClusterState clusterState, String value) {
        Set<String> nodeNames = clusterState.getLiveNodes();
        return filterFromPrefix(nodeNames, value);
    }

    /**
     * Probe the collection name to resolve shard/replica names.
     * We assume completion is enabled on collection names for another parameter than the currently
     * resolved one. There is no guarantee that the returned collection exists.
     */
    private String probeCollectionName(Map<String, String> others) {

        // Do a reverse lookup on all enabled completions.
        // If we find completion on collection, we assume this is the correct argument
        String argument = null;
        for (Map.Entry<String, CompletionType> entry : types.entrySet()) {

            if (CompletionType.COLLECTION.equals(entry.getValue())) {
                argument = entry.getKey();
                break;
            }
        }

        return others.get(argument);
    }

    /**
     * Probe the shard name to resolve replica names.
     * THere is no guarantee the returned shard name is valid.
     */
    private String probeShardName(Map<String, String> others) {
        // Do a reverse lookup on all enabled completions.
        // If we find completion on collection, we assume this is the correct argument
        String argument = null;
        for (Map.Entry<String, CompletionType> entry : types.entrySet()) {

            if (CompletionType.SHARD.equals(entry.getValue())) {
                argument = entry.getKey();
                break;
            }
        }

        return others.get(argument);
    }

    private List<Completion> filterFromPrefix(Collection<String> allValues, String prefix) {

        // when no prefix is typed yet, return all values
        if (prefix == null || prefix.isEmpty()) {
            return allValues.stream().map(Completion::new).collect(Collectors.toList());
        }

        return allValues.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(prefix))
                .map(Completion::new)
                .collect(Collectors.toList());

    }

    private ClusterState getClusterState(CommandEnvironment env) {
        try {
            SolrClient client = env.getObject(SolrClient.class);
            if (client instanceof CloudSolrClient) {
                CloudSolrClient cloudClient = (CloudSolrClient) client;
                return cloudClient.getClusterState();
            } else {
                return null;
            }
        } catch (CommandExecutionException e) {
            // silently abort if we can't get cluster state
            return null;
        }
    }
}
