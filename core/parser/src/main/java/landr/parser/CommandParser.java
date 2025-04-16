package landr.parser;

import landr.cmd.Command;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Parser for a single command with basic syntax.
 */
public class CommandParser {

    private final CommandRegistry registry;

    public CommandParser(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Parse a list of commands (a script).
     * Eventual pragma are executed during parsing.
     */
    public List<Command> parse(Reader input) throws IOException, CommandParseException {

        List<Command> commands = new ArrayList<>();
        ParserContext context = new ParserContext(false);

        try (LineNumberReader reader = new LineNumberReader(input)) {

            String line;
            while ((line = reader.readLine()) != null) {

                List<Command> parsed = parse(context, line);
                if (parsed != null) {
                    commands.addAll(parsed);
                }
            }
        }

        return commands;
    }

    /**
     * Parses any statement. Can be a command or a pragma.
     * Pragmas are immediately executed, while commands are returned.
     *
     * <p>Note this method can return more than one command, if a single statement produces multiple commands.
     *
     * @return the parsed commands, or {@code null} if no command is to be executed.
     */
    public List<Command> parse(ParserContext context, String line) throws CommandParseException {

        // ignore empty lines and comments
        if (line.isBlank() || line.startsWith("#")) {
            return null;
        }

        List<Command> commands = new ArrayList<>();

        List<CommandString> strings = CommandString.parse(line);

        for (CommandString string : strings) {
            if (string.isPragma()) {
                PragmaDescriptor<?> descriptor = resolvePragma(context, string.getCommandName());
                Pragma pragma = descriptor.buildPragma(context, string);
                pragma.execute(context);

                // If the pragma also has an impact at execution time, introduce a command in the execution chain
                Command command = pragma.getCommand();
                if (command != null) {
                    commands.add(command);
                }
            } else {
                commands.addAll(parseCommand(context, string));
            }
        }

        if (commands.isEmpty()) {
            return null;
        } else {
            return commands;
        }
    }

    private List<Command> parseCommand(ParserContext context, CommandString string) throws CommandParseException {

        // first token is always command name
        CommandDescriptor<?> syntax = resolveCommand(context, string.getCommandName());

        Command command = syntax.buildCommand(string, context);

        // If tracing is enabled, print all statements during command execution
        if (context.getTrace()) {
            CommandString canonical = syntax.getCanonicalString(string, context, false);
            EchoCommand echo = new EchoCommand(canonical.toString());
            return List.of(echo, command);
        } else {
            return Collections.singletonList(command);
        }
    }

    private CommandDescriptor<?> resolveCommand(ParserContext context, String name) throws CommandParseException {
        CommandDescriptor<?> descriptor = registry.getCommand(name);

        if (descriptor == null) {
            throw new UnknownCommandException(context, name);
        }

        return descriptor;
    }

    private PragmaDescriptor<?> resolvePragma(ParserContext context, String name) throws CommandParseException {
        PragmaDescriptor<?> descriptor = registry.getPragma(name);

        if (descriptor == null) {
            throw new UnknownPragmaException(context, name);
        }

        return descriptor;
    }

}
