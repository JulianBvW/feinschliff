package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.Entity;

/**
 * Reaches the one field the flight needs that the game keeps to itself.
 *
 * <p>A shadow would not do: it only finds fields declared in the class a mixin
 * targets, and the flight's mixin targets {@code MobEntity} while this field
 * belongs to {@code Entity} above it.
 */
@Mixin(Entity.class)
public interface EntityAccessor {

	@Accessor("fallDistance")
	void feinschliff$setFallDistance(float fallDistance);
}
