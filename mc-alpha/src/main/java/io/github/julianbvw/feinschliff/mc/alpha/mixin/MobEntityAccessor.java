package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.mob.MobEntity;

/**
 * The reins: what a walking animal is told each tick.
 *
 * <p>All three are protected fields of {@code MobEntity}, which is what an
 * accessor is for -- the pig riding lives outside that package.
 */
@Mixin(MobEntity.class)
public interface MobEntityAccessor {

	@Accessor("forwardSpeed")
	void feinschliff$setForwardSpeed(float forwardSpeed);

	@Accessor("sidewaysSpeed")
	void feinschliff$setSidewaysSpeed(float sidewaysSpeed);

	@Accessor("jumping")
	void feinschliff$setJumping(boolean jumping);
}
