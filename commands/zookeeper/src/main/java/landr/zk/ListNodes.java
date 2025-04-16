package landr.zk;

import landr.cmd.CommandExecutionContext;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Command to list nodes in Zookeeper.
 * Goal is to have something similar to command 'ls' in zkCli.
 */
class ListNodes extends ZkCommand {

    private final String path;

    ListNodes(String path) {
        this.path = path;
    }

    @Override
    public void execute(CommandExecutionContext context, ZooKeeper zk) throws KeeperException, InterruptedException {

        List<String> nodes = zk.getChildren(path, false);

        for (String node : nodes) {
            context.println(node);
        }

    }

}
