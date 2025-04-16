package landr.cmd;

/**
 * Basic implementation of the execution context. This is mostly to be used when
 * in non-interactive mode.
 *
 * <p> Error messages go to the standard error output ({@link System#err}) while other messages go to
 * standard output ({@link System#out}).
 */
public class DefaultExecutionContext extends CommandExecutionContext {

    public DefaultExecutionContext(CommandEnvironment environment) {
        super(environment);
    }

    @Override
    public void print(String message, OutputStyle style) {
        System.out.print(message);
    }

    @Override
    public void println(String message, OutputStyle style) {
        System.out.println(message);
    }

    @Override
    public void printVerbose(String message, OutputStyle style) {
        System.out.print(message);
    }

    @Override
    public void printlnVerbose(String message, OutputStyle style) {
        System.out.println(message);
    }

    @Override
    public void printError(String message, OutputStyle style) {
        System.err.print(message);
    }

    @Override
    public void printlnError(String message, OutputStyle style) {
        System.err.println(message);
    }

    @Override
    public void printStackTrace(Throwable error) {
        error.printStackTrace();
    }

    @Override
    public void flush() {
        System.out.flush();
        System.err.flush();
    }
}
