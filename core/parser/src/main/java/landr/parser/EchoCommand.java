package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

/**
 * Basic command that prints a string in the context output.
 */
class EchoCommand extends Command {

    private final String message;

    EchoCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(CommandExecutionContext context) throws CommandExecutionException {
        context.println(message);
    }
}
