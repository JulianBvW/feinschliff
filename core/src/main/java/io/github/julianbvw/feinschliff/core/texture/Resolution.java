package io.github.julianbvw.feinschliff.core.texture;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * The arithmetic the game writes with sixteen in it.
 *
 * <p>Almost nothing in the renderer cares how large a texture is. The many
 * {@code / 256.0F} and {@code / 128.0F} look like sheet sizes but are not:
 * a coordinate counted in the pixels this version draws, divided by the size
 * this version draws them on, is the same fraction of the sheet whatever the
 * sheet measures. Only the handful of places that index real pixels care, and
 * these are the sums those places need.
 *
 * <p>Both of these are pure: an array in, an array out, no game and no state.
 */
public final class Resolution {

	/** Sixteen tiles across, and sixteen characters to a row. */
	private static final int TILES = 16;

	/** The width of a character cell on the font this version was drawn for. */
	private static final int FONT_CELL = 8;

	/** The one space the game widens by hand, and to what. */
	private static final int SPACE = 32;
	private static final int SPACE_WIDTH = 4;

	private Resolution() {
	}

	/**
	 * How wide each of the two hundred and fifty-six characters is, measured
	 * off the font and answered in the eight-pixel measure the game lays text
	 * out in.
	 *
	 * <p>The game does this itself in the font's constructor, but counts in
	 * cells of eight whatever the picture measures, so a larger font is
	 * measured across its top left corner alone. This is the same walk with
	 * the cell the picture actually has: find the last column carrying ink,
	 * then put that width back on the eight-pixel scale. At a cell of eight
	 * the two are the same sum.
	 *
	 * @param pixels the whole font, row by row, alpha in the top byte
	 * @param size   its side length in pixels, sixteen cells across
	 */
	public static int[] fontWidths(int[] pixels, int size) {
		int cell = size / TILES;
		int[] widths = new int[256];

		for (int character = 0; character < widths.length; character++) {
			int column = character % TILES * cell;
			int row = character / TILES * cell;

			// The game asks the blue channel rather than alpha here. On every
			// font measured the two answer the same, and alpha is the right
			// question for a font whose characters are not white.
			int last = -1;
			for (int x = cell - 1; x >= 0 && last < 0; x--) {
				for (int y = 0; y < cell; y++) {
					if ((pixels[column + x + (row + y) * size] >>> 24) != 0) {
						last = x;
						break;
					}
				}
			}

			if (character == SPACE) {
				widths[character] = SPACE_WIDTH;
				continue;
			}

			// One pixel of air after the last one that carries ink, and the
			// ink itself rounded up so nothing is ever cut off.
			widths[character] = last < 0 ? 1
				: ((last + 1) * FONT_CELL + cell - 1) / cell + 1;
		}

		return widths;
	}

	/**
	 * The same picture at another size, for the two places that read a sheet
	 * by the pixel and would otherwise read a corner of it. Smooth rather than
	 * blocky, because this way round is throwing detail away, not inventing
	 * it.
	 */
	public static BufferedImage atSize(BufferedImage image, int size) {
		if (image.getWidth() == size && image.getHeight() == size) {
			return image;
		}

		BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = out.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(image, 0, 0, size, size, null);
		graphics.dispose();
		return out;
	}

	/**
	 * A square of pixels with every one of them repeated into a block, so that
	 * what the game draws at sixteen covers a tile of any size.
	 *
	 * <p>Four bytes to a pixel and no blending: this is pixel art being made
	 * bigger, not an image being resampled.
	 */
	public static byte[] magnify(byte[] rgba, int side, int factor) {
		if (factor <= 1) {
			return rgba;
		}

		int wide = side * factor;
		byte[] out = new byte[wide * wide * 4];

		for (int y = 0; y < side; y++) {
			for (int x = 0; x < side; x++) {
				int from = (x + y * side) * 4;
				for (int dy = 0; dy < factor; dy++) {
					int row = (y * factor + dy) * wide;
					for (int dx = 0; dx < factor; dx++) {
						int to = (row + x * factor + dx) * 4;
						out[to] = rgba[from];
						out[to + 1] = rgba[from + 1];
						out[to + 2] = rgba[from + 2];
						out[to + 3] = rgba[from + 3];
					}
				}
			}
		}

		return out;
	}
}
