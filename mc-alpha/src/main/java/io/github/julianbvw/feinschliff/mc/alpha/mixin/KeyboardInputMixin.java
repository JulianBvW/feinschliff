package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.mob.player.Input;
import net.minecraft.client.entity.mob.player.KeyboardInput;
import net.minecraft.entity.mob.player.PlayerEntity;

import io.github.julianbvw.feinschliff.core.camera.Freecam;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	/**
	 * Holds the player still while the camera is away, without touching the
	 * key state itself: the pressed keys keep being recorded, only the movement
	 * they would cause is dropped. Walking therefore resumes on its own the
	 * moment the camera comes back, even for a key that was held the whole time.
	 */
	@Inject(method = "tick", at = @At("TAIL"))
	private void feinschliff$freezePlayerWhileFlying(PlayerEntity player, CallbackInfo ci) {
		if (!Freecam.active()) {
			return;
		}
		this.movementForward = 0.0F;
		this.movementSideways = 0.0F;
		this.jumping = false;
		this.sneaking = false;
	}
}
