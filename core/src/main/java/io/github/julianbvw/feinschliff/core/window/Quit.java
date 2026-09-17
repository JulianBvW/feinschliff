package io.github.julianbvw.feinschliff.core.window;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Leaving the game from inside it.
 *
 * <p>This version offers no way out at all: the title screen ends at
 * {@code Options...}, and the only exit is the window's own close button, which
 * a window filling the screen does not show and which the keyboard shortcut for
 * it cannot always reach either.
 */
public final class Quit {

	private static boolean asked;

	private Quit() {
	}

	/** The feature is available. */
	public static boolean enabled() {
		return Settings.WINDOW_QUIT_BUTTON.on();
	}

	/** The player asked to leave. There is no way back from this. */
	public static void request() {
		asked = true;
	}

	/**
	 * Whether the process should end now that the game loop has.
	 *
	 * <p>A closed window is a setting, because somebody may want the window
	 * gone and the process left alone. A pressed quit button is not: it says
	 * what it does.
	 */
	public static boolean endsTheProcess() {
		return asked || Settings.WINDOW_EXIT_ON_CLOSE.on();
	}
}
