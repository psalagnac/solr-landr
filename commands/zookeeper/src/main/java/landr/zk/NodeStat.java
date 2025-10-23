package landr.zk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import landr.cmd.CommandExecutionContext;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

class NodeStat extends ZkCommand {

  private final String path;

  NodeStat(String path) {
    this.path = path;
  }

  @Override
  public void execute(CommandExecutionContext context, ZooKeeper zk)
      throws KeeperException, InterruptedException {

    Stat stat = zk.exists(path, null);

    if (stat == null) {
      context.println("No such node: " + path);
      return;
    }

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ROOT);

    context.println("Version: " + stat.getVersion());
    context.println("Children: " + stat.getNumChildren());
    context.println("Created: " + format.format(new Date(stat.getCtime())));
    context.println("Modified: " + format.format(new Date(stat.getMtime())));

    long owner = stat.getEphemeralOwner();
    context.println("Type: " + (owner == 0L ? "persistent" : "ephemeral"));
    if (owner != 0L) {
      context.println("Ephemeral owner: 0x" + Long.toHexString(owner));
    }

    context.println("Data length: " + stat.getDataLength() + " bytes");
  }
}
