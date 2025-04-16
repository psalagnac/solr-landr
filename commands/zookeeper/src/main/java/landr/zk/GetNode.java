package landr.zk;

import landr.cmd.CommandExecutionContext;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

class GetNode extends ZkCommand {

    private final String path;

    GetNode(String path) {
        this.path = path;
    }

    @Override
    public void execute(CommandExecutionContext context, ZooKeeper zk) throws KeeperException, InterruptedException {

        byte[] data = zk.getData(path, null, null);

        if (data != null) {
            context.println(new String(data));
        }
    }

}
