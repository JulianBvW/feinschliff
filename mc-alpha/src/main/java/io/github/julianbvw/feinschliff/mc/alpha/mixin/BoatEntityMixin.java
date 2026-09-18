package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.vehicle.BoatEntity;

import io.github.julianbvw.feinschliff.core.vehicle.BoatHandling;

/**
 * The whole of the boat handling, which is three numbers inside one method.
 *
 * <p>Everything else vanilla's {@code tick()} does is still its own work:
 * buoyancy, the movement, the spray, the crash that breaks the boat and the
 * planks it leaves behind. {@code takeDamage} is not touched at all, so a boat
 * is no less fragile than it was and drops what it always dropped, however many
 * cacti take it apart at once.
 *
 * <p>Each handler is handed the constant it replaces and gives it straight back
 * while the feature is off, which leaves a switched-off boat vanilla down to
 * the bit.
 */
@Mixin(BoatEntity.class)
public class BoatEntityMixin {

	/**
	 * The drag, on both horizontal axes. The third of the three that the
	 * vertical axis uses is a different number and stays as it is: it is the
	 * boat bobbing, not the boat sailing.
	 *
	 * <p>Written as the double the compiler made of the float in the source.
	 * The constant in the class file is the widened {@code 0.99F}, and a float
	 * of that value matches nothing.
	 */
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = (double)0.99F))
	private double feinschliff$drag(double vanilla) {
		return BoatHandling.drag(vanilla);
	}

	/** The ceiling, applied to each horizontal axis on its own. */
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.4))
	private double feinschliff$topSpeed(double vanilla) {
		return BoatHandling.maxSpeed(vanilla);
	}

	/**
	 * The share of the rider's own movement that becomes the boat's, on both
	 * axes. This is the throttle, and with it the boat still goes exactly where
	 * its rider is looking.
	 */
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.2))
	private double feinschliff$push(double vanilla) {
		return BoatHandling.riderFactor(vanilla);
	}
}
