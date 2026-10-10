package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.InputStream;

import net.minecraft.client.render.TextRenderer;

import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

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
}
