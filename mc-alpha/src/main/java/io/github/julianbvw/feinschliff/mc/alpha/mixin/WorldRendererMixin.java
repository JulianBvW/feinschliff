package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.HitResult;

import io.github.julianbvw.feinschliff.core.camera.Freecam;
import io.github.julianbvw.feinschliff.core.config.Settings;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	/**
	 * Both of these mark what the crosshair is on, and the crosshair stays with
	 * the player. A box drawn around a block the camera happens to be nowhere
	 * near reads as a bug rather than as information.
	 */
	@Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void feinschliff$hideBlockOutlineInFreecam(PlayerEntity camera, HitResult hit, int i,
			ItemStack itemInHand, float tickDelta, CallbackInfo ci) {
		if (Freecam.active()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderMiningProgress", at = @At("HEAD"), cancellable = true)
	private void feinschliff$hideMiningProgressInFreecam(PlayerEntity camera, HitResult hit, int i,
			ItemStack itemInHand, float tickDelta, CallbackInfo ci) {
		if (Freecam.active()) {
			ci.cancel();
		}
	}

	/**
	 * The test that hides the player's own body from their own eyes. A free
	 * camera has left those eyes behind, so the body may as well be drawn.
	 */
	@ModifyExpressionValue(
		method = "renderEntities",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;perspective:Z"))
	private boolean feinschliff$showPlayerInFreecam(boolean thirdPerson) {
		return thirdPerson || Freecam.active() && Settings.CAMERA_FREECAM_SHOW_PLAYER.on();
	}
}
