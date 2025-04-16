package landr.zk;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * Abstract implementation of commands that only interacts with Zookeeper client.
 */
public abstract class ZkCommand extends Command {

    @Override
    public void execute(CommandExecutionContext context) throws CommandExecutionException {

        CommandEnvironment environment = context.getEnvironment();
        ZooKeeper client = environment.getObject(ZooKeeper.class);

        try {
            execute(context, client);
        } catch (KeeperException | IllegalArgumentException e) {
            handleZookeeperError(context, e);
        } catch (InterruptedException e) {
            throw new CommandExecutionException(e);
        }
    }

    private void handleZookeeperError(CommandExecutionContext context, Throwable e) {

        if (context.getDebug()) {
            context.printStackTrace(e);
        } else {
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                context.println(e.getClass().getSimpleName());
            } else {
                context.printlnError(e.getMessage());
            }
        }
    }

    /**
     * Implementation for Zookeeper commands.
     */
    public abstract void execute(CommandExecutionContext context, ZooKeeper zk) throws KeeperException, InterruptedException;

}
