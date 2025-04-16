package landr.zk;

import landr.cmd.CommandEnvironment;
import landr.parser.Completion;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.junit.Rule;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.zookeeper.ZooDefs.Ids.ANYONE_ID_UNSAFE;
import static org.apache.zookeeper.ZooDefs.Perms.ALL;
import static org.junit.Assert.assertEquals;

public class PathCompleterTest {

    private static final List<ACL> ACL = Collections.singletonList(new ACL(ALL, ANYONE_ID_UNSAFE));

    @Rule
    public ZkServerRule zk = new ZkServerRule();

    /**
     * Check we gracefully return no candidate with an empty prefix.
     */
    @Test
    public void testEmpty() throws Exception {

        ZooKeeper zookeeper = zk.getClient();
        assertCompletion(zookeeper, "");
    }

    /**
     * Test we flip the debug flag in parser and execution contexts.
     */
    @Test
    public void testListRoot() throws Exception {

        ZooKeeper zookeeper = zk.getClient();
        zookeeper.create("/one", null, ACL, CreateMode.PERSISTENT);
        zookeeper.create("/two", null, ACL, CreateMode.PERSISTENT);

        assertCompletion(zookeeper, "/", "/zookeeper", "/one", "/two");
    }

    @Test
    public void testSimplePrefix() throws Exception {

        ZooKeeper zookeeper = zk.getClient();
        zookeeper.create("/one", null, ACL, CreateMode.PERSISTENT);
        zookeeper.create("/two", null, ACL, CreateMode.PERSISTENT);
        zookeeper.create("/three", null, ACL, CreateMode.PERSISTENT);

        assertCompletion(zookeeper, "/o", "/one");
        assertCompletion(zookeeper, "/on", "/one");
        assertCompletion(zookeeper, "/one", "/one");
        assertCompletion(zookeeper, "/t", "/two", "/three");
    }

    /**
     * Test listing a sub folder.
     */
    @Test
    public void testChildrenNodes() throws Exception {

        ZooKeeper zookeeper = zk.getClient();
        zookeeper.create("/parent", null, ACL, CreateMode.PERSISTENT);
        zookeeper.create("/parent/sub1", null, ACL, CreateMode.PERSISTENT);
        zookeeper.create("/parent/sub2", null, ACL, CreateMode.PERSISTENT);

        assertCompletion(zookeeper, "/parent", "/parent");
        assertCompletion(zookeeper, "/parent/", "/parent/sub1", "/parent/sub2");
        assertCompletion(zookeeper, "/parent/su", "/parent/sub1", "/parent/sub2");
        assertCompletion(zookeeper, "/parent/sub1", "/parent/sub1");
    }

    private void assertCompletion(ZooKeeper zookeeper, String value, String... expected) {

        CommandEnvironment env = new CommandEnvironment();
        env.addObject(zookeeper);

        PathCompleter completer = new PathCompleter();
        List<Completion> completions = completer.completeArgument(env, "path", value, Collections.emptyMap());

        Set<String> actualSet;
        if (completions == null) {
            actualSet = Collections.emptySet();
        } else {
            actualSet = completions.stream().map(Completion::getValue).collect(Collectors.toSet());
        }

        Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));
        assertEquals(expectedSet, actualSet);
    }

}
