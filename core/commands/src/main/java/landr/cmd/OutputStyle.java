package landr.cmd;

/**
 * Simple structure to override style when command writes into the context output.
 * Depending on the actually output implementation supporting styles is optional.
 */
public class OutputStyle {

    public static final OutputStyle DEFAULT = new OutputStyle(0, FormatColor.DEFAULT);

    // Convenience constants
    public static final OutputStyle BOLD      = DEFAULT.bold();
    public static final OutputStyle ITALIC    = DEFAULT.italic();
    public static final OutputStyle UNDERLINE = DEFAULT.underline();

    private static final int BIT_BOLD      = 0b0001;
    private static final int BIT_ITALIC    = 0b0010;
    private static final int BIT_UNDERLINE = 0b0100;

    private final int style;
    private final FormatColor color;

    private OutputStyle(int style, FormatColor color) {
        this.style = style;
        this.color = color;
    }

    /**
     * Check whether this style is the default style.
     * Default style does not override any parameter in the output format.
     */
    public boolean isDefault() {
        return style == 0 && color == FormatColor.DEFAULT;
    }

    public OutputStyle bold() {
        return addBitStyle(BIT_BOLD);
    }

    public boolean isBold() {
        return checkBitStyle(BIT_BOLD);
    }

    public OutputStyle italic() {
        return addBitStyle(BIT_ITALIC);
    }

    public boolean isItalic() {
        return checkBitStyle(BIT_ITALIC);
    }

    public OutputStyle underline() {
        return addBitStyle(BIT_UNDERLINE);
    }

    public boolean isUnderline() {
        return checkBitStyle(BIT_UNDERLINE);
    }

    private OutputStyle addBitStyle(int bit) {
        return new OutputStyle(style | bit, color);
    }

    private boolean checkBitStyle(int bit) {
        return (style & bit) != 0;
    }

    public OutputStyle color(FormatColor color) {
        return new OutputStyle(style, color);
    }

    public FormatColor getColor() {
        return color;
    }

}
