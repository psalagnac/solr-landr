package landr.parser.tokenizer;

import java.util.Iterator;

/**
 * Iterator of token when parsing command line.
 */
public class CommandTokenizer implements Iterator<Token> {

    private final Iterator<Token> delegate;

    /**
     * Create a new tokenizer that will return all the tokens found in the input.
     */
    public CommandTokenizer(CharSequence input) {
        Tokenizer tokenizer = new Tokenizer(input);
        PhraseMerger merger = new PhraseMerger(tokenizer);
        StatementSplitter splitter = new StatementSplitter(merger);
        delegate = new Trimmer(splitter);
    }

    /**
     * Create a new tokenizer returning only tokens for the statement at the specified position.
     */
    public CommandTokenizer(CharSequence input, int cursor) {
        Tokenizer tokenizer = new Tokenizer(input);
        PhraseMerger merger = new PhraseMerger(tokenizer);
        StatementSplitter splitter = new StatementSplitter(merger);
        CursorFilter filter = new CursorFilter(splitter, cursor);
        delegate = new Trimmer(filter);

    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public Token next() {
        return delegate.next();
    }

}
