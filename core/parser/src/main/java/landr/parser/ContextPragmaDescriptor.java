package landr.parser;

import landr.parser.syntax.ContextKey;
import landr.parser.syntax.PragmaSyntax;

import java.util.ArrayList;
import java.util.List;

public class ContextPragmaDescriptor extends PragmaDescriptor<Pragma> {

    private static final String NAME = "context";

    private static final PragmaSyntax SYNTAX = new PragmaSyntax(NAME);

    ContextPragmaDescriptor() {
        super(SYNTAX);
    }

    @Override
    Pragma buildPragma(ParserContext context, CommandString string) throws CommandParseException {

        if (string.getParameterCount() == 0) {
            if (context.isInteractive()) {
                return new PrintContextPragma();
            } else {
                throw new CommandParseException(context, "context key expected, like '@context key=value'");
            }
        }

        List<Pragma> updatePragmas = new ArrayList<>();
        for (int i=0; i<string.getParameterCount(); i++) {
            String param = string.getParameter(i);

            String[] tokens = param.split("=", 2);
            if (tokens.length != 2) {
                throw new CommandParseException(context, "expected key=value but found " + param);
            }

            ContextKey key = ParserContext.resolveKey(context, tokens[0]);
            updatePragmas.add(new UpdateContextPragma(key, tokens[1]));
        }

        if (updatePragmas.size() == 1) {
            return updatePragmas.get(0);
        } else {
            return new CompoundPragma(updatePragmas);
        }
    }

    /**
     * Pragma to update a single value in the parser context.
     */
    private static class UpdateContextPragma extends Pragma {
        private final ContextKey key;
        private final String value;

        UpdateContextPragma(ContextKey key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        void execute(ParserContext context) {
            context.setProperty(key, value);
        }
    }

    /**
     * Pragma to print all the defined values in the parser context.
     */
    private static class PrintContextPragma extends Pragma {
        @Override
        void execute(ParserContext context) {

            boolean hasValue = false;

            for (ContextKey key : ContextKey.values()) {

                String value = context.getProperty(key);
                if (value != null) {
                    System.out.println(key.name + ":\t" + value);
                    hasValue = true;
                }
            }

            if (!hasValue) {
                System.out.println("no property defined");
            }
        }
    }
}