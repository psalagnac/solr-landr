package landr;

/**
 * Utility class to parse options from the main command line.
 */
class MainArguments {

    final String zkHost;
    final String zkRoot;
    final String seed;
    final String input;

    // customizations
    final boolean banner;
    final String prompt;

    // output flags
    final boolean system;
    final boolean jline;

    // print help and exit
    final boolean help;

    MainArguments(Builder builder) {
        this.zkHost = builder.zkHost;
        this.zkRoot = builder.zkRoot;
        this.seed = builder.seed;
        this.input  = builder.input;
        this.banner = builder.banner;
        this.prompt = builder.prompt;
        this.system = builder.system;
        this.jline = builder.jline;
        this.help = builder.help;
    }

    /**
     * Creates a new instance from system command.
     */
    static MainArguments parse(String[] args) {

        Builder builder = new Builder();

        // naive parsing for now
        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-zkhost":
                    builder.zkHost = args[++i];
                    break;
                case "-zkroot":
                    builder.zkRoot = args[++i];
                    break;
                case "-seed":
                    builder.seed = args[++i];
                    break;
                case "-banner":
                    builder.banner = true;
                    break;
                case "-prompt":
                    builder.prompt = args[++i];
                    break;
                case "-system":
                    builder.system = true;
                    break;
                case "-jline":
                    builder.jline = true;
                    break;
                case "-help":
                    builder.help = true;
                    break;
                default:
                    builder.input = args[i];
                    break;
            }
        }

        return new MainArguments(builder);
    }

    private static class Builder {

        private static final String DEFAULT_ZK_HOST = "localhost:2181";

        private String zkHost = DEFAULT_ZK_HOST;
        private String zkRoot;
        private String seed;
        private String input;

        private boolean banner;
        private String prompt;

        private boolean system;
        private boolean jline;

        private boolean help;
    }

    static String usageHelp() {

        return "Solr Landr " + MainArguments.class.getPackage().getImplementationVersion() +
                "\n\n" +
                "USAGE: solr-cli [option] [file]\n" +
                "\n" +
                "OPTIONS:\n" +
                "  -zkhost host    Zookeeper hosts to connect to. Default is " + Builder.DEFAULT_ZK_HOST + ".\n" +
                "  -zkroot path    Root path in Zookeeper.\n" +
                "  -seed s         Seed for randomized operations.\n" +
                "\n" +
                "  -system         Always use system console.\n" +
                "  -jline          Always enable JLine.\n" +
                "\n" +
                "  -help           Print this help and exit.";

    }
}
