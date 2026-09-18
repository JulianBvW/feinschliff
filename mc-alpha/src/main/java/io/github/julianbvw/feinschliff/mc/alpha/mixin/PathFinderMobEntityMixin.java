package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.mob.PathFinderMobEntity;

import io.github.julianbvw.feinschliff.mc.alpha.mob.Pigs;

/**
 * Where an animal decides what to do next.
 *
 * <p>A ridden pig is told instead, and only that one: every other animal, and
 * the same pig the moment nobody is holding wheat at it, falls straight
 * through to its own wandering. That includes the check further down this
 * method that removes animals nobody is near, so nothing about how a pig comes
 * and goes changes.
 */
@Mixin(PathFinderMobEntity.class)
public class PathFinderMobEntityMixin {

	@Inject(method = "aiTick", at = @At("HEAD"), cancellable = true)
	private void feinschliff$followTheWheat(CallbackInfo ci) {
		if (Pigs.listens((PathFinderMobEntity)(Object)this)) {
			ci.cancel();
		}
	}
}
