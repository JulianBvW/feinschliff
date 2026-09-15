package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.mob.MobEntity;

import io.github.julianbvw.feinschliff.core.movement.Fly;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;
import io.github.julianbvw.feinschliff.mc.alpha.movement.FlyPhysics;

@Mixin(MobEntity.class)
public class MobEntityMixin {

	@Shadow
	protected boolean jumping;

	/**
	 * The player's whole movement, and every other mob's with it -- this is the
	 * one call that turns input into motion.
	 *
	 * <p>The condition wraps the call rather than an injection sitting inside
	 * the method, because a cancellable injection would build a CallbackInfo for
	 * every mob in every tick before the first check even runs. Like this the
	 * cost of a switched-off feature is one static field read.
	 */
	@WrapWithCondition(
		method = "mobTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;moveRelative(FF)V"))
	private boolean feinschliff$flyInsteadOfWalking(MobEntity self, float sideways, float forwards) {
		if (!Fly.active() && !FlyPhysics.flying()) {
			return true;
		}
		if (self != FeinschliffClient.minecraft().player) {
			return true;
		}

		// Whatever the flight has moved, vanilla must not move a second time.
		return !FlyPhysics.tookOver(self, sideways, forwards, this.jumping);
	}
}
