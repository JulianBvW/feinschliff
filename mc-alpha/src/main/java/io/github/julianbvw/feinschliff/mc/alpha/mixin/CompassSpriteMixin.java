package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

import net.minecraft.client.render.texture.CompassSprite;

import io.github.julianbvw.feinschliff.core.texture.Resolution;
import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

@Mixin(CompassSprite.class)
public class CompassSpriteMixin {

	/** The size of item sheet this version cuts the needle out of. */
	private static final int VANILLA = 256;

	/**
	 * The needle is drawn from a copy of the compass square that this cuts out
	 * of the item sheet when it is built, and then writes back over the sheet
	 * every tick, turned. Without this the copy would be the game's own needle
	 * sitting in the middle of a pack's items, which is the one tile that would
	 * look wrong.
	 *
	 * <p>The read is wrapped rather than the lookup in front of it: this is the
	 * only place in the client that asks for a file by URL instead of by
	 * stream, and an image is what the caller wants either way, so there is
	 * nothing to gain from inventing a URL that points inside a zip.
	 *
	 * <p>The sheet is handed over at the size this version reads: the cut is
	 * a blind sixteen by sixteen at sixteen-pixel offsets, so a pack drawn
	 * larger would have its needle taken from a corner of the wrong tile. The
	 * needle is therefore worked out at the size the game expects and grown
	 * on the way back onto the sheet, like the rest of the animated tiles.
	 *
	 * <p>Cut once, in the constructor, like the font -- a pack swapped while
	 * the game runs leaves the old needle until the next start.
	 */
	@WrapOperation(
		method = "<init>",
		at = @At(value = "INVOKE",
			target = "Ljavax/imageio/ImageIO;read(Ljava/net/URL;)Ljava/awt/image/BufferedImage;"),
		require = 1)
	private BufferedImage feinschliff$compassFromThePack(URL url, Operation<BufferedImage> original) {
		InputStream fromPack = TexturePacks.open("/gui/items.png");
		if (fromPack != null) {
			try {
				BufferedImage image = ImageIO.read(fromPack);
				if (image != null) {
					return Resolution.atSize(image, VANILLA);
				}
			} catch (Throwable t) {
				// Fall through to the jar; the sheet itself is already drawn
				// from the pack, so only the needle is at stake here.
			}
		}
		return original.call(url);
	}
}
