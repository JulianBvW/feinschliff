package io.github.julianbvw.feinschliff.core.vehicle;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * How a boat answers the tiller.
 *
 * <p>A boat is a first-order system with exactly two degrees of freedom, and
 * the drag is both of them at once: it decides how long the boat takes to reach
 * speed, how long it takes to stop, and how wide it turns, because all three
 * are the same number seen from different sides. Vanilla keeps 99% of the speed
 * every tick, which is why an alpha boat needs eleven seconds to get going and
 * five to become harmless again.
 *
 * <p>The push is not a setting of its own. A boat settles where the push and
 * the drag cancel, at {@code push / (1 - drag)}, so once the top speed and the
 * drag are known the push follows:
 *
 * <pre>push = topSpeed * (1 - drag)</pre>
 *
 * <p>Working it out this way rather than asking for it means a boat always
 * reaches the speed it was promised. Asking would allow a boat that crawls to
 * a third of its own ceiling, and there is nothing that combination can do that
 * a lower ceiling cannot do better.
 *
 * <p>The steering is untouched: the boat goes where its rider looks, which is
 * how this version has always done it, and turning is direct simply because the
 * old direction stops taking five seconds to fade.
 */
public final class BoatHandling {

	/**
	 * What one tick of a rider pushing against their boat is worth. It is the
	 * scale {@code MobEntity.moveRelative} uses in mid-air, and a rider in a
	 * boat is in mid-air as far as that method is concerned.
	 */
	private static final double RIDER_STEP = 0.02;

	private static final int TICKS_PER_SECOND = 20;

	private BoatHandling() {
	}

	public static boolean enabled() {
		return Settings.BOAT_HANDLING.on();
	}

	/**
	 * What is left of the speed after a tick.
	 *
	 * @param vanilla the value the game would use, returned untouched while the
	 *                feature is off, so nothing about a boat changes by a bit
	 */
	public static double drag(double vanilla) {
		return enabled() ? Settings.BOAT_DRAG.value() : vanilla;
	}

	/** The ceiling the game clamps each horizontal axis to, in blocks per tick. */
	public static double maxSpeed(double vanilla) {
		return enabled() ? perTick() : vanilla;
	}

	/**
	 * How much of the rider's own push reaches the boat.
	 *
	 * <p>This is where the top speed is actually made: the clamp above is a
	 * ceiling the boat approaches and never passes, not the speed it reaches.
	 */
	public static double riderFactor(double vanilla) {
		if (!enabled()) {
			return vanilla;
		}
		return perTick() * (1.0 - Settings.BOAT_DRAG.value()) / RIDER_STEP;
	}

	/** True while a boat is not to turn the head of whoever sits in it. */
	public static boolean freeView() {
		return Settings.BOAT_FREE_VIEW.on();
	}

	private static double perTick() {
		return Settings.BOAT_TOP_SPEED.value() / TICKS_PER_SECOND;
	}
}
