package landr.parser;

import landr.parser.syntax.Help;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JacksonHelpParserTest {

    /**
     * Parse a help description with only a summary.
     */
    @Test
    public void testParseSummary() throws IOException {
        Help help = parse("summary.json");
        assertEquals("the-summary", help.getSummary());
    }

    /**
     * Parse a help with all fields populated.
     */
    @Test
    public void testFull() throws IOException {
        Help help = parse("full.json");
        assertEquals("the-section", help.getSection());
        assertEquals("the-summary", help.getSummary());

        // check usages are in order
        List<String> usages = new ArrayList<>();
        usages.add("the-usage1");
        usages.add("the-usage2");
        assertEquals(usages, help.getUsages());
    }

    private Help parse(String filename) throws IOException {

        ClassLoader loader = getClass().getClassLoader();
        String path = getClass().getSimpleName() + '/' + filename;

        try (InputStream input = loader.getResourceAsStream(path)) {

            if (input == null) {
                fail("Can't find input");
            }

            Reader reader = new InputStreamReader(input, Charset.defaultCharset());
            JacksonHelpParser parser = new JacksonHelpParser();
            return parser.readHelp(reader);
        }
    }
}
