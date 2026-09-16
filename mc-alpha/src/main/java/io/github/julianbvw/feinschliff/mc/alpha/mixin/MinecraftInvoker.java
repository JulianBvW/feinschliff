package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Minecraft;

/**
 * Reaches the one method the borderless window needs that the game keeps to
 * itself: it sets the size the game believes it has and lays out an open
 * screen for it.
 */
@Mixin(Minecraft.class)
public interface MinecraftInvoker {

	@Invoker("resize")
	void feinschliff$resize(int width, int height);
}
