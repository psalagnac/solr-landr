package landr.zk;

import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionException;
import landr.parser.Completer;
import landr.parser.Completion;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Argument completer for Zookeeper node path.
 */
public class PathCompleter implements Completer {

    @Override
    public List<Completion> completeArgument(CommandEnvironment env, String argument, String value, Map<String, String> others) {

        // This completer must be used only for paths in ZK
        if (!ZkCommandDescriptor.PATH_PARAM.equals(argument)) {
            return null;
        }

        // return no candidate when no prefix is typed
        if (value == null || !value.startsWith("/")) {
            return null;
        }

        try {
            ZooKeeper client = env.getObject(ZooKeeper.class);

            // if the path ends with '/', returns children
            if  (value.endsWith("/")) {
                return completeChildren(client, value);
            } else {
                return completePath(client, value);
            }

        } catch (CommandExecutionException | InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get suggestions by listing children of the current path.
     */
    private List<Completion> completeChildren(ZooKeeper client, String value) throws KeeperException, InterruptedException {

        // remove trailing "/"
        String path;
        if (value.equals("/")) {
            path = value;
        } else {
            path = value.substring(0, value.length() - 1);
        }

        // Skip if the path does not exist
        if (client.exists(path, false) == null) {
            return null;
        }

        List<String> children = client.getChildren(path, false);
        return children.stream()
                .map(c -> new Completion(value + c, c, false))
                .collect(Collectors.toList());
    }

    /**
     * Get suggestions by completing current path.
     */
    private List<Completion> completePath(ZooKeeper client, String value) throws KeeperException, InterruptedException {

        int separator = value.lastIndexOf('/');
        String parent = value.substring(0, separator);
        String prefix = value.substring(separator + 1);

        // special case if the path is already a node
        if (client.exists(value, false) != null) {
            return Collections.singletonList(new Completion(value, prefix, false));
        }

        // If parent node is root, make sure we don't have n empty path
        if (parent.isEmpty()) {
            parent = "/";
        }

        if (client.exists(parent, false) != null) {
            List<String> children = client.getChildren(parent, false);
            return children.stream()
                    .filter(c -> c.startsWith(prefix))
                    .map(c -> new Completion(value.substring(0, separator + 1) + c, c, false))
                    .collect(Collectors.toList());
        }

        return null;
    }

}
