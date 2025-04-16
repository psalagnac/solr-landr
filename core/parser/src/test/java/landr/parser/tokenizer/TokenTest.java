package landr.parser.tokenizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;

public class TokenTest {

    /**
     * Check looking for position of a character.
     */
    @Test
    public void testIndexOf() {

        Token.Builder builder = new Token.Builder();

        Token base = builder
            .value("0123456789")
            .positions(0, 9)
            .build();

        assertEquals(0, base.indexOf('0'));
        assertEquals(1, base.indexOf('1'));
        assertEquals(5, base.indexOf('5'));
        assertEquals(9, base.indexOf('9'));
        assertEquals(-1, base.indexOf('a'));
    }

    /**
     * Test extracting a sub-token. Both start and end positions are inclusive (unlike java String).
     */
    @Test
    public void tesSubToken() {

        Token.Builder builder = new Token.Builder();

        Token base = builder
            .value("0123456789")
            .positions(0, 9)
            .build();

        // same view
        assertEquals(base, base.subToken(0));
        assertSame(base, base.subToken(0));

        // end of token
        assertEquals(builder.value("456789").positions(4, 9).build(), base.subToken(4));

        // single character
        assertEquals(builder.value("0").positions(0, 0).build(), base.subToken(0, 0));
        assertEquals(builder.value("1").positions(1, 1).build(), base.subToken(1, 1));
        assertEquals(builder.value("8").positions(8, 8).build(), base.subToken(8, 8));
        assertEquals(builder.value("9").positions(9, 9).build(), base.subToken(9, 9));

        // multiple characters
        assertEquals(builder.value("012").positions(0, 2).build(), base.subToken(0, 2));
        assertEquals(builder.value("345").positions(3, 5).build(), base.subToken(3, 5));
        assertEquals(builder.value("789").positions(7, 9).build(), base.subToken(7, 9));
    }

    /**
     * Test appending another token.
     */
    @Test
    public void testAppend() {

        Token base = new Token.Builder().value("hello").positions(0, 4).build();
        Token suffix = new Token.Builder().value("world").positions(6, 10).build();

        assertEquals(new Token.Builder().value("hello world").positions(0, 10).build(), base.append(suffix));
        assertEquals(new Token.Builder().value("hello w").positions(0, 6).build(), base.append(suffix, 0, 0));
        assertEquals(new Token.Builder().value("hello orl").positions(0, 9).build(), base.append(suffix, 1, 3));
        assertEquals(new Token.Builder().value("hello d").positions(0, 10).build(), base.append(suffix, 4, 4));
    }

    /**
     * Test removing leading and trailing white spaces.
     */
    @Test
    public void testTrim() {

        // no trim needed
        Token token = new Token.Builder().value("token").positions(3, 7).build();
        assertEquals(token, token.trim());
        assertSame(token, token.trim());

        // both leading and trailing spaces
        Token spaces = new Token.Builder().value("   token   ").positions(0, 11).build();
        assertEquals(token, spaces.trim());

        // leading space
        Token left = new Token.Builder().value("   token").positions(0, 8).build();
        assertEquals(token, left.trim());

        // trailing spaces
        Token right = new Token.Builder().value("token   ").positions(3, 11).build();
        assertEquals(token, right.trim());
    }

    /**
     * Test trimming when we only have spaces.
     */
    @Test
    public void testTrimBlank() {

        Token blank1 = new Token.Builder().value(" ").positions(6, 7).build();
        assertNull(blank1.trim());

        Token blank2 = new Token.Builder().value("    ").positions(0, 11).build();
        assertNull(blank2.trim());

    }

}
