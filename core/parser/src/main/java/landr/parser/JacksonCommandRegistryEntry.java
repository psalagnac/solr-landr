package landr.parser;

import landr.parser.syntax.Help;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public abstract class JacksonCommandRegistryEntry implements CommandRegistryEntry {

    private final String helpFolder;

    protected JacksonCommandRegistryEntry(String helpFolder) {
        this.helpFolder = helpFolder;
    }

    /**
     * Register an already created descriptor to the registry. We try to load a help from a Json
     * with same name as the command descriptor.
     */
    protected void registerCommand(CommandRegistry registry, CommandDescriptor<?> descriptor) {

        String name = descriptor.getName();
        Help help = parseHelp(name);

        if (help == null) {
            registry.registerCommand(descriptor);
        } else {
            registry.registerCommand(descriptor, help);
        }
    }


    /**
     * Best effort to parse help from a JSON file.
     * Return {@code null} if no file is found.
     */
    protected Help parseHelp(String command) {

        ClassLoader loader = getClass().getClassLoader();

        String path = helpFolder + "/" + command + ".json";
        try (InputStream input = loader.getResourceAsStream(path)) {

            if (input == null) {
                return null;
            }

            JacksonHelpParser parser = new JacksonHelpParser();
            Reader reader = new InputStreamReader(input, Charset.defaultCharset());
            return parser.readHelp(reader);
        } catch (IOException e) {
            // This should not happen. Just fail
            String message = String.format("failed to parse help for command %s", command);
            throw new RuntimeException(message, e);
        }
    }

}
