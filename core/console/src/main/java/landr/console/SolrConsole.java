package landr.console;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import landr.parser.CommandParseException;
import landr.parser.CommandParser;
import landr.parser.CommandRegistry;
import landr.parser.ParserContext;

import java.io.IOException;
import java.util.List;

/**
 * Main class to run the interactive console.
 */
public class SolrConsole {

    private final TerminalFactory factory;

    public SolrConsole(TerminalFactory factory) {
        this.factory = factory;
    }

    public void work(CommandEnvironment environment, CommandRegistry registry) {

        CommandParser parser = new CommandParser(registry);

        ConsoleTerminal terminal;
        try {
            terminal = factory.createInteractiveTerminal(environment, registry);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ParserContext parserContext = new ParserContext(true);
        CommandExecutionContext context = new TerminalExecutionContext(environment, terminal);

        String line;
        while ((line = terminal.readLine()) != null) {

            List<Command> commands;
            try {
                commands = parser.parse(parserContext, line);
            } catch (CommandParseException e) {
                handleParseException(context, e);
                continue;
            }

            // No command returned by the parser.
            // Can be a parser pragma, or an empty line
            if (commands == null) {
                continue;
            }

            try {
                for (Command command : commands) {
                    command.execute(context);
                }
            } catch (CommandExecutionException e) {
                // in case we have multiple commands, do not execute the remaining ones
                handleCommandException(context, e);
            }
        }
    }

    /**
     * Handler for error during command parsing.
     */
    private void handleParseException(CommandExecutionContext context, CommandParseException e) {

        if (context.getDebug()) {
            context.printStackTrace(e);
        } else {
            String message = e.getClass().getSimpleName() + ": " + e.getMessage();
            context.printlnError(message);
        }
    }

    /**
     * Handler for error because of command exception. Can be after an error on Solr side.
     */
    private void handleCommandException(CommandExecutionContext context, CommandExecutionException e) {

        if (context.getDebug()) {
            context.printStackTrace(e);
        } else {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            }

            String message = cause.getClass().getSimpleName() + ": " + cause.getMessage();
            context.printlnError(message);
        }
    }

}
