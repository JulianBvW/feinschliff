package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;

import io.github.julianbvw.feinschliff.core.camera.Freecam;
import io.github.julianbvw.feinschliff.core.hud.DebugOverlay;
import io.github.julianbvw.feinschliff.mc.alpha.hud.DebugOverlayRenderer;

@Mixin(GameGui.class)
public class GameGuiMixin {

	@Shadow
	private Minecraft minecraft;

	/**
	 * The one key test that guards the vanilla debug block. Answering it with
	 * false hands the whole block over to this mod: the game then falls into
	 * its else branch and draws nothing but the version line, which the overlay
	 * keeps and uses as its heading.
	 */
	@ModifyExpressionValue(
		method = "render",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
	private boolean feinschliff$hideVanillaDebugScreen(boolean pressed) {
		return pressed && !DebugOverlay.enabled();
	}

	/**
	 * The crosshair: the first thing drawn once the inverting blend mode is on.
	 * It aims from the player, so with the camera elsewhere it points at
	 * nothing the viewer can see, and every click it invites is blocked anyway.
	 */
	@WrapWithCondition(
		method = "render",
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V", ordinal = 0)),
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GameGui;drawTexture(IIIIII)V", ordinal = 0))
	private boolean feinschliff$hideCrosshairInFreecam(GameGui gui, int x, int y, int u, int v, int width, int height) {
		return !Freecam.active();
	}

	/**
	 * TAIL, so the overlay sits on top of everything else the hud draws. Blend
	 * and alpha test are in the same state here as they are where the vanilla
	 * debug block runs, so the text needs no setup of its own.
	 */
	@Inject(method = "render", at = @At("TAIL"))
	private void feinschliff$renderDebugOverlay(float partialTick, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
		DebugOverlayRenderer.render(this.minecraft);
	}
}
