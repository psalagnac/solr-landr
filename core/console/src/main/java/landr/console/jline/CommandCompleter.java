package landr.console.jline;

import landr.cmd.CommandEnvironment;
import landr.parser.*;
import landr.parser.CommandString;
import landr.parser.syntax.Syntax;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;

/**
 * Unique implementation of the JLine completer.
 */
class CommandCompleter implements org.jline.reader.Completer {

    private final CommandEnvironment environment;
    private final CommandRegistry registry;

    CommandCompleter(CommandEnvironment environment, CommandRegistry registry) {
        this.environment = environment;
        this.registry = registry;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {

        List<Completion> completions = resolveCompletions(line);

        if (completions != null) {
            for (Completion c : completions) {
                Candidate candidate = new Candidate(c.getValue(), c.getDisplay(), null, null, null, null, c.isComplete());
                candidates.add(candidate);
            }
        }
    }

    private List<Completion> resolveCompletions(ParsedLine line) {

        CommandString string = CommandString.parse(line.line(), line.cursor());
        CommandStringLayout layout = string != null ? string.getLayout() : null;

        // first token in statement: just expand command names
        if (layout == null || line.wordIndex() == layout.getCommandNameIndex()) {
            return completeStatementNames(line, string);
        }

        List<String> words = line.words();
        if (!words.isEmpty()) {
            List<Completion> completions = new ArrayList<>();
            String name = words.get(0);

            // Default completion on the syntax
            Syntax syntax = registry.getSyntax(name);
            if (syntax != null) {
                List<Completion> syntaxCompletions = completeSyntax(syntax, line);
                if (syntaxCompletions != null) {
                    completions.addAll(syntaxCompletions);
                }
            }

            // Custom completion specific to a command
            CommandDescriptor<?> command = registry.getCommand(name);
            if (command != null) {
                List<Completion> customCompletions = completeCustomCommand(command, line);
                if (customCompletions != null) {
                    completions.addAll(customCompletions);
                }
            }

            return completions;
        }

        return null;
    }

    private List<Completion> completeStatementNames(ParsedLine line, CommandString string) {
        List<Completion> completions = new ArrayList<>();

        String word;
        boolean pragma;
        boolean empty;

        // If the statement was successfully parsed, retrieve the parsed name. This removes separators.
        if (string != null) {

            // Special case if the cursor is before the typed input. We consider we have an empty statement
            if (line.cursor() < string.getLayout().getCommandNameStartPosition()) {
                word = "";
                empty = true;
                pragma = false;
            } else {
                word = string.getCommandName();
                pragma = string.isPragma();
                empty = false;
            }
        } else {
            word = "";
            empty = true;
            pragma = false;
        }

        // if we start with '@' character, complete with pragma names
        if (empty || pragma) {
            completions.addAll(completePragmaNames(word));
        }

        if (!pragma) {
            completions.addAll(completeCommandNames(word));
        }

        return completions;
    }

    /**
     * Completion for the first token of a command line.
     * We only resolve command names, ignoring other parameters.
     */
    private List<Completion> completeCommandNames(String word) {
        List<Completion> completions = new ArrayList<>();

        Collection<String> names = registry.getCommandNames();
        for (String name : names) {
            if (name.startsWith(word)) {
                completions.add(new Completion(name));
            }
        }

        return completions;
    }

    /**
     * Complete for the first token of a pragma.
     */
    private List<Completion> completePragmaNames(String word) {
        List<Completion> completions = new ArrayList<>();

        Collection<String> names = registry.getPragmaNames();
        for (String name : names) {
            if (name.startsWith(word)) {
                completions.add(new Completion("@" + name));
            }
        }

        return completions;
    }

    /**
     * Suggest argument names based on the syntax.
     */
    private List<Completion> completeSyntax(Syntax syntax, ParsedLine line) {

        String word = line.word();

        // Complete argument name
        if (word.startsWith("-")) {
            List<Completion> completions = new ArrayList<>();

            for (String name : syntax.getArgumentNames()) {
                completions.add(new Completion("-" + name));
            }

            return completions;
        }

        return null;
    }

    /**
     * Invoke custom completer if any. This is invoked only if we were able to resolve the command name.
     */
    private List<Completion> completeCustomCommand(CommandDescriptor<?> command, ParsedLine line) {

        Completer completer = command.getCompleter();
        if (completer == null) {
            return null;
        }

        // we need a valid argument name to try completion
        List<String> words = line.words();
        if (words.size() < 2) {
            return null;
        }

        // Resolve argument name in the syntax
        String argument = words.get(line.wordIndex() - 1);
        if (argument.equals(command.getName())) {
            argument = command.getSyntax().getDefaultArgument();
        } else if (argument.startsWith("-")) {
            argument = argument.substring(1);
            if (!command.getSyntax().hasArgument(argument)) {
                argument = null;
            }
        }

        // We fail to get the current parameter name
        if (argument == null || argument.isEmpty()) {
            return null;
        }

        // The custom may require values of other argument (like if we want to resolve a shard/replica, we also
        // need the collection name).
        // Blindly retrieve all values with best effort
        CommandString string = CommandString.parse(line.line(), line.cursor());
        Map<String, String> others = new HashMap<>();
        for (String argumentName : command.getSyntax().getArgumentNames()) {

            // Skip argument we're completing
            if (argumentName.equals(argument)) {
                continue;
            }

            String argumentValue = string.getArgumentValue(argumentName);
            if (argumentValue != null) {
                others.put(argumentName, argumentValue);
            }
        }

        return completer.completeArgument(environment, argument, line.word(), others);
    }

}
