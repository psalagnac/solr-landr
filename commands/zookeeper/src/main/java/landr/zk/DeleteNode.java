package landr.zk;

import landr.cmd.CommandExecutionContext;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

class DeleteNode extends ZkCommand {

    private final String path;

    DeleteNode(String path) {
        this.path = path;
    }

    @Override
    public void execute(CommandExecutionContext context, ZooKeeper zk) throws KeeperException, InterruptedException {

        zk.delete(path, -1);

    }

}
