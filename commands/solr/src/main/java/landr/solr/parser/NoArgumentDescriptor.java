package landr.solr.parser;

import landr.solr.cmd.SolrCommand;
import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.CommandSyntax;

import java.lang.reflect.Constructor;

/**
 * Syntax for a command with no argument.
 * The command instance is created with reflection.
 */
public class NoArgumentDescriptor<T extends SolrCommand> extends SolrCommandDescriptor<T> {

    private final Class<T> clazz;

    public NoArgumentDescriptor(String name, Class<T> clazz) {
        super(new CommandSyntax(name));

        this.clazz = clazz;
    }

    @Override
    public T buildCommand(CommandString string, ParserContext context) throws CommandParseException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String message = String.format("Class %s has no default constructor", clazz.getSimpleName());
            throw new CommandParseException(context, message);
        } catch (ReflectiveOperationException e) {
            throw new CommandParseException(context, "Failed to instantiate command", e);
        }
    }
}
