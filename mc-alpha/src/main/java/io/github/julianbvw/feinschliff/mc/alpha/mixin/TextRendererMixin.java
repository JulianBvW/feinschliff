package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.texture.TextureManager;

import io.github.julianbvw.feinschliff.core.texture.Resolution;
import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

	/** The size of font this version was written for. */
	private static final int VANILLA = 128;

	@Shadow
	private int[] characterWidths;

	/**
	 * The font is read here and nowhere else, and it is read once: the widths
	 * of all two hundred and fifty-six characters are measured off the image
	 * and baked into display lists in this constructor. That is why a pack
	 * swapped while the game runs keeps the old font until the next start --
	 * there is no second reading to catch.
	 */
	@WrapOperation(
		method = "<init>",
		at = @At(value = "INVOKE",
			target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"),
		require = 1)
	private InputStream feinschliff$fontFromThePack(Class<?> owner, String path,
			Operation<InputStream> original) {
		InputStream fromPack = TexturePacks.open(path);
		return fromPack != null ? fromPack : original.call(owner, path);
	}

	/**
	 * How wide each character is, measured again for a font that is not the
	 * size this version expects.
	 *
	 * <p>The game walks the picture in cells of eight across an array of the
	 * picture's own width, which is exactly right at 128 and reads only the
	 * top left corner of anything larger -- every character would come out
	 * with the width of whatever happens to sit in that corner. The walk is
	 * repeated here with the cell the font actually has, and the answer put
	 * back on the eight-pixel scale the text is laid out in.
	 *
	 * <p>At 128 nothing is touched at all: the game's own numbers are already
	 * the right ones, and leaving them alone keeps a pack of this version's
	 * size bit for bit as it was.
	 *
	 * <p>⚠ This has to happen before the display lists are built, which is
	 * why it hangs off the upload rather than the end of the constructor: the
	 * step from one character to the next is a {@code glTranslatef} compiled
	 * into each list, so a width corrected afterwards would fix the centring
	 * of text and nothing else, and every line would still be drawn one pixel
	 * at a time on top of itself.
	 *
	 * <p>The lists themselves need no care. They look resolution-bound, with
	 * their {@code / 128.0F}, but a cell of eight over a width of 128 is one
	 * sixteenth however many pixels that turns out to be.
	 */
	@Inject(
		method = "<init>",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/texture/TextureManager;"
				+ "load(Ljava/awt/image/BufferedImage;)I"))
	private void feinschliff$measureTheFont(GameOptions options, String fontPath,
			TextureManager textureManager, CallbackInfo ci, @Local BufferedImage font) {
		int size = font.getWidth();
		if (size == VANILLA || size != font.getHeight() || size % 16 != 0 || size < VANILLA) {
			return;
		}

		int[] pixels = new int[size * size];
		font.getRGB(0, 0, size, size, pixels, 0, size);
		int[] widths = Resolution.fontWidths(pixels, size);
		System.arraycopy(widths, 0, this.characterWidths, 0, widths.length);
	}
}
