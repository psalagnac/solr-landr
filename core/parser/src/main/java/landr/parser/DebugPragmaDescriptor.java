package landr.parser;

import landr.cmd.Command;
import landr.cmd.CommandExecutionContext;
import landr.parser.syntax.PragmaSyntax;

import java.util.function.BiConsumer;

class DebugPragmaDescriptor extends PragmaDescriptor<DebugPragmaDescriptor.DebugPragma>  {

    private static final String DEBUG_NAME   = "debug";
    private static final String VERBOSE_NAME = "verbose";
    private static final String TRACE_NAME   = "trace";

    private static final PragmaSyntax DEBUG_SYNTAX;
    private static final PragmaSyntax VERBOSE_SYNTAX;
    private static final PragmaSyntax TRACE_SYNTAX;
    static {
        DEBUG_SYNTAX = new PragmaSyntax(DEBUG_NAME);
        VERBOSE_SYNTAX = new PragmaSyntax(VERBOSE_NAME);
        TRACE_SYNTAX = new PragmaSyntax(TRACE_NAME);
    }

    private DebugPragmaDescriptor(PragmaSyntax syntax) {
        super(syntax);
    }

    static PragmaDescriptor<?> createDebugDescriptor() {
        return new DebugPragmaDescriptor(DEBUG_SYNTAX);
    }

    static PragmaDescriptor<?> createVerboseDescriptor() {
        return new DebugPragmaDescriptor(VERBOSE_SYNTAX);
    }

    static PragmaDescriptor<?> createTraceDescriptor() {
        return new DebugPragmaDescriptor(TRACE_SYNTAX);
    }

    @Override
    DebugPragma buildPragma(ParserContext context, CommandString string) {

        boolean flag = true;
        String flagValue = string.getParameter(0);
        if (flagValue != null) {
            flag = Boolean.parseBoolean(flagValue);
        }

        DebugAction action = createAction(flag);
        return new DebugPragma(action);
    }

    private DebugAction createAction(boolean flag) {
        String name = getName();

        switch (name) {
            case VERBOSE_NAME:
                return new DebugAction(flag, CommandExecutionContext::setVerbose, ParserContext::setVerbose);
            case TRACE_NAME:
                return new DebugAction(flag, null, ParserContext::setTrace);

        }
        // by default, switch debug flag
        return new DebugAction(flag, CommandExecutionContext::setDebug, ParserContext::setDebug);
    }

    /**
     * Internal action to change the debug flag.
     * Can be either invoked in the command execution context or the command parsing context.
     */
    private static class DebugAction {

        private final boolean flag;

        private final BiConsumer<CommandExecutionContext, Boolean> commandAction;
        private final BiConsumer<ParserContext, Boolean> parserAction;

        private DebugAction(boolean flag, BiConsumer<CommandExecutionContext, Boolean> commandAction, BiConsumer<ParserContext, Boolean> parserAction) {
            this.flag = flag;
            this.commandAction = commandAction;
            this.parserAction = parserAction;
        }

        /**
         * Check whether this pragma has an impact at runtime in middle-of a script execution. When true, we insert
         * a {@link Command} to execute the action.
         */
        private boolean hasCommand() {
            return commandAction != null;
        }

        private void execute(CommandExecutionContext context) {
            commandAction.accept(context, flag);
        }

        private void execute(ParserContext context) {
            parserAction.accept(context, flag);
        }
    }

    /**
     * Execution of the pragma.
     */
    static class DebugPragma extends Pragma {

        private final DebugAction action;

        DebugPragma(DebugAction action) {
            this.action = action;
        }

        @Override
        void execute(ParserContext context) {
            action.execute(context);
        }

        /**
         * Add a command to change the debug flag during execution.
         */
        @Override
        Command getCommand() {
            if (action.hasCommand()) {
                return new DebugCommand(action);
            } else {
                return null;
            }
        }
    }

    static class DebugCommand extends Command {

        private final DebugAction action;

        DebugCommand(DebugAction action) {
            this.action = action;
        }

        @Override
        public void execute(CommandExecutionContext context) {
            action.execute(context);
        }
    }
}
