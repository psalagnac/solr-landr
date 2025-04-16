package landr.cmd;

/**
 * Abstract class for all commands.
 */
public abstract class Command {

    protected Command() {
    }

    public abstract void execute(CommandExecutionContext context) throws CommandExecutionException;
}
