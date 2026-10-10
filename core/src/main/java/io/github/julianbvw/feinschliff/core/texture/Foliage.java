package io.github.julianbvw.feinschliff.core.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.github.julianbvw.feinschliff.core.Feinschliff;

/**
 * Colours the grass and leaves a pack leaves grey.
 *
 * <p>From the beta line on, packs draw both in grey and let the game colour
 * them as it draws, by the climate of the biome the block stands in. This
 * version colours nothing -- {@code Block.getColorTint} is a literal white that
 * no block overrides -- so a pack made that way shows grey grass and grey
 * leaves until the colour is put into the tile.
 *
 * <p>The colour is the pack's own. A pack that works this way carries
 * {@code misc/grasscolor.png} and {@code misc/foliagecolor.png}, a map of
 * climate to colour, and a pack that has already painted its tiles carries
 * those maps as plain white. Multiplying by white changes nothing, so the two
 * kinds need no telling apart: the pack states what it wants and the answer
 * falls out.
 *
 * <p>There are no biomes here, so one spot in the map stands for the whole
 * world. The tiles are named one by one rather than found by how grey they are,
 * and the snowed grass side is why: it is pale because it is snow, and colouring
 * it would turn the snow green.
 *
 * <p>Nothing here may throw, and nothing here writes to disk.
 */
public final class Foliage {

	/** Sixteen tiles across, in every version there has ever been. */
	private static final int TILES = 16;

	/** The one grass tile that is grass rather than dirt or snow. */
	private static final int[] GRASS = {0};

	/** Leaves, drawn through and drawn solid. */
	private static final int[] FOLIAGE = {52, 53};

	/**
	 * The climate the colour map is read at. Later versions look up the biome's
	 * temperature and rainfall; this version has neither, so this is a single
	 * ordinary green rather than a range. The map is a triangle with its unused
	 * half above the diagonal, and these two put the reading well inside it.
	 */
	private static final double TEMPERATURE = 0.8;
	private static final double RAINFALL = 0.4;

	/** The side length the climate arithmetic below is written in. */
	private static final int MAP_SIZE = 256;

	private static final int WHITE = 0xFFFFFF;

	private Foliage() {
	}

	/**
	 * The same sheet with the grey tiles coloured, or the bytes exactly as they
	 * came in when there is nothing to do or anything at all goes wrong.
	 *
	 * @param below leave a tile alone once it carries more colour than this, so
	 *              a painted tile cannot come out painted twice. Zero turns the
	 *              whole thing off.
	 */
	public static byte[] tint(byte[] terrain, byte[] grassMap, byte[] foliageMap, double below) {
		if (terrain == null || below <= 0.0 || (grassMap == null && foliageMap == null)) {
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

			int painted = paint(sheet, GRASS, grassMap, below, size, "grass")
				+ paint(sheet, FOLIAGE, foliageMap, below, size, "leaves");

			if (painted == 0) {
				return terrain;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream(terrain.length);
			if (!ImageIO.write(sheet, "png", out)) {
				return terrain;
			}

			Feinschliff.log().debug("texture pack: coloured " + painted + " grey tiles");
			return out.toByteArray();
		} catch (Throwable t) {
			Feinschliff.log().warn("texture pack: terrain.png could not be coloured: " + t
				+ "; it is used as it is");
			return terrain;
		}
	}

	/** How many of those tiles ended up coloured. */
	private static int paint(BufferedImage sheet, int[] tiles, byte[] map, double below,
			int size, String what) {
		int colour = WHITE;
		if (map != null) {
			try {
				colour = sample(map);
			} catch (Throwable t) {
				Feinschliff.log().warn("texture pack: the colour map for " + what
					+ " could not be read: " + t + "; they stay as they are");
				return 0;
			}
		}

		int painted = 0;
		for (int i = 0; i < tiles.length; i++) {
			int x = (tiles[i] % TILES) * size;
			int y = (tiles[i] / TILES) * size;
			int[] pixels = sheet.getRGB(x, y, size, size, null, 0, size);

			if (saturation(pixels) >= below) {
				continue;
			}

			if (map == null) {
				Feinschliff.log().info("texture pack: the " + what + " are drawn grey, but the"
					+ " pack carries no colour map for them, so they stay grey");
				return painted;
			}

			if (colour == WHITE) {
				continue;
			}

			int red = (colour >> 16) & 0xFF;
			int green = (colour >> 8) & 0xFF;
			int blue = colour & 0xFF;
			for (int p = 0; p < pixels.length; p++) {
				int argb = pixels[p];
				pixels[p] = (argb & 0xFF000000)
					| ((((argb >> 16) & 0xFF) * red / 255) << 16)
					| ((((argb >> 8) & 0xFF) * green / 255) << 8)
					| ((argb & 0xFF) * blue / 255);
			}

			sheet.setRGB(x, y, size, size, pixels, 0, size);
			painted++;
		}

		return painted;
	}

	/**
	 * The colour this map holds for the climate above, the way later versions
	 * look it up, and scaled should a pack draw its map at another size.
	 */
	private static int sample(byte[] map) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(map));
		if (image == null) {
			return WHITE;
		}

		int x = (int)((1.0 - TEMPERATURE) * (MAP_SIZE - 1));
		int y = (int)((1.0 - RAINFALL * TEMPERATURE) * (MAP_SIZE - 1));

		x = Math.min(x * image.getWidth() / MAP_SIZE, image.getWidth() - 1);
		y = Math.min(y * image.getHeight() / MAP_SIZE, image.getHeight() - 1);

		return image.getRGB(x, y) & 0xFFFFFF;
	}

	/**
	 * How much colour the tile carries, zero being grey. Taken from the average
	 * of its opaque pixels rather than averaged over them, so that a tile of
	 * two strong colours reads as coloured and a dark tile does not read as
	 * noise. A tile with nothing opaque in it answers one and is left alone.
	 */
	private static double saturation(int[] pixels) {
		long red = 0;
		long green = 0;
		long blue = 0;
		int opaque = 0;

		for (int i = 0; i < pixels.length; i++) {
			if ((pixels[i] >>> 24) == 0) {
				continue;
			}
			red += (pixels[i] >> 16) & 0xFF;
			green += (pixels[i] >> 8) & 0xFF;
			blue += pixels[i] & 0xFF;
			opaque++;
		}

		if (opaque == 0) {
			return 1.0;
		}

		double r = (double)red / opaque;
		double g = (double)green / opaque;
		double b = (double)blue / opaque;
		double high = Math.max(r, Math.max(g, b));
		double low = Math.min(r, Math.min(g, b));

		return high == 0.0 ? 0.0 : (high - low) / high;
	}
}
