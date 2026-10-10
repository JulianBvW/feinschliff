package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.InputStream;

import net.minecraft.client.render.texture.TextureManager;

import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

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
}
