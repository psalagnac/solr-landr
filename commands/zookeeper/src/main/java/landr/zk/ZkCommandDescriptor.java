package landr.zk;

import landr.parser.CommandDescriptor;
import landr.parser.Completer;
import landr.parser.syntax.CommandSyntax;

/**
 * Abstract class to be used for all Zookeeper commands.
 */
abstract class ZkCommandDescriptor<T extends ZkCommand> extends CommandDescriptor<T> {

    /**
     * Parameter name for all commands when targeting a path in ZK.
     * When used, it enables completion with {@link PathCompleter}.
     */
    static final String PATH_PARAM = "path";

    protected ZkCommandDescriptor(CommandSyntax syntax) {
        super(syntax);
    }

    @Override
    public Completer getCompleter() {
        return new PathCompleter();
    }
}
