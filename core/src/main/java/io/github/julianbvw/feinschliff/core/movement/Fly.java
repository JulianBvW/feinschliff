package io.github.julianbvw.feinschliff.core.movement;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.input.Hotkey;
import io.github.julianbvw.feinschliff.core.platform.Game;
import io.github.julianbvw.feinschliff.core.platform.GameHost;

/**
 * Flight for the player, in a version that has no creative mode to borrow it
 * from.
 *
 * <p>Unlike the free camera this moves the real player, so it loads and
 * generates terrain exactly the way walking there would -- only faster. That is
 * the point of it, not a side effect.
 *
 * <p>The three components of a tick's movement are read back one at a time
 * rather than returned in an object, because this runs twenty times a second
 * and there is no reason for it to allocate anything.
 */
public final class Fly {

	private static final double TICKS_PER_SECOND = 20.0;

	/**
	 * Below -64 {@code Entity.baseTick} hands the player to the void and removes
	 * it, which no amount of flying gets you back from. Collision guards that as
	 * long as the bedrock is whole, and this guards it when it is not.
	 */
	private static final double MIN_Y = -16.0;

	/** Above the clouds there is nothing left to look at. */
	private static final double MAX_Y = 160.0;

	private static final Hotkey TOGGLE = new Hotkey(Settings.HOTKEY_FLY);

	private static boolean active;

	private static double stepX;
	private static double stepY;
	private static double stepZ;

	private Fly() {
	}

	/** The feature is available: switched on and reachable by a key. */
	public static boolean enabled() {
		return Settings.MOVEMENT_FLY.on() && Settings.HOTKEY_FLY.isBound();
	}

	/** The player is flying right now. */
	public static boolean active() {
		return active;
	}

	public static void tick() {
		GameHost host = Game.host();
		if (host == null) {
			return;
		}

		if (!host.inWorld() || !enabled()) {
			stop();
			return;
		}

		// An open screen filters no hotkey, so without this an L written on a
		// sign would take off. See Hotkey.
		if (TOGGLE.pressed() && !host.screenOpen()) {
			if (active) {
				stop();
			} else {
				start();
			}
		}
	}

	public static void stop() {
		if (!active) {
			return;
		}
		active = false;
		Feinschliff.log().debug("fly off");
	}

	/**
	 * Works out the movement for this tick, to be read back one component at a
	 * time from {@link #stepX}, {@link #stepY} and {@link #stepZ}.
	 *
	 * <p>Direction comes from the sign of the two inputs, not from their size.
	 * Vanilla scales them down to 30% while sneaking and decays them by 0.98
	 * before handing them over, and neither has any business changing a flying
	 * speed -- descending would be slower than climbing. Letting go sets them to
	 * exactly zero, so stopping stays exact.
	 */
	public static void step(float yaw, float sideways, float forwards, boolean up, boolean down, double y) {
		double forward = Math.signum(forwards);
		double strafe = Math.signum(sideways);
		double vertical = (up ? 1.0 : 0.0) - (down ? 1.0 : 0.0);

		if (y <= MIN_Y && vertical < 0.0 || y >= MAX_Y && vertical > 0.0) {
			vertical = 0.0;
		}

		double yawRadians = Math.toRadians(yaw);
		double dx = -Math.sin(yawRadians) * forward + Math.cos(yawRadians) * strafe;
		double dy = vertical;
		double dz = Math.cos(yawRadians) * forward + Math.sin(yawRadians) * strafe;

		// Two directions at once must not be faster than one.
		double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length > 1.0) {
			dx /= length;
			dy /= length;
			dz /= length;
		}

		double step = Settings.MOVEMENT_FLY_SPEED.value() / TICKS_PER_SECOND;
		stepX = dx * step;
		stepY = dy * step;
		stepZ = dz * step;
	}

	public static double stepX() {
		return stepX;
	}

	public static double stepY() {
		return stepY;
	}

	public static double stepZ() {
		return stepZ;
	}

	private static void start() {
		active = true;
		Feinschliff.log().debug("fly on");
	}
}
