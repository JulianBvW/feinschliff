package io.github.julianbvw.feinschliff.core.window;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.platform.Video;

/**
 * Keeps the driver's vertical sync in step with the config.
 *
 * <p>Driven from the client tick rather than from startup, which is what makes
 * it reloadable: the setting needs a created display, and every tick already
 * runs long after the window exists.
 */
public final class VSync {

	private static Video video = enabled -> {
	};

	private static Boolean applied;

	private static boolean failed;

	private VSync() {
	}

	public static void setVideo(Video video) {
		VSync.video = video;
	}

	/** Applies the setting on the first tick and after every config reload. */
	public static void tick() {
		if (failed) {
			return;
		}

		boolean wanted = Settings.WINDOW_VSYNC.on();
		if (applied != null && applied == wanted) {
			return;
		}

		try {
			video.setVSync(wanted);
			applied = wanted;
			Feinschliff.log().debug("vertical sync requested: " + wanted);
		} catch (Throwable t) {
			failed = true;
			Feinschliff.log().error("could not change vertical sync; leaving it"
				+ " to the driver for this session", t);
		}
	}
}
