package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

/**
 * This command does nothing (but it still exists).
 */
class NullCommand extends Command {

    @Override
    public void execute(CommandExecutionContext context) throws CommandExecutionException {
        // does nothing
    }
}
