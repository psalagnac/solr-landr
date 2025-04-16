package landr.solr.parser;

import landr.solr.cmd.SolrCommand;
import landr.parser.CommandDescriptor;
import landr.parser.Completer;
import landr.parser.syntax.CommandSyntax;

import java.util.Map;

/**
 * Abstract for all descriptors that build a SolrJ based command.
 */
public abstract class SolrCommandDescriptor<T extends SolrCommand> extends CommandDescriptor<T> {

    protected SolrCommandDescriptor(CommandSyntax syntax) {
        super(syntax);
    }

    @Override
    public Completer getCompleter() {

        Map<String, ClusterStateCompleter.CompletionType> types = getClusterStateCompletions();

        if (types == null || types.isEmpty()) {
            return null;
        } else {
            return new ClusterStateCompleter(types);
        }
    }

    /**
     * This can be overridden by subclasses to enabled completion from the SolrCloud cluster state. Default
     * implementation does not enable any completion.
     *
     * @return A per-argument mapping of completion type to enable.
     *
     * @see ClusterStateCompleter
     */
    public Map<String, ClusterStateCompleter.CompletionType> getClusterStateCompletions() {
        return null;
    }

}
