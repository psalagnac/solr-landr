package landr.solr.cmd;

import landr.cmd.Command;
import landr.cmd.CommandEnvironment;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import landr.cmd.OutputStyle;
import landr.cmd.FormatColor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of commands that always send command to Solr cluster.
 */
public abstract class SolrCommand extends Command {

    @Override
    public void execute(CommandExecutionContext context) throws CommandExecutionException {

        CommandEnvironment environment = context.getEnvironment();
        SolrClient client = environment.getObject(SolrClient.class);

        try {
            execute(context, client);
        } catch (IOException | SolrServerException | SolrException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Implementation for Solr commands.
     */
    public abstract void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException;

    /**
     * Cast the solr client into a cloud aware client. An explicit exception is raised if the client is
     * not compatible.
     */
    protected CloudSolrClient toCloudClient(SolrClient client) throws CommandExecutionException {

        if (client instanceof CloudSolrClient) {
            return (CloudSolrClient)client;
        } else {
            throw new CommandExecutionException(new ClassCastException("Can't run command with non-cloud client"));
        }
    }

    /**
     * Utility method to send a synchronous command to the Solr cluster.
     * Using this method instead of directly calling SolrJ is preferred.
     */
    protected <R extends SolrResponse> R processRequest(CommandExecutionContext context, SolrRequest<R> request)
    throws SolrServerException, IOException, CommandExecutionException {
        return processRequest(context, request, null);
    }

    /**
     * Utility method to send a synchronous command to the Solr cluster, collection name is specified to SolrJ.
     * Using this method instead of directly calling SolrJ is preferred.
     */
    protected <R extends SolrResponse> R processRequest(CommandExecutionContext context, SolrRequest<R> request, String collection)
    throws SolrServerException, IOException, CommandExecutionException {

        CommandEnvironment environment = context.getEnvironment();
        SolrClient client = environment.getObject(SolrClient.class);

        R response = request.process(client, collection);

        if (context.getVerbose()) {
            if (response instanceof SolrResponseBase) {
                SolrResponseBase responseBase = (SolrResponseBase)response;
                context.printlnVerbose("Status: " + responseBase.getStatus());
                context.printlnVerbose("QTime: " + responseBase.getQTime());
            }
        }

        return response;
    }

    /**
     * Utility method to send an asynchronous command to the Solr cluster. The async ID is generated and printed.
     * Using this method instead of directly calling SolrJ is preferred.
     */
    protected void processAsyncRequest(CommandExecutionContext context, CollectionAdminRequest.AsyncCollectionAdminRequest request)
    throws SolrServerException, IOException, CommandExecutionException {

        String id = context.generateId();
        request.setAsyncId(id);
        context.println("ID: " + id);

        processRequest(context, request);
    }

    /**
     * Recursive walkthrough all objects to display a JSon like output.
     * This is mostly useful to print a {@link NamedList} from a Solr response.
     */
    protected void printNamedObject(CommandExecutionContext context, String name, Object object) {
        printNamedObject(context, name, object, 0);
    }

    private void printNamedObject(CommandExecutionContext context, String name, Object object, int level) {

        printIndent(context, level);

        if (name != null) {
            printName(context, name);
            context.print(": ");
        }

        if (object instanceof NamedList) {
            NamedList<?> map = (NamedList<?>) object;
            printEntries(context, map, level);
        } else if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            printEntries(context, map.entrySet(), level);
        } else if (object instanceof List) {
            printList(context, object, level);
        }
        else {
            printObject(context, object);
        }
    }

    private <K,V> void printEntries(CommandExecutionContext context, Iterable<Map.Entry<K, V>> entries, int level) {
        context.println("{");

        Iterator<Map.Entry<K,V>> iterator = entries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();

            Object k = entry.getKey();
            Object v = entry.getValue();
            printNamedObject(context, k.toString(), v, level + 1);

            if (iterator.hasNext()) {
                context.println(",");
            } else {
                context.println();
            }
        }

        printIndent(context, level);
        context.print("}");
    }

    private void printList(CommandExecutionContext context, Object object, int level) {
        List<?> list = (List<?>) object;

        context.println("[");

        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();

            printIndent(context, level + 1);
            printObject(context, item);

            if (iterator.hasNext()) {
                context.println(",");
            } else {
                context.println();
            }
        }

        printIndent(context, level);
        context.print("]");
    }

    private void printName(CommandExecutionContext context, String name) {
        context.print("\"");
        context.print(name);
        context.print("\"");
    }

    private void printObject(CommandExecutionContext context, Object object) {

        if (object == null) {
            context.print("null", OutputStyle.DEFAULT.color(FormatColor.NULL));
        } else if (object instanceof String) {
            context.print(String.format("\"%s\"", object), OutputStyle.DEFAULT.color(FormatColor.STRING));
        } else {
            OutputStyle style = OutputStyle.DEFAULT;
            if (object instanceof Number) {
                style = style.color(FormatColor.NUMBER);
            } else if (object instanceof Boolean) {
                style = style.color(FormatColor.BOOLEAN);
            }

            context.print(object.toString(), style);
        }
    }

    /**
     * Print whitespaces for JSon indentation.
     */
    private void printIndent(CommandExecutionContext context, int level) {
        for (int i = 0; i < level * 3; i++) {
            context.print(" ");
        }
    }
}
