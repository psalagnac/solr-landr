package landr.parser;

import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenericHelpTest {

    /**
     * Test generating usages from the syntax.
     */
    @Test
    public void testSyntaxUsages() {

        Argument required = new Argument("required", true);
        Argument optional = new Argument("optional", false);

        // Simple command with no argument
        CommandSyntax simple = new CommandSyntax("simple");
        assertSyntaxUsages(simple, "simple");

        // Command with a unique required default argument
        CommandSyntax shortS = new CommandSyntax("short", "required", required);
        assertSyntaxUsages(shortS, "short required", "short -required required");

        CommandSyntax syntax = new CommandSyntax("cmd", required, optional);

        assertSyntaxUsages(syntax,
                "cmd -required required",
                "cmd -required required [-optional optional]");
    }

    private static void assertSyntaxUsages(CommandSyntax syntax, String... expected) {

        List<String> usages = GenericHelp.generateSyntaxUsages(syntax);

        assertEquals(Arrays.asList(expected), usages);
    }

}
