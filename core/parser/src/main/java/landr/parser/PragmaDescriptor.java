package landr.parser;

import landr.parser.syntax.PragmaSyntax;

import java.util.List;

abstract class PragmaDescriptor<T extends Pragma> extends StatementDescriptor {

    protected PragmaDescriptor(PragmaSyntax syntax) {
        super(syntax);
    }

    abstract T buildPragma(ParserContext context, CommandString string) throws CommandParseException;

    /**
     * A pragma that executes a list of pragmas.
     */
    static class CompoundPragma extends Pragma {

        private final List<Pragma> pragmas;

        CompoundPragma(List<Pragma> pragmas) {
            this.pragmas = pragmas;
        }

        @Override
        void execute(ParserContext context) {
            for (Pragma pragma : pragmas) {
                pragma.execute(context);
            }
        }
    }

}
