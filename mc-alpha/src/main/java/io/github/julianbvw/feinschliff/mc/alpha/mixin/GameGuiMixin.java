package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;

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
	 * TAIL, so the overlay sits on top of everything else the hud draws. Blend
	 * and alpha test are in the same state here as they are where the vanilla
	 * debug block runs, so the text needs no setup of its own.
	 */
	@Inject(method = "render", at = @At("TAIL"))
	private void feinschliff$renderDebugOverlay(float partialTick, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
		DebugOverlayRenderer.render(this.minecraft);
	}
}
