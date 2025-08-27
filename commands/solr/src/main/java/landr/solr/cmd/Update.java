package landr.solr.cmd;

import landr.cmd.CommandExecutionContext;
import landr.cmd.CommandExecutionException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.UpdateParams;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class Update extends SolrDataCommand {

    private final int count;
    private final int batches;
    private final boolean commit;
    private final boolean softCommit;
    private final DocGenerator generator;

    public Update(String collection, int count, boolean commit, boolean softCommit) {
        super(collection);
        this.count = count;
        this.commit = commit;
        this.softCommit = softCommit;

        this.generator = Update::defaultDocGenerator;
        this.batches = 1;
    }

    public Update(Builder builder) {
        super(builder);
        this.count = builder.count;
        this.batches = builder.batches;
        this.commit = builder.commit;
        this.softCommit = builder.softCommit;
        this.generator = builder.generator != null ? builder.generator : Update::defaultDocGenerator;
    }

    @Override
    public void execute(CommandExecutionContext context, SolrClient client)
    throws SolrServerException, IOException, CommandExecutionException {

        for (int i=0; i<batches; i++){
            executeBatch(context);
        }
    }

    private void executeBatch(CommandExecutionContext context)
    throws SolrServerException, IOException, CommandExecutionException {
        UpdateRequest request = new UpdateRequest();

        for (int i=0; i<count; i++){
            request.add(generateDoc(context.getRandom()));
        }

        if (commit) {
            request.setParam(UpdateParams.COMMIT, "true");
        }
        if (softCommit) {
            request.setParam(UpdateParams.SOFT_COMMIT, "true");
        }

        processRequest(context, request, collection);
    }

    private SolrInputDocument generateDoc(Random random) {

        SolrInputDocument document = new SolrInputDocument();

        Map<String,String> fields = generator.generate(random);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            document.setField(entry.getKey(), entry.getValue());
        }

        return document;
    }

    /**
     * Builder pattern.
     */
    public static class Builder extends DataCommandBuilder {

        private int count;
        private int batches;
        private boolean commit;
        private boolean softCommit;
        private DocGenerator generator;

        public Builder(String collection) {
            super(collection);
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setBatches(int batches) {
            this.batches = batches;
        }

        public void setCommit(boolean commit) {
            this.commit = commit;
        }

        public void setSoftCommit(boolean softCommit) {
            this.softCommit = softCommit;
        }

        public void setGenerator(DocGenerator generator) {
            this.generator = generator;
        }
    }

    @FunctionalInterface
    public interface DocGenerator {

        /**
         * Generate a single record to be indexed.
         * In case the generator has some randomization, the specified random instance must be
         * used to have reproducible runs.
         */
        Map<String, String> generate(Random random);
    }

    private static Map<String, String> defaultDocGenerator(Random random) {
        String id = String.valueOf(random.nextInt(100_000_000));
        return Collections.singletonMap("id", id);
    }
}
