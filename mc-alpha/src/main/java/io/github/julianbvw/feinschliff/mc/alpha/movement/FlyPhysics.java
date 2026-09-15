package io.github.julianbvw.feinschliff.mc.alpha.movement;

import net.minecraft.entity.mob.MobEntity;

import io.github.julianbvw.feinschliff.core.movement.Fly;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.EntityAccessor;

/**
 * Moves the player the way the flight says to, instead of the way walking would.
 *
 * <p>Collision is untouched: the movement still goes through
 * {@code Entity.move}, so walls, ceilings and floors all still stop you. What is
 * skipped is everything {@code moveRelative} does around that call -- gravity,
 * drag, and the water, lava and ladder cases.
 */
public final class FlyPhysics {

	/** Whether the last movement was ours, and a hand-back is therefore still owed. */
	private static boolean flying;

	private FlyPhysics() {
	}

	public static boolean flying() {
		return flying;
	}

	/** Drops a flight with no entity left to hand back to, for a world that is gone. */
	public static void forget() {
		flying = false;
	}

	/**
	 * Moves the player for this tick and says whether it did. Where it did not,
	 * it has handed them back to their own physics instead.
	 *
	 * <p>Two things end a flight besides switching it off. A dead player would
	 * hover, because {@code mobTick} keeps asking for movement while the death
	 * screen is up. And a passenger is placed by whatever it rides immediately
	 * afterwards, so flying there would be a full collision sweep worked out and
	 * then thrown away.
	 */
	public static boolean tookOver(MobEntity self, float sideways, float forwards, boolean jumping) {
		if (self.health <= 0) {
			Fly.stop();
		}

		if (!Fly.active() || self.vehicle != null) {
			handBack(self);
			return false;
		}

		fly(self, sideways, forwards, jumping);
		return true;
	}

	/** Back to gravity, and without the speed the flight was carrying. */
	private static void handBack(MobEntity self) {
		if (!flying) {
			return;
		}
		flying = false;
		self.velocityX = 0.0;
		self.velocityY = 0.0;
		self.velocityZ = 0.0;
	}

	private static void fly(MobEntity self, float sideways, float forwards, boolean jumping) {
		flying = true;
		Fly.step(self.yaw, sideways, forwards, jumping, self.isSneaking(), self.y);

		// Assignment, not addition: mobTick may have put a jump or a swim nudge
		// into the velocity a moment ago, and the flight owns the speed outright.
		// Anything that changes this to accumulate brings gravity back in.
		self.velocityX = Fly.stepX();
		self.velocityY = Fly.stepY();
		self.velocityZ = Fly.stepZ();

		// Before the movement, not after: move() hands out the fall damage
		// itself, the moment it finds ground under a descent. Clearing it
		// afterwards would let the first tick of a flight begun in mid-fall
		// arrive with the whole drop still counted against it.
		((EntityAccessor)(Object)self).feinschliff$setFallDistance(0.0F);

		// Airborne as far as move() is concerned. That switches off three things
		// meant for walking: the sneak guard that sticks you to ledges, the
		// automatic step up, and the fall damage. move() works the real answer
		// out again before it returns.
		self.onGround = false;

		// The distance walked drives both the footsteps move() plays and the
		// view bobbing the renderer applies, and neither belongs to someone in
		// mid-air. Putting it back afterwards holds both still, at the price of
		// at most one step sound as the flight begins.
		float walked = self.walkDistance;
		self.move(self.velocityX, self.velocityY, self.velocityZ);
		self.walkDistance = walked;

		animate(self);
	}

	/**
	 * The tail of {@code moveRelative}, which is the only place these are
	 * written and which every model reads. Skipping it freezes the legs
	 * mid-stride.
	 */
	private static void animate(MobEntity self) {
		self.lastWalkAnimationSpeed = self.walkAnimationSpeed;

		double dx = self.x - self.lastX;
		double dz = self.z - self.lastZ;
		float speed = (float)Math.sqrt(dx * dx + dz * dz) * 4.0F;
		if (speed > 1.0F) {
			speed = 1.0F;
		}

		self.walkAnimationSpeed = self.walkAnimationSpeed + (speed - self.walkAnimationSpeed) * 0.4F;
		self.walkAnimationProgress = self.walkAnimationProgress + self.walkAnimationSpeed;
	}
}
