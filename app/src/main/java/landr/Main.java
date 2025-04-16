package landr;

import landr.cmd.*;
import landr.console.ConsoleTerminal;
import landr.console.SolrConsole;
import landr.console.TerminalExecutionContext;
import landr.console.TerminalFactory;
import landr.parser.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Landr entry point for standalone application.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        MainArguments arguments = MainArguments.parse(args);
        if (arguments.help) {
            System.out.println(MainArguments.usageHelp());
            return;
        }

        if (arguments.banner) {
            printBanner();
        }

        CommandEnvironment environment = createEnvironment(arguments);
        try {
            if (arguments.input != null) {
                CommandRegistry registry = createCommandRegistry(environment, false);
                runFromInput(environment, registry, arguments);
            } else {
                CommandRegistry registry = createCommandRegistry(environment, true);
                runFromConsole(environment, registry, arguments);
            }
        } finally {
            environment.close();
        }

    }

    /**
     * Build a new execution environment from command line parameters.
     */
    private static CommandEnvironment createEnvironment(MainArguments arguments) {

        Random random;
        if (arguments.seed == null) {
            random = new Random();
        } else {
            long seed = Long.parseLong(arguments.seed);
            random = new Random(seed);
        }

        // Create map of global properties
        Map<String, String> properties = new HashMap<>();
        properties.put("zkhost", arguments.zkHost);
        properties.put("zkroot", arguments.zkRoot);

        return new CommandEnvironment(properties, random);
    }


    /**
     * Run in non-interactive mode reading a file.
     */
    private static void runFromInput(CommandEnvironment environment, CommandRegistry registry, MainArguments arguments) throws Exception {

        CommandExecutionContext context;

        // Unless we force JLine, use system execution context when the JVM has no console
        if (System.console() == null && !arguments.jline || arguments.system) {
            context = new DefaultExecutionContext(environment);
        } else {
            TerminalFactory factory = createTerminalFactory(arguments, false);
            ConsoleTerminal terminal = factory.createOutputTerminal();
            context = new TerminalExecutionContext(environment, terminal);
        }

        try (Reader reader = openInput(arguments)) {

            CommandParser parser = new CommandParser(registry);

            List<Command> commands = parser.parse(reader);
            for (Command command : commands) {
                command.execute(context);
            }
        }
    }

    /**
     * Open a reader to the script input. If file name is '-', we read from standard input.
     */
    private static Reader openInput(MainArguments arguments) throws IOException {

        if (arguments.input.equals("-")) {
            return new InputStreamReader(System.in, Charset.defaultCharset());
        } else {
            return new FileReader(arguments.input);
        }
    }

    /**
     * Run in interactive mode from the terminal.
     */
    private static void runFromConsole(CommandEnvironment environment, CommandRegistry registry, MainArguments arguments) {

        TerminalFactory factory = createTerminalFactory(arguments, true);
        SolrConsole console = new SolrConsole(factory);

        console.work(environment, registry);
    }

    /**
     * Create the terminal factory to use. Can be either in interactive or non-interactive mode.
     *
     * @param console Whether we run in interactive mode.
     */
    private static TerminalFactory createTerminalFactory(MainArguments arguments, boolean console)  {

        TerminalFactory factory = new TerminalFactory(console);
        if (arguments.jline) {
            factory.enableJLine();
        }
        if (arguments.system) {
            factory.disableJLine();
        }
        if (arguments.prompt != null) {
            factory.setPrompt(arguments.prompt);
        }

        return factory;
    }

    /**
     * Load all discoverable commands and add them in the registry.
     */
    private static CommandRegistry createCommandRegistry(CommandEnvironment environment, boolean interactive) {

        CommandRegistryFactory factory = new CommandRegistryFactory(interactive);
        factory.addEchoCommand();

        // add base Solr commands
        String solrEntry = "landr.solr.SolrCommandRegistryEntry";
        addOptionalEntry(factory, environment, solrEntry);

        // add base ZK commands
        String zkEntry = "landr.zk.ZkCommandRegistryEntry";
        addOptionalEntry(factory, environment, zkEntry);

        return factory.create(true);
    }

    /**
     * Add a new entry to the registry. In case we cannot find it, it is silently ignored.
     *
     * @param name Qualified class name of the entry.
     */
    private static void addOptionalEntry(CommandRegistryFactory factory, CommandEnvironment environment, String name) {

        try {
            Class<?> clazz = Class.forName(name, true, Main.class.getClassLoader());

            if (!CommandRegistryEntry.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("bad class: " + name);
            }

            Class<? extends CommandRegistryEntry> subclass = clazz.asSubclass(CommandRegistryEntry.class);

            CommandRegistryEntry entry = subclass.getDeclaredConstructor().newInstance();
            factory.addEntry(entry);
            entry.init(environment);
        } catch (ClassNotFoundException e) {
            // entry is not found,  we may just ignore
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Can't find constructor for: " + name);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate: " + name, e);
        }
    }

    /**
     * Print banner to standard output.
     */
    private static void printBanner() {
        String banner = " ___      _          ___  _  _\n" +
        "/ __| ___| |_ _     / __|| |(_)\n" +
        "\\__ \\/ _ \\ | '_|   | |__ | ||_|\n" +
        "|___/\\___/_|_|      \\___||_||_|";

        System.out.println();
        System.out.println(banner);
        System.out.println();
    }
}
