package io.github.julianbvw.feinschliff.core.window;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.platform.WindowMode;

/**
 * A window that fills the screen without a frame around it.
 *
 * <p>Preferred over the fullscreen the game already has, which switches the
 * monitor's display mode and leaves it to the driver to put it back. A
 * borderless window keeps the desktop resolution, so nothing else on the
 * screen is moved around and alt-tabbing costs no mode switch.
 *
 * <p>There is no hotkey of its own: this sits on the key the game already uses
 * for fullscreen.
 */
public final class Borderless {

	private static WindowMode window = new WindowMode() {

		@Override
		public void setBorderless(boolean borderless) {
		}

		@Override
		public void takePointer() {
		}
	};

	private static boolean active;

	private static boolean started;

	private static boolean failed;

	private static boolean pointerToTake;

	private Borderless() {
	}

	public static void setWindow(WindowMode window) {
		Borderless.window = window;
	}

	/**
	 * The feature answers for the fullscreen key. It keeps answering after a
	 * failed switch, because the fullscreen it replaces is the reason it is
	 * here.
	 */
	public static boolean enabled() {
		return Settings.WINDOW_BORDERLESS.on();
	}

	/** The window fills the screen right now. */
	public static boolean active() {
		return active;
	}

	public static void toggle() {
		set(!active);
	}

	/**
	 * Fills the screen on the first tick if the config asks for it, and puts
	 * the window back if the feature is switched off while it is filling it.
	 */
	public static void tick() {
		if (!started) {
			started = true;
			if (enabled() && Settings.WINDOW_BORDERLESS_ON_START.on()) {
				set(true);
			}
			return;
		}

		if (pointerToTake) {
			pointerToTake = false;
			window.takePointer();
		}

		if (active && !enabled()) {
			set(false);
		}
	}

	private static void set(boolean borderless) {
		if (failed || active == borderless) {
			return;
		}

		try {
			window.setBorderless(borderless);
			active = borderless;

			// The switch takes the pointer itself, but only the window manager
			// decides when the window is really up, and a pointer cannot be
			// taken on one that is not. Hence a second attempt a tick later.
			pointerToTake = true;
			Feinschliff.log().debug("borderless " + (borderless ? "on" : "off"));
		} catch (Throwable t) {
			// A window left half changed is worse than one left alone, and a
			// second attempt would start from a state nobody can describe.
			failed = true;
			Feinschliff.log().error("could not change the window; it stays as it"
				+ " is for this session", t);
		}
	}
}
