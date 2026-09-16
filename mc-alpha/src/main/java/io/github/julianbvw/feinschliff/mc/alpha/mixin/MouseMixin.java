package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.lwjgl.opengl.Display;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Mouse;

import io.github.julianbvw.feinschliff.core.window.Borderless;

/**
 * Puts the pointer back in the middle of the screen when a screen opens.
 *
 * <p>Releasing it centres it on the awt component the game was built around,
 * which is the right answer for as long as that component is what the game
 * draws into. A borderless window is not: the component keeps the size it had,
 * so half of it lands somewhere near the bottom left corner of the screen.
 */
@Mixin(Mouse.class)
public class MouseMixin {

	@ModifyExpressionValue(
		method = "unlock",
		at = @At(value = "INVOKE", target = "Ljava/awt/Component;getWidth()I"))
	private int feinschliff$centreOnTheRealWidth(int width) {
		return Borderless.active() ? Display.getWidth() : width;
	}

	@ModifyExpressionValue(
		method = "unlock",
		at = @At(value = "INVOKE", target = "Ljava/awt/Component;getHeight()I"))
	private int feinschliff$centreOnTheRealHeight(int height) {
		return Borderless.active() ? Display.getHeight() : height;
	}
}
