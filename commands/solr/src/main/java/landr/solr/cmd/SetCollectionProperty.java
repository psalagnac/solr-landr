package landr.solr.cmd;

import java.io.IOException;
import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;

/** Admin command to set a collection property in the cluster state. */
public class SetCollectionProperty extends SolrAdminCommand {

  private final String name;
  private final String value;

  public SetCollectionProperty(Builder builder) {
    super(builder);
    this.name = builder.name;
    this.value = builder.value;
  }

  @Override
  public void execute(CommandExecutionContext context, SolrClient client)
      throws SolrServerException, IOException, CommandExecutionException {

    CollectionAdminRequest.CollectionProp request =
        CollectionAdminRequest.setCollectionProperty(collection, name, value);

    processAdminRequest(context, request);
  }

  public static class Builder extends AdminCommandBuilder {

    private String name;
    private String value;

    public Builder(String collection) {
      super(collection);
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
