package landr.parser;

import landr.cmd.Command;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommandDescriptorTest {

    private static class MyCommandDescriptor extends CommandDescriptor<Command> {

        protected MyCommandDescriptor(CommandSyntax syntax) {
            super(syntax);
        }

        @Override
        public Command buildCommand(CommandString string, ParserContext context) {
            return null;
        }
    }

    private enum MyNumber {
        ONE,
        TWO,
        THREE
    }

    /**
     * Check we can retrieve enum instance from command line.
     */
    @Test
    public void testEnumParameter() throws CommandParseException {

        Argument arg1 = new Argument("arg1");
        Argument arg2 = new Argument("arg2");
        Argument arg3 = new Argument("arg3");
        Argument arg4 = new Argument("arg4");
        CommandSyntax syntax = new CommandSyntax("my-command", arg1, arg2, arg3, arg4);

        CommandDescriptor<?> descriptor = new MyCommandDescriptor(syntax);
        ParserContext context = new ParserContext(false);
        CommandString string = parseSingle("my-command -arg1 ONE -arg2 TWO -arg3 three");

        // Happy path to retrieve an enum
        assertEquals(MyNumber.ONE, descriptor.getArgumentEnumValue("arg1", string, context, MyNumber.class));
        assertEquals(MyNumber.TWO, descriptor.getArgumentEnumValue("arg2", string, context, MyNumber.class));

        // Enums are not case-sensitive
        assertEquals(MyNumber.THREE, descriptor.getArgumentEnumValue("arg3", string, context, MyNumber.class));

        // Null value
        assertNull(descriptor.getArgumentEnumValue("arg4", string, context, MyNumber.class));
    }

    @Test
    public void testCanonicalString() {

        Argument arg1 = new Argument("default-arg");
        Argument arg2 = new Argument("in-context", false, ContextKey.COLLECTION_NAME);
        Argument arg3 = new Argument("with-default", "def-value");
        Argument arg4 = new Argument("simple-arg");
        CommandSyntax syntax = new CommandSyntax("my-command", "default-arg", arg1, arg2, arg3, arg4);

        CommandDescriptor<?> descriptor = new MyCommandDescriptor(syntax);
        ParserContext context = new ParserContext(false);
        context.setProperty(ContextKey.COLLECTION_NAME, "context-value");
        CommandString string = parseSingle("my-command the-param -simple-arg simple-value");
        CommandString canonical = descriptor.getCanonicalString(string, context, true);

        assertEquals("the-param", canonical.getArgumentValue("default-arg"));
        assertEquals("context-value", canonical.getArgumentValue("in-context"));
        assertEquals("def-value", canonical.getArgumentValue("with-default"));
        assertEquals("simple-value", canonical.getArgumentValue("simple-arg"));
    }

    /**
     * Parse a single statement for the test.
     */
    private static CommandString parseSingle(String command) {
        List<CommandString> strings = CommandString.parse(command);
        assertEquals(1, strings.size());
        return strings.get(0);
    }

}
