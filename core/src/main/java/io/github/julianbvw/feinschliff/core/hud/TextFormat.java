package io.github.julianbvw.feinschliff.core.hud;

/**
 * The colour codes the vanilla text renderer understands.
 *
 * <p>A section sign followed by one of {@code 0-9a-f} switches the colour for
 * the rest of the string. The renderer skips those two characters when it
 * measures a string, so a coloured line still aligns like a plain one.
 */
public final class TextFormat {

	private static final char SECTION = '§';

	public static final String BLACK = SECTION + "0";
	public static final String DARK_BLUE = SECTION + "1";
	public static final String DARK_GREEN = SECTION + "2";
	public static final String DARK_AQUA = SECTION + "3";
	public static final String DARK_RED = SECTION + "4";
	public static final String PURPLE = SECTION + "5";
	public static final String GOLD = SECTION + "6";
	public static final String GRAY = SECTION + "7";
	public static final String DARK_GRAY = SECTION + "8";
	public static final String BLUE = SECTION + "9";
	public static final String GREEN = SECTION + "a";
	public static final String AQUA = SECTION + "b";
	public static final String RED = SECTION + "c";
	public static final String PINK = SECTION + "d";
	public static final String YELLOW = SECTION + "e";
	public static final String WHITE = SECTION + "f";

	private TextFormat() {
	}

	/** Removes every colour code, for users who want a plain overlay. */
	public static String strip(String text) {
		if (text.indexOf(SECTION) < 0) {
			return text;
		}

		StringBuilder plain = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == SECTION && i + 1 < text.length()) {
				i++;
			} else {
				plain.append(c);
			}
		}
		return plain.toString();
	}
}
