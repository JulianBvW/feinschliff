package io.github.julianbvw.feinschliff.core.screenshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.imageio.ImageIO;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.input.Hotkey;

/**
 * Everything about a screenshot that needs no Minecraft: when one was asked
 * for, what it is called, how it reaches the disk, and what is said about it
 * afterwards. Reading the pixels off the screen is the adapter's half.
 *
 * <p>A request is raised on the client tick and picked up by the next frame,
 * because only a frame knows what the screen looks like. It is taken after
 * everything has been drawn and before the buffers are swapped, so the file
 * holds exactly the picture that is about to appear on the monitor.
 */
public final class Screenshots {

	/** Where the pictures go, below the instance root and beside {@code saves}. */
	private static final String FOLDER = "screenshots";

	/** The name modern Minecraft gives them, down to the dots in the time. */
	private static final DateTimeFormatter STAMP =
		DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);

	/** How long the message stays up, in ticks. */
	private static final int MESSAGE_TICKS = 60;

	/** The last stretch of that, over which it fades out. */
	private static final int FADE_TICKS = 20;

	private static final int OPAQUE = 255;

	private static final Hotkey TAKE = new Hotkey(Settings.HOTKEY_SCREENSHOT);

	private static Path root;

	private static boolean pending;

	private static String message;
	private static int messageTicks;

	private Screenshots() {
	}

	/** The instance root, the directory that also holds {@code saves}. */
	public static void setRoot(Path root) {
		Screenshots.root = root;
	}

	/** The feature is available: switched on and reachable by a key. */
	public static boolean enabled() {
		return Settings.SCREENSHOT_ENABLED.on() && Settings.HOTKEY_SCREENSHOT.isBound();
	}

	public static void tick() {
		if (messageTicks > 0) {
			messageTicks--;
			if (messageTicks == 0) {
				message = null;
			}
		}

		// No screen guard: unlike the letters other features sit on, a picture
		// of an open inventory is a perfectly reasonable thing to want.
		if (enabled() && TAKE.pressed()) {
			pending = true;
		}
	}

	/** True while a request is waiting for the frame that will serve it. */
	public static boolean pending() {
		return pending;
	}

	/**
	 * Claims a waiting request, at most once.
	 *
	 * <p>A request survives frames that are never drawn, which is what happens
	 * while a world is loading, and is served by the next one that is.
	 */
	public static boolean takePending() {
		if (!pending) {
			return false;
		}
		pending = false;
		return true;
	}

	/**
	 * Writes the picture and says so on screen.
	 *
	 * @param pixels one {@code 0xRRGGBB} per pixel, left to right and top to
	 *               bottom, i.e. already turned the right way up
	 */
	public static void save(int[] pixels, int width, int height) {
		try {
			Path file = write(pixels, width, height);
			show("Saved screenshot as " + file.getFileName());
			Feinschliff.log().info("saved screenshot to " + file.toAbsolutePath());
		} catch (Throwable t) {
			failed(t);
		}
	}

	/** Reports a picture that never came about, wherever it went wrong. */
	public static void failed(Throwable cause) {
		show("Could not save the screenshot, see the log");
		Feinschliff.log().error("could not save the screenshot", cause);
	}

	/** What to put on screen, or null while there is nothing to say. */
	public static String message() {
		return message;
	}

	/**
	 * How solid the message is, from 255 down to 0 over its last moments.
	 *
	 * @param partialTick how far this frame sits into the current tick, so the
	 *                    fade runs at frame rate rather than in twenty steps
	 */
	public static int messageAlpha(float partialTick) {
		if (messageTicks <= 0) {
			return 0;
		}

		float left = messageTicks - partialTick;
		int alpha = (int)(left * (OPAQUE + 1) / FADE_TICKS);
		return alpha > OPAQUE ? OPAQUE : alpha;
	}

	/** Takes the message away, for a tick loop that has given up. */
	public static void clearMessage() {
		message = null;
		messageTicks = 0;
	}

	/** One message at a time: a second picture replaces what the first said. */
	private static void show(String text) {
		message = text;
		messageTicks = MESSAGE_TICKS;
	}

	private static Path write(int[] pixels, int width, int height) throws IOException {
		Path directory = root.resolve(FOLDER);
		Files.createDirectories(directory);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);

		Path file = freeName(directory);
		ImageIO.write(image, "png", file.toFile());
		return file;
	}

	/**
	 * Two pictures in the same second are two keystrokes apart, so the plain
	 * timestamp is not enough on its own.
	 */
	private static Path freeName(Path directory) {
		String stamp = LocalDateTime.now().format(STAMP);

		Path file = directory.resolve(stamp + ".png");
		for (int attempt = 1; Files.exists(file); attempt++) {
			file = directory.resolve(stamp + "_" + attempt + ".png");
		}
		return file;
	}
}
