package landr.parser;

import landr.parser.syntax.PragmaSyntax;
import landr.parser.tokenizer.CommandTokenizer;
import landr.parser.tokenizer.Token;

import java.util.*;

/**
 * Parser for {@link CommandString}.
 */
class StringParser {

    /**
     * Parse all the available statements, in order.
     */
    List<CommandString> parse(String string) {
        CommandTokenizer tokenizer = new CommandTokenizer(string);
        return parse(tokenizer);
    }

    /**
     * Parse the command line at the cursor position. If the input contains multiple statements, other ones are
     * ignored.
     */
    CommandString parse(String string, int cursor) {
        CommandTokenizer tokenizer = new CommandTokenizer(string, cursor);
        List<CommandString> commands = parse(tokenizer);

        // we're supposed to get a single command !
        if (commands.isEmpty()) {
            return null;
        }
        if (commands.size() == 1) {
            return commands.get(0);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Parse from an already tokenized input.
     */
    private List<CommandString> parse(Iterator<Token> tokens) {

        List<CommandString> commands = new ArrayList<>();
        int index = 0;

        while (tokens.hasNext()) {
            CommandStringLayout.Builder layout = new CommandStringLayout.Builder();

            // first token is always command name
            Token name = tokens.next();
            layout.startStatement(name, index++);

            // If the command name is ',', wo got an empty statement
            if (name.isStatementSeparator()) {
                continue;
            }

            Map<String, String> arguments = new HashMap<>();
            List<String> parameters = new ArrayList<>();

            // naive parsing for now
            while (tokens.hasNext()) {
                Token token = tokens.next();

                // we're done with the statement
                if (token.isStatementSeparator()) {
                    break;
                }

                if (token.charAt(0) == '-') {

                    // Skip if argument name is the last token
                    if (!tokens.hasNext()) {
                        continue;
                    }

                    Token argName = token.subToken(1);
                    Token argValue = tokens.next();
                    arguments.put(argName.getValue(), argValue.getValue());
                } else {
                    parameters.add(token.getValue());
                }
            }

            String commandName;

            // if the line is pragma, remove the first character from the command line
            boolean pragma = false;
            if (name.charAt(0) == PragmaSyntax.PRAGMA_PREFIX) {
                pragma = true;
                if (name.getLength() == 1) {
                    commandName = "";
                } else {
                    name = name.subToken(1);
                    commandName = name.getValue();
                }
            } else {
                commandName = name.getValue();
            }

            commands.add(new CommandString(commandName, pragma, arguments, parameters, layout.build()));
        }

        return commands;
    }

}
