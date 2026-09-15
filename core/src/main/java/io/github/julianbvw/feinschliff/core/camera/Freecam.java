package io.github.julianbvw.feinschliff.core.camera;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.input.Hotkey;
import io.github.julianbvw.feinschliff.core.input.Keys;
import io.github.julianbvw.feinschliff.core.platform.Game;
import io.github.julianbvw.feinschliff.core.platform.GameHost;
import io.github.julianbvw.feinschliff.core.platform.GameHost.Move;

/**
 * A camera that flies on its own while the player stays where they are.
 *
 * <p>The player entity is never touched -- not its position, not its velocity,
 * not its facing. Everything that reads the world during rendering keeps being
 * fed the real player, and only the view matrix is moved. That is what keeps
 * the free camera off the world: the render chunk grid stays anchored, so no
 * block is ever read outside the chunks that are already loaded, and nothing
 * can be generated or written to disk.
 *
 * <p>Positions are kept for two ticks so the renderer can interpolate between
 * them; angles are not, because the look hook runs once per frame anyway.
 */
public final class Freecam {

	private static final double TICKS_PER_SECOND = 20.0;

	/** Matches the factor vanilla applies to the same mouse deltas. */
	private static final double LOOK_FACTOR = 0.15;

	/** One notch of the wheel. */
	private static final double SPEED_STEP = 1.2;

	private static final double MIN_SPEED = 0.5;
	private static final double MAX_SPEED = 100.0;

	/** Wheel deltas arrive in multiples of this. */
	private static final int WHEEL_NOTCH = 120;

	/** Room to look at the bedrock from below and at the clouds from above. */
	private static final double MIN_Y = -16.0;
	private static final double MAX_Y = 160.0;

	private static final Hotkey TOGGLE = new Hotkey(Settings.HOTKEY_FREECAM);

	private static boolean active;
	private static double x;
	private static double y;
	private static double z;
	private static double prevX;
	private static double prevY;
	private static double prevZ;
	private static float yaw;
	private static float pitch;
	private static double speed;

	private Freecam() {
	}

	/** The feature is available: switched on and reachable by a key. */
	public static boolean enabled() {
		return Settings.CAMERA_FREECAM.on() && Settings.HOTKEY_FREECAM.isBound();
	}

	/** The feature is available and sits on that key. */
	public static boolean boundTo(int keyCode) {
		return enabled() && Settings.HOTKEY_FREECAM.code() == keyCode;
	}

	/** The camera is flying right now. */
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

		// An open screen filters no hotkey, so a rebound letter would take off
		// while a sign is being written. See Hotkey.
		if (TOGGLE.pressed() && !host.screenOpen()) {
			if (active) {
				stop();
			} else {
				start();
			}
		}

		if (!active) {
			return;
		}

		prevX = x;
		prevY = y;
		prevZ = z;

		// A menu takes the mouse, so it takes the camera with it.
		if (!host.screenOpen()) {
			move();
		}
	}

	/** Leaves the camera where it is and hands the view back to the player. */
	public static void stop() {
		if (!active) {
			return;
		}
		active = false;
		Feinschliff.log().debug("freecam off");
	}

	/** Mouse movement for this frame, in the units vanilla hands to the player. */
	public static void look(float deltaYaw, float deltaPitch) {
		yaw = (float)(yaw + deltaYaw * LOOK_FACTOR);
		pitch = (float)(pitch - deltaPitch * LOOK_FACTOR);
		if (pitch < -90.0F) {
			pitch = -90.0F;
		}
		if (pitch > 90.0F) {
			pitch = 90.0F;
		}
	}

	/** One mouse wheel event, in LWJGL's units: a notch is 120. */
	public static void changeSpeed(int wheelDelta) {
		double notches = (double)wheelDelta / WHEEL_NOTCH;
		speed = clamp(speed * Math.pow(SPEED_STEP, notches), MIN_SPEED, MAX_SPEED);
		Feinschliff.log().debug("freecam speed " + Math.round(speed) + " blocks/s");
	}

	public static double x(float partialTick) {
		return prevX + (x - prevX) * partialTick;
	}

	public static double y(float partialTick) {
		return prevY + (y - prevY) * partialTick;
	}

	public static double z(float partialTick) {
		return prevZ + (z - prevZ) * partialTick;
	}

	public static float yaw() {
		return yaw;
	}

	public static float pitch() {
		return pitch;
	}

	/** Starts where the player's eyes are, so switching over shows no jump. */
	private static void start() {
		GameHost host = Game.host();
		x = host.playerX();
		y = host.playerY();
		z = host.playerZ();
		prevX = x;
		prevY = y;
		prevZ = z;
		yaw = host.playerYaw();
		pitch = host.playerPitch();
		speed = Settings.CAMERA_FREECAM_SPEED.value();
		active = true;
		Feinschliff.log().debug("freecam on at " + Math.round(x) + " " + Math.round(y) + " " + Math.round(z));
	}

	private static void move() {
		double forward = axis(Move.FORWARD, Move.BACK);
		double sideways = axis(Move.LEFT, Move.RIGHT);
		double vertical = axis(Move.UP, Move.DOWN);
		if (forward == 0.0 && sideways == 0.0 && vertical == 0.0) {
			return;
		}

		double yawRadians = Math.toRadians(yaw);

		// Level flight: looking down does not push the camera down. Height is
		// the vertical keys' business alone, the way walking and jumping are.
		double dx = -Math.sin(yawRadians) * forward + Math.cos(yawRadians) * sideways;
		double dy = vertical;
		double dz = Math.cos(yawRadians) * forward + Math.sin(yawRadians) * sideways;

		// Two directions at once must not be faster than one.
		double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length > 1.0) {
			dx /= length;
			dy /= length;
			dz /= length;
		}

		double step = speed / TICKS_PER_SECOND;
		x += dx * step;
		y = clamp(y + dy * step, MIN_Y, MAX_Y);
		z += dz * step;

		limitToRenderedWorld();
	}

	/**
	 * Keeps the camera inside the part of the world that is actually drawn.
	 *
	 * <p>Flying further shows nothing but the inside of the fog, and looking
	 * back from out there at terrain nobody has visited is not what this is for.
	 */
	private static void limitToRenderedWorld() {
		if (!Settings.CAMERA_FREECAM_LIMIT.on()) {
			return;
		}

		GameHost host = Game.host();
		double radius = host.renderedRadius();
		double offsetX = x - host.playerX();
		double offsetZ = z - host.playerZ();
		double distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
		if (distance <= radius) {
			return;
		}

		x = host.playerX() + offsetX / distance * radius;
		z = host.playerZ() + offsetZ / distance * radius;
	}

	private static double axis(Move positive, Move negative) {
		return (held(positive) ? 1.0 : 0.0) - (held(negative) ? 1.0 : 0.0);
	}

	/** Steered with the player's own bindings, read straight off the keyboard. */
	private static boolean held(Move direction) {
		return Keys.isDown(Game.host().movementKey(direction));
	}

	private static double clamp(double value, double min, double max) {
		return value < min ? min : value > max ? max : value;
	}
}
