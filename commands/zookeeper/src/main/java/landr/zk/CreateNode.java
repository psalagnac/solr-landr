package landr.zk;

import landr.cmd.CommandExecutionContext;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.util.Collections;
import java.util.List;

class CreateNode extends ZkCommand {

    private final String path;

    CreateNode(String path) {
        this.path = path;
    }

    @Override
    public void execute(CommandExecutionContext context, ZooKeeper zk) throws KeeperException, InterruptedException {

        byte[] data = new byte[0];
        List<ACL> acl = Collections.singletonList(new ACL(0, ZooDefs.Ids.ANYONE_ID_UNSAFE));

        zk.create(path, data, acl, CreateMode.PERSISTENT);
    }

}
