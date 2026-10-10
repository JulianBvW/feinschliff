package io.github.julianbvw.feinschliff.core.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import io.github.julianbvw.feinschliff.core.Feinschliff;

/**
 * Puts a pack of a later era back into the grid this version reads.
 *
 * <p>A texture pack is sixteen by sixteen tiles and the game addresses them by
 * number, so a pack drawn for a later version is not wrong, it is filed
 * differently: what this version looks for at one number sits at another, and
 * the number it looks at holds something else entirely. Two such moves exist
 * between this version and the beta line, both of them below.
 *
 * <p>This is version data in the sense of the project's layout rules: every
 * entry falls away once the mod runs on the version the pack was drawn for, so
 * the tables shrink towards empty rather than grow.
 *
 * <p>Which era a pack belongs to is told, not guessed. A pack drawn for this
 * version can carry anything at all in the tiles the later era uses -- one of
 * the packs this was measured against has a perfectly ordinary block sitting at
 * 164 -- so every rule that could read the era off the picture gets at least
 * one real pack wrong.
 *
 * <p>Nothing here may throw, and nothing here writes to disk. The pack's own
 * file stays as it is; the tiles move in a copy on the way to the game.
 */
public final class TileGrid {

	/** This version's own grid: the pack is taken as it is. */
	public static final String ALPHA = "alpha";

	/** The grid of the beta line, and of everything after it. */
	public static final String BETA = "beta";

	public static final String[] ERAS = {ALPHA, BETA};

	/** Sixteen tiles across, in every version there has ever been. */
	private static final int TILES = 16;

	/**
	 * Tiles that move unchanged. Iron, gold and diamond blocks are drawn with a
	 * top, a side and a bottom here, and with a single texture from the beta
	 * line on; later packs therefore carry only the first of each three, and
	 * fill the other two with the plants that version keeps there.
	 */
	private static final int[][] BETA_FACES = {
		{22, 38}, {22, 54},   // iron, side and bottom
		{23, 39}, {23, 55},   // gold
		{24, 40}, {24, 56}    // diamond
	};

	/**
	 * Tiles that move and are coloured, and by how much of their brightness.
	 * Redstone wire moved four rows down and lost the difference between
	 * powered and unpowered on the way, because that version draws one grey
	 * wire and colours it as it goes. The two percentages are this version's
	 * own two tiles measured against each other: unpowered peaks at 72, powered
	 * at 253, and both are pure red.
	 */
	private static final int[][] BETA_WIRE = {
		{164,  84,  28},   // cross, unpowered
		{165,  85,  28},   // line, unpowered
		{164, 100, 100},   // cross, powered
		{165, 101, 100}    // line, powered
	};

	private TileGrid() {
	}

	/**
	 * The same sheet with the tiles of that era moved into place, or the bytes
	 * exactly as they came in when there is nothing to do or anything at all
	 * goes wrong.
	 */
	public static byte[] regrid(byte[] terrain, String era) {
		if (terrain == null || !BETA.equalsIgnoreCase(era)) {
			return terrain;
		}

		try {
			BufferedImage read = ImageIO.read(new ByteArrayInputStream(terrain));
			if (read == null) {
				return terrain;
			}

			int width = read.getWidth();
			if (width != read.getHeight() || width % TILES != 0) {
				return terrain;
			}

			int size = width / TILES;
			BufferedImage sheet = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
			sheet.setRGB(0, 0, width, width,
				read.getRGB(0, 0, width, width, null, 0, width), 0, width);

			int faces = 0;
			for (int[] move : BETA_FACES) {
				faces += move(sheet, move[0], move[1], size, 0) ? 1 : 0;
			}

			int wires = 0;
			for (int[] move : BETA_WIRE) {
				wires += move(sheet, move[0], move[1], size, move[2]) ? 1 : 0;
			}

			if (wires == 0) {
				Feinschliff.log().warn("texture pack: the era is set to '" + BETA + "', but this"
					+ " pack draws no redstone wire where that era keeps it; it was probably"
					+ " drawn for this version");
			}

			if (faces + wires == 0) {
				return terrain;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream(terrain.length);
			if (!ImageIO.write(sheet, "png", out)) {
				return terrain;
			}

			Feinschliff.log().debug("texture pack: moved " + (faces + wires)
				+ " tiles into this version's grid");
			return out.toByteArray();
		} catch (Throwable t) {
			Feinschliff.log().warn("texture pack: terrain.png could not be put into this"
				+ " version's grid: " + t + "; it is used as it is");
			return terrain;
		}
	}

	/**
	 * Copies one tile over another, red at that percentage of its brightness
	 * when a percentage is given and unchanged at {@code 0}. False when the
	 * source tile is empty, in which case the target keeps what it had -- a
	 * pack of this era that is told it is of the other one loses nothing it
	 * actually draws.
	 */
	private static boolean move(BufferedImage sheet, int from, int to, int size, int percent) {
		int[] pixels = sheet.getRGB((from % TILES) * size, (from / TILES) * size,
			size, size, null, 0, size);

		boolean empty = true;
		for (int i = 0; i < pixels.length; i++) {
			if ((pixels[i] >>> 24) != 0) {
				empty = false;
				break;
			}
		}
		if (empty) {
			return false;
		}

		if (percent > 0) {
			for (int i = 0; i < pixels.length; i++) {
				int argb = pixels[i];
				int level = Math.max(Math.max((argb >> 16) & 0xFF, (argb >> 8) & 0xFF), argb & 0xFF);
				pixels[i] = (argb & 0xFF000000) | ((level * percent / 100) << 16);
			}
		}

		sheet.setRGB((to % TILES) * size, (to / TILES) * size, size, size, pixels, 0, size);
		return true;
	}
}
