package landr.zk;

import landr.cmd.CommandEnvironment;
import landr.parser.CommandRegistry;
import landr.parser.JacksonCommandRegistryEntry;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Registry entry to add all commands specific to Zookeeper domain.
 */
public class ZkCommandRegistryEntry extends JacksonCommandRegistryEntry {

    private static final String HELP_FOLDER = "help";

    public ZkCommandRegistryEntry() {
        super(HELP_FOLDER);
    }

    @Override
    public void init(CommandEnvironment environment) {

        String zkHost = environment.getProperty("zkhost");
        String zkRoot = environment.getProperty("zkroot");

        try {
            ZooKeeper zookeeper = createZookeeperClient(zkHost, zkRoot);
            environment.addObject(zookeeper);
        } catch (IOException e) {
            // not much we can do
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(CommandRegistry registry) {

        registerCommand(registry, new GetNodeDescriptor());
        registerCommand(registry, new ListNodesDescriptor());
        registerCommand(registry, new CreateNodeDescriptor());
        registerCommand(registry, new DeleteNodeDescriptor());

    }

    /**
     * Create the Zookeeper client to be used by all commands.
     */
    private static ZooKeeper createZookeeperClient(String zkHost, String zkRoot) throws IOException {

        String connectString = zkHost;
        if (zkRoot != null) {
            connectString += zkRoot;
        }

        Watcher watcher = event -> {};
        return new ZooKeeper(connectString, 60000, watcher);
    }
}
