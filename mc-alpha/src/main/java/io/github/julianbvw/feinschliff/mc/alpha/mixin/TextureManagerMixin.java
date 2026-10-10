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
import java.nio.ByteBuffer;

import net.minecraft.client.render.platform.MemoryTracker;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.client.render.texture.TextureManager;

import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

	@Shadow
	private ByteBuffer imageBuffer;

	/**
	 * The one door every texture in the game comes through. Both methods ask
	 * the jar for a file three times over -- once plainly, once for the
	 * stretched sprite sheets and once for the blurred ones -- and all six
	 * calls want the same answer, so they are wrapped together.
	 *
	 * <p>The path arrives here already stripped of the {@code ##} and
	 * {@code %%} markers the caller uses to pick its branch, so what is asked
	 * for is the plain file name and nothing has to be unpicked.
	 *
	 * <p>Null from the pack is the usual answer and means the jar should go
	 * ahead, which is what keeps an incomplete pack playable.
	 */
	@WrapOperation(
		method = {"load(Ljava/lang/String;)I", "reload()V"},
		at = @At(value = "INVOKE",
			target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"),
		require = 6)
	private InputStream feinschliff$fromThePack(Class<?> owner, String path,
			Operation<InputStream> original) {
		InputStream fromPack = TexturePacks.open(path);
		return fromPack != null ? fromPack : original.call(owner, path);
	}

	/**
	 * The buffer every texture is uploaded through holds one megabyte, which
	 * is four bytes for each pixel of a 512 square and not one more. Anything
	 * larger overflows it before a single pixel reaches the card, so it is
	 * grown to whatever is actually being handed over.
	 *
	 * <p>It only ever grows, and only when something does not fit, so a game
	 * without a pack keeps the buffer it has always had.
	 */
	@Inject(method = "load(Ljava/awt/image/BufferedImage;I)V", at = @At("HEAD"))
	private void feinschliff$roomForTheWholeImage(BufferedImage image, int id, CallbackInfo ci) {
		int needed = image.getWidth() * image.getHeight() * 4;
		if (needed > this.imageBuffer.capacity()) {
			this.imageBuffer = MemoryTracker.createByteBuffer(needed);
		}
	}

	/**
	 * Fire, lava, water and the compass are worked out every tick and written
	 * straight over their tile, sixteen pixels square at sixteen-pixel
	 * offsets. On a pack drawn larger that lands as a small blot in the corner
	 * of each of those tiles, so both the place and the size are put back in
	 * proportion and the sixteen squares are grown to match.
	 *
	 * <p>Which sheet decides the size is the sprite's own business: the
	 * compass sits on the item sheet and the rest in the terrain, and a pack
	 * may well draw the two at different sizes.
	 *
	 * <p>The pixels are taken from the sprite rather than from the buffer
	 * being passed: the game refills that buffer for each call, and the water
	 * and lava sides go round twice in each direction to cover four tiles.
	 */
	@WrapOperation(
		method = "tick()V",
		at = @At(value = "INVOKE",
			target = "Lorg/lwjgl/opengl/GL11;glTexSubImage2D(IIIIIIIILjava/nio/ByteBuffer;)V",
			ordinal = 0),
		require = 1)
	private void feinschliff$intoTheWholeTile(int target, int level, int x, int y,
			int width, int height, int format, int type, ByteBuffer pixels,
			Operation<Void> original, @Local DynamicTexture sprite) {
		int factor = TexturePacks.tileSize(sprite.atlas) / width;
		if (factor <= 1) {
			original.call(target, level, x, y, width, height, format, type, pixels);
			return;
		}

		original.call(target, level, x * factor, y * factor, width * factor, height * factor,
			format, type, TexturePacks.magnified(sprite.pixels, factor));
	}
}
