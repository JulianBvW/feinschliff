package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

import io.github.julianbvw.feinschliff.mc.alpha.mob.Pigs;

/**
 * The two things a pig needs from the class every animal shares: the saddle
 * coming back when it dies, and a longer stride while somebody is steering it.
 */
@Mixin(MobEntity.class)
public class MobEntityPigMixin {

	/**
	 * TAIL, so the saddle lands with the pork rather than instead of it. The
	 * tag it reads is vanilla's own, written by every saddled pig since this
	 * version shipped -- a saddle that goes in still comes back out without
	 * the mod, it just stays on the pig.
	 */
	@Inject(method = "die", at = @At("TAIL"))
	private void feinschliff$dropTheSaddle(Entity killer, CallbackInfo ci) {
		MobEntity self = (MobEntity)(Object)this;
		if (Pigs.dropsTheSaddle(self)) {
			self.dropItem(Pigs.saddleId(), 1);
		}
	}

	/**
	 * The push a step gives, and with it the speed the animal settles at. It is
	 * a factor rather than a figure on purpose: the surface keeps its say, so a
	 * pig on ice still slides like a pig on ice.
	 *
	 * <p>Every mob passes through here every tick. All but the one being ridden
	 * get their own number back unchanged.
	 */
	@ModifyArg(
		method = "moveRelative",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;updateVelocity(FFF)V"),
		index = 2)
	private float feinschliff$pigStride(float push) {
		return push * Pigs.push(this);
	}
}
