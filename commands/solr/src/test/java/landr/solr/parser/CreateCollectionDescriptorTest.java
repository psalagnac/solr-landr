package landr.solr.parser;

import landr.parser.CommandParseException;
import landr.solr.cmd.CreateCollection;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CreateCollectionDescriptorTest extends AdminDescriptorTestBase {

    @Test
    public void testAllParams() throws CommandParseException {
        String statement = "create-collection" +
                " -name col" +
                " -shards 1" +
                " -replicas 2" +
                " -type NRT" +
                " -router r" +
                " -config conf" +
                " -async true";

        CreateCollection command = parseSingleCommand(statement, CreateCollection.class);
        assertNotNull(command);
    }

    @Override
    protected String getCollectionParameterName() {
        return "name";
    }

    @Override
    protected CreateCollectionDescriptor createDescriptor() {
        return new CreateCollectionDescriptor();
    }

}
