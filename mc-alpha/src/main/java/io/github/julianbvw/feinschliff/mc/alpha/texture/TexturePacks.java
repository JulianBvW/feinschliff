package io.github.julianbvw.feinschliff.mc.alpha.texture;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.MemoryTracker;

import io.github.julianbvw.feinschliff.core.texture.Resolution;
import io.github.julianbvw.feinschliff.core.texture.TexturePack;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/**
 * Stands between the game asking its jar for a file and the core answering
 * from a pack.
 *
 * <p>Thin on purpose: every question of which file, from where and in what
 * shape belongs to the core, which can be asked without a game running.
 */
public final class TexturePacks {

	/** The sheet the game's animated tiles live on, by its own numbering. */
	private static final int ITEM_ATLAS = 1;

	/** What the game writes its animated tiles at, whatever the pack draws. */
	private static final int WRITTEN_AT = 16;

	/** Kept between frames: the animations are uploaded every tick. */
	private static ByteBuffer magnified;

	private TexturePacks() {
	}

	/** How many pixels a tile has on the sheet that animation writes to. */
	public static int tileSize(int atlas) {
		return atlas == ITEM_ATLAS ? TexturePack.itemTile() : TexturePack.terrainTile();
	}

	/**
	 * The game's own sixteen-pixel animation, grown to fill a tile of the
	 * size the pack draws at.
	 *
	 * <p>Fire, lava, water and the compass are worked out rather than read
	 * from a file -- the colours are literals in those classes -- so the pack
	 * has no say in what they look like, only in how large the square is that
	 * they have to cover.
	 */
	public static ByteBuffer magnified(byte[] pixels, int factor) {
		byte[] grown = Resolution.magnify(pixels, WRITTEN_AT, factor);
		if (magnified == null || magnified.capacity() < grown.length) {
			magnified = MemoryTracker.createByteBuffer(grown.length);
		}

		((Buffer)magnified).clear();
		magnified.put(grown);
		((Buffer)magnified).position(0).limit(grown.length);
		return magnified;
	}

	/** What the pack holds for that path, or {@code null} to let the jar answer. */
	public static InputStream open(String path) {
		return TexturePack.open(path);
	}

	/**
	 * Reads every loaded texture again.
	 *
	 * <p>The texture manager does not exist for the first stretch of startup,
	 * and the core opens its pack before it does -- there is simply nothing to
	 * refresh that early, because nothing has been loaded yet.
	 */
	public static void reloadAll() {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft != null && minecraft.textureManager != null) {
			minecraft.textureManager.reload();
		}
	}
}
