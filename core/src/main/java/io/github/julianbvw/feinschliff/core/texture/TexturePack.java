package io.github.julianbvw.feinschliff.core.texture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.platform.Textures;

/**
 * The pack that answers before the game's own files do.
 *
 * <p>This version has no notion of a texture pack at all, so there is nothing
 * to extend and no format to obey: a pack here is a folder or a zip holding
 * the same paths the game asks its own jar for. Answering {@code null} hands
 * the question back, which is what makes an incomplete pack work -- whatever
 * it does not carry stays the game's.
 *
 * <p>Two of the tables below are version data and shrink towards empty. The
 * aliases are the paths later versions moved out of the root, and they stop
 * mattering once the mod runs on the version a pack was built for. The tile
 * size is what this version's upload buffer can hold, and it grows once the
 * buffer does.
 *
 * <p>Nothing here may throw. A pack is something a player dropped in a folder,
 * and the worst a bad one may do is leave the game looking the way it did
 * before.
 */
public final class TexturePack {

	/** Beside saves and config, and the same name later versions use. */
	private static final String FOLDER = "texturepacks";

	/** The file that makes a folder a texture pack rather than a folder. */
	private static final String TERRAIN = "/terrain.png";

	/** Sixteen tiles across, in every version there has ever been. */
	private static final int TILES = 16;

	/**
	 * The one tile size this version can show. Its upload buffer is a fixed
	 * megabyte, which is 512 by 512 pixels exactly, and its animated sprites
	 * write sixteen pixels wherever they write -- so a larger pack does not
	 * look wrong, it fails to load.
	 */
	private static final int TILE_SIZE = 16;

	/** Four bytes a pixel, and the buffer is 1 MiB, so this is 512 by 512. */
	private static final int MAX_BYTES = 1048576;

	/**
	 * The three files the game walks with hardcoded pixel arithmetic, and the
	 * size it walks them at. Everything else is read through coordinates that
	 * divide out, so only these three care.
	 *
	 * <p>Two of them are a crash rather than a blemish, and not a caught one:
	 * a font below 128 square runs the width measurement off the end of its
	 * own pixel array, and an item sheet below 256 square does the same to the
	 * compass. Both happen while the game is starting, before anything is on
	 * screen.
	 */
	private static final String[] FIXED_SIZE_PATHS = {
		"/terrain.png",
		"/gui/items.png",
		"/default.png"
	};

	/** Side length in pixels, one per path above. */
	private static final int[] FIXED_SIZE_PIXELS = {
		256,
		256,
		128
	};

	/**
	 * Where a later pack keeps what this version looks for in the root. The
	 * files are not missing from those packs, they moved -- and without this
	 * the clouds, the water, the shadow, the menu background and the font stay
	 * the game's own while the pack sits there carrying all five.
	 */
	private static final String[][] ALIASES = {
		{"/char.png",    "/mob/char.png"},
		{"/clouds.png",  "/environment/clouds.png"},
		{"/default.png", "/font/default.png"},
		{"/dirt.png",    "/gui/background.png"},
		{"/shadow.png",  "/misc/shadow.png"},
		{"/snow.png",    "/environment/snow.png"},
		{"/water.png",   "/misc/water.png"}
	};

	/**
	 * The PNG chunks that carry the picture. Everything else is dropped on the
	 * way through, because a broken colour profile is common in packs of this
	 * age and a strict reader refuses the whole file over one -- even with
	 * every scanline intact.
	 */
	private static final String[] KEPT_CHUNKS = {"IHDR", "PLTE", "tRNS", "IDAT", "IEND"};

	private static final byte[] PNG_MAGIC = {
		(byte) 0x89, 'P', 'N', 'G', '\r', '\n', (byte) 0x1A, '\n'
	};

	private static final int CHUNK_HEADER = 8;
	private static final int CHUNK_CRC = 4;
	private static final int IHDR_SIZE_AT = 16;

	private static Textures textures = () -> {
	};

	private static Path root;

	private static ZipFile zip;
	private static Path folder;
	private static String opened = "";

	private TexturePack() {
	}

	public static void setTextures(Textures textures) {
		TexturePack.textures = textures;
	}

	/** The instance root, the directory that also holds {@code saves}. */
	public static void setRoot(Path root) {
		TexturePack.root = root;
	}

	/** True while a pack is answering for at least some of the game's art. */
	public static boolean inUse() {
		return zip != null || folder != null;
	}

	/**
	 * Drops whatever is open and takes the configured pack again, from disk.
	 *
	 * <p>Always from disk, even when the name has not changed: editing a pack
	 * and looking at the result is the loop this is for, and a name is a poor
	 * way to ask whether a file changed.
	 */
	public static void reopen() {
		boolean was = inUse();
		close();

		String wanted = Settings.TEXTUREPACK_NAME.text();
		if (!wanted.isEmpty() && root != null) {
			take(wanted);
		} else if (was) {
			Feinschliff.log().info("texture pack off, back to the game's own art");
		}

		if (was || inUse()) {
			textures.reloadAll();
		}
	}

	/**
	 * What the pack holds for that path, or {@code null} to let the game
	 * answer.
	 *
	 * <p>The bytes come back whole rather than as a handle into the archive:
	 * the game does not close what it is handed, and cleaning a PNG needs all
	 * of it anyway. Nothing the pack gives out outlives the pack.
	 */
	public static InputStream open(String path) {
		if (!inUse() || path == null || path.isEmpty() || path.contains("..")) {
			return null;
		}

		try {
			byte[] bytes = read(path);
			if (bytes == null) {
				String alias = aliasOf(path);
				if (alias != null) {
					bytes = read(alias);
				}
			}

			if (bytes == null) {
				return null;
			}

			if (isPng(bytes)) {
				if (tooLarge(bytes)) {
					Feinschliff.log().debug("texture pack: " + path
						+ " is larger than this version can upload, using the game's own");
					return null;
				}
				bytes = clean(bytes);

				if (TERRAIN.equals(path)) {
					bytes = TileGrid.regrid(bytes, Settings.TEXTUREPACK_ERA.text());
					bytes = Foliage.tint(bytes, read("/misc/grasscolor.png"),
						read("/misc/foliagecolor.png"),
						Settings.TEXTUREPACK_TINT_BELOW.value());
				}
			}

			Feinschliff.log().debug("texture pack: serving " + path);
			return new ByteArrayInputStream(bytes);
		} catch (Throwable t) {
			Feinschliff.log().debug("texture pack: " + path + " unreadable, using the game's own");
			return null;
		}
	}

	private static void take(String wanted) {
		if (wanted.contains("/") || wanted.contains("\\") || wanted.contains("..")) {
			Feinschliff.log().warn("texture pack name '" + wanted + "' is a path, not a name;"
				+ " the game keeps its own art");
			return;
		}

		Path base = root.resolve(FOLDER);
		Path asFolder = base.resolve(wanted);
		Path asZip = base.resolve(wanted + ".zip");

		try {
			if (Files.isDirectory(asFolder)) {
				folder = asFolder;
			} else if (Files.isRegularFile(asZip)) {
				zip = new ZipFile(asZip.toFile());
			} else {
				Feinschliff.log().warn("no texture pack called '" + wanted + "' in "
					+ base.toAbsolutePath() + "; the game keeps its own art");
				return;
			}
		} catch (Throwable t) {
			Feinschliff.log().warn("texture pack '" + wanted + "' could not be opened: "
				+ t + "; the game keeps its own art");
			close();
			return;
		}

		opened = wanted;
		if (!accepted(wanted)) {
			close();
		}
	}

	/**
	 * Whether this version can show the pack at all, answered once rather than
	 * once per file. A pack with larger tiles is not wrong, it is early.
	 */
	private static boolean accepted(String wanted) {
		byte[] terrain;
		try {
			terrain = read(TERRAIN);
		} catch (Throwable t) {
			terrain = null;
		}

		if (terrain == null || !isPng(terrain)) {
			Feinschliff.log().warn("'" + wanted + "' has no terrain.png and is not a texture"
				+ " pack; the game keeps its own art");
			return false;
		}

		int width = intAt(terrain, IHDR_SIZE_AT);
		int height = intAt(terrain, IHDR_SIZE_AT + 4);

		if (width != height || width % TILES != 0) {
			Feinschliff.log().warn("'" + wanted + "' has a " + width + "x" + height
				+ " terrain.png, which is not a square grid of sixteen tiles;"
				+ " the game keeps its own art");
			return false;
		}

		int tile = width / TILES;
		if (tile != TILE_SIZE) {
			Feinschliff.log().warn("'" + wanted + "' draws its blocks at " + tile + " pixels,"
				+ " and this version can only show " + TILE_SIZE + "; the game keeps its own art");
			return false;
		}

		for (int i = 0; i < FIXED_SIZE_PATHS.length; i++) {
			String path = FIXED_SIZE_PATHS[i];
			int expected = FIXED_SIZE_PIXELS[i];
			if (!sized(path, expected)) {
				Feinschliff.log().warn("'" + wanted + "' draws " + path + " at a size this"
					+ " version cannot read; it wants " + expected + " square."
					+ " The game keeps its own art");
				return false;
			}
		}

		Feinschliff.log().info("texture pack: " + wanted + (zip != null ? " (zip)" : " (folder)"));
		return true;
	}

	/**
	 * Whether the pack leaves that file at the size the game walks it at. A
	 * file the pack does not carry is fine -- the game keeps its own, which is
	 * the right size by definition.
	 */
	private static boolean sized(String path, int expected) {
		byte[] png;
		try {
			png = read(path);
			if (png == null) {
				String alias = aliasOf(path);
				png = alias == null ? null : read(alias);
			}
		} catch (Throwable t) {
			return false;
		}

		if (png == null) {
			return true;
		}

		return isPng(png)
			&& intAt(png, IHDR_SIZE_AT) == expected
			&& intAt(png, IHDR_SIZE_AT + 4) == expected;
	}

	private static void close() {
		if (zip != null) {
			try {
				zip.close();
			} catch (IOException e) {
				Feinschliff.log().debug("texture pack '" + opened + "' did not close cleanly");
			}
			zip = null;
		}
		folder = null;
		opened = "";
	}

	/** The file's bytes, or {@code null} when the pack does not carry it. */
	private static byte[] read(String path) throws IOException {
		String inside = path.startsWith("/") ? path.substring(1) : path;

		if (folder != null) {
			Path file = folder.resolve(inside);
			return Files.isRegularFile(file) ? Files.readAllBytes(file) : null;
		}

		if (zip == null) {
			return null;
		}

		ZipEntry entry = zip.getEntry(inside);
		if (entry == null || entry.isDirectory()) {
			return null;
		}

		InputStream in = zip.getInputStream(entry);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
			return out.toByteArray();
		} finally {
			in.close();
		}
	}

	private static String aliasOf(String path) {
		for (int i = 0; i < ALIASES.length; i++) {
			if (ALIASES[i][0].equals(path)) {
				return ALIASES[i][1];
			}
		}
		return null;
	}

	private static boolean isPng(byte[] bytes) {
		if (bytes.length < IHDR_SIZE_AT + 8) {
			return false;
		}
		for (int i = 0; i < PNG_MAGIC.length; i++) {
			if (bytes[i] != PNG_MAGIC[i]) {
				return false;
			}
		}
		return true;
	}

	private static boolean tooLarge(byte[] png) {
		long pixels = (long) intAt(png, IHDR_SIZE_AT) * intAt(png, IHDR_SIZE_AT + 4);
		return pixels * 4L > MAX_BYTES;
	}

	/**
	 * The same picture with every chunk the decoder does not need taken out.
	 * The scanlines are copied across untouched, so this changes no pixel.
	 */
	private static byte[] clean(byte[] png) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(png.length);
		out.write(png, 0, PNG_MAGIC.length);

		int at = PNG_MAGIC.length;
		while (at + CHUNK_HEADER <= png.length) {
			int length = intAt(png, at);
			int whole = CHUNK_HEADER + length + CHUNK_CRC;
			if (length < 0 || at + whole > png.length) {
				return png;
			}

			String type = new String(png, at + 4, 4, java.nio.charset.StandardCharsets.US_ASCII);
			if (kept(type)) {
				out.write(png, at, whole);
			}

			at += whole;
			if ("IEND".equals(type)) {
				return out.toByteArray();
			}
		}

		return png;
	}

	private static boolean kept(String type) {
		for (int i = 0; i < KEPT_CHUNKS.length; i++) {
			if (KEPT_CHUNKS[i].equals(type)) {
				return true;
			}
		}
		return false;
	}

	private static int intAt(byte[] bytes, int at) {
		return ((bytes[at] & 0xFF) << 24)
			| ((bytes[at + 1] & 0xFF) << 16)
			| ((bytes[at + 2] & 0xFF) << 8)
			| (bytes[at + 3] & 0xFF);
	}
}
