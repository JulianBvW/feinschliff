package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;

import io.github.julianbvw.feinschliff.core.camera.Freecam;
import io.github.julianbvw.feinschliff.mc.alpha.camera.FreecamView;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	private Minecraft minecraft;

	/**
	 * The only place the view matrix is aimed, and the only one the free camera
	 * needs. Everything else in the render path stays on the player.
	 */
	@Inject(method = "transformCamera", at = @At("HEAD"), cancellable = true)
	private void feinschliff$freecamCamera(float partialTick, CallbackInfo ci) {
		if (!Freecam.active()) {
			return;
		}
		FreecamView.applyCamera(this.minecraft, partialTick);
		ci.cancel();
	}

	/**
	 * The held item and the water and fire overlays are drawn in front of the
	 * eye, so they would follow the camera around instead of staying with the
	 * body they belong to.
	 */
	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void feinschliff$hideHandInFreecam(float partialTick, int anaglyphRenderPass, CallbackInfo ci) {
		if (Freecam.active()) {
			ci.cancel();
		}
	}

	/**
	 * Mouse look, once per frame rather than once per tick. Taking it away from
	 * the player here is what keeps the body facing where it was left, and it
	 * keeps the camera as smooth as the frame rate allows.
	 */
	@WrapWithCondition(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/entity/mob/player/ClientPlayerEntity;updateLocalPlayerCamera(FF)V"))
	private boolean feinschliff$freecamLook(ClientPlayerEntity player, float deltaYaw, float deltaPitch) {
		if (!Freecam.active()) {
			return true;
		}
		Freecam.look(deltaYaw, deltaPitch);
		return false;
	}
}
