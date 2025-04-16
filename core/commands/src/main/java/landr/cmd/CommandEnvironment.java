package landr.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Execution environment with external resources for commands.
 * It is used for example to store Solr client or Zookeeper client.
 */
public class CommandEnvironment {

    private final Random random;
    private final Map<String, String> properties;
    private final Map<Class<?>, AutoCloseable> objects;

    /**
     * Create a new environment with a random seed. To be used only by tests.
     */
    public CommandEnvironment() {
        this(new HashMap<>(), new Random());
    }

    public CommandEnvironment(Map<String, String> properties, Random random) {
        this.random = random;
        this.properties = properties;

        objects = new HashMap<>();
    }

    /**
     * Return a global property from the environment. Global properties are available when registering commands,
     * and cannot be modified.
     */
    public String getProperty(String name) {
        return properties.get(name);
    }

    /**
     * Add a new object, that can be later retrieved with {@link #getObject(Class)}.
     *
     * <p> All registered objects are closed when invoking {@link #close()}.
     */
    public void addObject(AutoCloseable object) {

        Class<?> clazz = object.getClass();
        objects.put(clazz, object);
    }

    public <T> T getObject(Class<T> clazz) throws CommandExecutionException {

        // first do a lookup with the exact class
        AutoCloseable object = objects.get(clazz);
        if (object != null) {
            return clazz.cast(object);
        }

        // Iterate over all registered object to check if one is assignable
        for (AutoCloseable entry : objects.values()) {
            if (clazz.isAssignableFrom(entry.getClass())) {
                object = entry;
                break;
            }
        }

        if (object == null) {
            throw new CommandExecutionException("no object for class: " + clazz.getName());
        }

        // We found something !
        // Register this object with the requested class, so a direct lookup will work next time
        objects.put(clazz, object);
        return clazz.cast(object);
    }

    /**
     * Random instance to be used, so script executions may be reproduced by specifying the seed.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Close all previously registered object and drop references.
     */
    public void close() throws Exception {

        for (AutoCloseable object : objects.values()) {
            object.close();
        }

        objects.clear();
    }
}
