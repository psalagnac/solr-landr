package landr.cmd;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CommandEnvironmentTest {

    private static class Parent implements AutoCloseable {
        private boolean closed;
        @Override
        public void close() {
            closed = true;
        }
    }
    private static class Child extends Parent {}

    /**
     * Check we can retrieve an object with either the parent or the child class.
     */
    @Test
    public void testSubClass() throws Exception {

        CommandEnvironment environment = new CommandEnvironment();

        Child object = new Child();
        environment.addObject(object);

        assertSame(object, environment.getObject(Child.class));
        assertSame(object, environment.getObject(Parent.class));
    }

    /**
     * Check we close registered objects.
     */
    @Test
    public void testClose() throws Exception {

        CommandEnvironment environment = new CommandEnvironment();

        Parent child = new Child();
        environment.addObject(child);
        environment.close();

        assertTrue(child.closed);
    }
}
