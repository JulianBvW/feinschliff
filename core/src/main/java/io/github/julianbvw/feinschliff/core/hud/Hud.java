package io.github.julianbvw.feinschliff.core.hud;

import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.input.Hotkey;
import io.github.julianbvw.feinschliff.core.platform.Game;
import io.github.julianbvw.feinschliff.core.platform.GameHost;

/**
 * Whether the hud is on screen at all.
 *
 * <p>Hiding it takes the whole of it -- hotbar, crosshair, health, both debug
 * screens and the hand in front of the eye. What stays is everything that
 * belongs to the world rather than being drawn over it, the water and fire
 * tints included: a clear screen is for looking at the world, not for losing
 * track of what is happening to you.
 *
 * <p>The hud comes back for as long as a screen is open and goes away again
 * when it closes, so the switch survives a look in a chest.
 *
 * <p>The state is deliberately not written to the config. It belongs to the
 * moment, and a game that started up with no hud and no obvious way to get one
 * back would be a poor joke.
 */
public final class Hud {

	private static final Hotkey TOGGLE = new Hotkey(Settings.HOTKEY_HIDE_HUD);

	private static boolean hidden;

	private Hud() {
	}

	/** The feature is available: switched on and reachable by a key. */
	public static boolean enabled() {
		return Settings.HUD_HIDE.on() && Settings.HOTKEY_HIDE_HUD.isBound();
	}

	/**
	 * True while nothing but the world is being drawn.
	 *
	 * <p>An open screen always gets the hud back, and not as a courtesy: the
	 * hud is what sets up the projection that screens are drawn in, so hiding
	 * it leaves the pause menu and every container invisible but still very
	 * much clickable.
	 */
	public static boolean hidden() {
		if (!enabled() || !hidden) {
			return false;
		}

		GameHost host = Game.host();
		return host != null && !host.screenOpen();
	}

	/** Brings the hud back, for a tick loop that has given up. */
	public static void show() {
		hidden = false;
	}

	public static void tick() {
		GameHost host = Game.host();
		if (host == null) {
			return;
		}

		// Switching the feature off and on again should not hand back a screen
		// that is still blank from last time.
		if (!enabled()) {
			hidden = false;
			return;
		}

		// An open screen filters no hotkey, so a rebound letter would clear the
		// screen while a sign is being written. See Hotkey.
		if (TOGGLE.pressed() && !host.screenOpen()) {
			hidden = !hidden;
		}
	}
}
