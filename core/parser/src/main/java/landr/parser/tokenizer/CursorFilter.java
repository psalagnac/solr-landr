package landr.parser.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Token iterator that filters on a single statement, at the cursor position. Other statements
 * in the input are dropped.
 */
class CursorFilter extends AbstractTokenIterator {

    private final Iterator<Token> input;
    private final int cursor;

    private Iterator<Token> statement;

    public CursorFilter(Iterator<Token> input, int cursor) {
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    Token nextToken() {

        if (statement == null) {
            statement = findStatement();
        }

        if (statement == null || !statement.hasNext()) {
            return null;
        }

        return statement.next();
    }

    private Iterator<Token> findStatement() {
        Statement statement = nextStatement();
        Statement lastStatement = statement;

        // Cursor is before the beginning, return the first statement
        if (statement != null) {
            int start = statement.get(0).getStartPosition();
            if (cursor < start) {
                return statement.iterator();
            }
        }

        // Happy path: look for a statement with cursor position
        while (statement != null) {

            int start = statement.get(0).getStartPosition();
            int end = statement.getLast().getEndPosition();

            if (start <= cursor && end >= cursor) {
                return statement.iterator();
            }

            lastStatement = statement;
            statement = nextStatement();
        }

        // If the cursor is after the last statement, return last statement
        if (lastStatement != null && lastStatement.endReached) {
            int end = lastStatement.getLast().getEndPosition();
            if (cursor > end) {
                return lastStatement.iterator();
            }
        }

        return null;
    }

    /**
     * Iterator over the input to retrieve all the tokens for a complete statement.
     */
    private Statement nextStatement() {

        if (!input.hasNext()) {
            return null;
        }

        List<Token> statement = new ArrayList<>();

        boolean endReached = true;

        while (input.hasNext()) {
            Token token = input.next();
            if (token.isStatementSeparator()) {
                endReached = false;
                break;
            } else {
                statement.add(token);
            }
        }

        return new Statement(statement, endReached);
    }

    private static class Statement {
        private final List<Token> tokens;
        private final boolean endReached;

        public Statement(List<Token> tokens, boolean endReached) {
            this.tokens = tokens;
            this.endReached = endReached;
        }

        private Token get(int index) {
            return tokens.get(index);
        }

        private Token getLast() {
            return tokens.get(tokens.size() - 1);
        }


        private Iterator<Token> iterator() {
            return tokens.iterator();
        }
    }
}
