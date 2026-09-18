package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;

import io.github.julianbvw.feinschliff.core.vehicle.BoatHandling;

/**
 * Keeps a boat from turning the head of whoever sits in it.
 *
 * <p>A passenger collects every turn its vehicle makes and gives itself half of
 * what it has collected back each tick, up to ten degrees. For a boat, whose
 * yaw chases the direction it happens to be drifting in, that is a view being
 * tugged at the whole way across a lake.
 *
 * <p>Halving nothing leaves the view where the mouse put it. Clearing what was
 * collected is the other half of the same job: without it the turns would pile
 * up unspent and arrive all at once the moment the feature is switched off.
 *
 * <p>Boats only. Everything else that can be ridden keeps the vanilla
 * behaviour, and so does a boat while {@code boat.freeView} is off.
 */
@Mixin(Entity.class)
public class EntityRideMixin {

	@Shadow
	public Entity vehicle;

	@Shadow
	private double ridingEntityYawDelta;

	@Shadow
	private double ridingEntityPitchDelta;

	@Inject(method = "rideTick", at = @At("HEAD"))
	private void feinschliff$forgetTheBoatsTurns(CallbackInfo ci) {
		if (feinschliff$freeOfTheBoat()) {
			this.ridingEntityYawDelta = 0.0;
			this.ridingEntityPitchDelta = 0.0;
		}
	}

	@ModifyConstant(method = "rideTick", constant = @Constant(doubleValue = 0.5))
	private double feinschliff$dontFollowTheBoat(double share) {
		return feinschliff$freeOfTheBoat() ? 0.0 : share;
	}

	private boolean feinschliff$freeOfTheBoat() {
		return this.vehicle instanceof BoatEntity && BoatHandling.freeView();
	}
}
