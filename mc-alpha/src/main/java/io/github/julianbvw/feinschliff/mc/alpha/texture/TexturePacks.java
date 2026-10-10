package io.github.julianbvw.feinschliff.mc.alpha.texture;

import java.io.InputStream;

import net.minecraft.client.Minecraft;

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

	private TexturePacks() {
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
