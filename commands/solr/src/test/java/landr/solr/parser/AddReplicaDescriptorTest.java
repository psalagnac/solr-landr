package landr.solr.parser;

import landr.parser.CommandParseException;
import landr.solr.cmd.AddReplica;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AddReplicaDescriptorTest extends AdminDescriptorTestBase {

    @Test
    public void testAllParams() throws CommandParseException {
        String statement = "add-replica -collection c -shard s -type NRT -async true";

        AddReplica command = parseSingleCommand(statement, AddReplica.class);
        assertNotNull(command);
    }

    @Override
    protected AddReplicaDescriptor createDescriptor() {
        return new AddReplicaDescriptor();
    }
}
