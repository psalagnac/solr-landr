package landr.zk;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.embedded.ZooKeeperServerEmbedded;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Simple JUnit rule to start an embedded Zookeeper server.
 */
public class ZkServerRule implements TestRule {

    private static final int SESSION_TIMEOUT = 60_000;

    private ZooKeeperServerEmbedded zkServer;

    // all clients created during the test
    private final List<ZooKeeper> clients;

    public ZkServerRule() {
        clients = new ArrayList<>();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new ZookeeperStatement(base);
    }

    public String getConnectString() throws Exception {
        return zkServer.getConnectionString();
    }

    /**
     * Return a usable Zookeeper client, not necessarily a new one. It is
     * auto closed at end of test execution.
     */
    public ZooKeeper getClient() throws Exception {
        if (clients.isEmpty()) {
            return createClient();
        } else {
            return clients.get(0);
        }
    }

    /**
     * Create a new Zookeeper client. The instance is auto closed at end of
     * test execution.
     */
    public ZooKeeper createClient() throws Exception {

        Watcher watcher = event ->{};
        ZooKeeper zookeeper = new ZooKeeper(getConnectString(), SESSION_TIMEOUT, watcher);
        clients.add(zookeeper);

        return zookeeper;
    }

    private void setup() throws Exception {

        // shutdown logs from Zookeeper
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.zookeeper", "WARN");

        // Pick a random port with a big enough number
        // There's still a chance it is already used...
        int port = 40000 + (int)(10000 * Math.random());

        Path baseDir = Files.createTempDirectory("zk-dir");

        Properties properties = new Properties();
        properties.setProperty("tickTime", "500");
        properties.setProperty("maxCnxns ", "1000");
        properties.setProperty("maxSessionTimeout ", String.valueOf(SESSION_TIMEOUT));
        properties.setProperty("admin.enableServer ", "false");
        properties.setProperty("clientPort", String.valueOf(port));

        ZooKeeperServerEmbedded.ZookKeeperServerEmbeddedBuilder builder = ZooKeeperServerEmbedded.builder();
        builder.baseDir(baseDir);
        builder.configuration(properties);

        zkServer = builder.build();
        zkServer.start();
    }

    private void tearDown() throws Exception {

        // close all known clients
        for (ZooKeeper client : clients) {
            client.close();
        }
        clients.clear();

        if (zkServer != null) {
            zkServer.close();
        }
    }

    private class ZookeeperStatement extends Statement {

        private final Statement base;

        public ZookeeperStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                setup();
                base.evaluate();
            } finally {
                tearDown();
            }
        }
    }
}
