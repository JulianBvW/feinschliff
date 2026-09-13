package io.github.julianbvw.feinschliff.core.config;

/**
 * Every setting the mod has, in the order they appear in {@code settings.txt}.
 *
 * <p>Conventions:
 * <ul>
 *   <li>Keys are grouped by a dotted prefix: {@code hud.}, {@code hotkey.}, ...</li>
 *   <li>Anything that changes game behaviour defaults to {@code false}, so a
 *       freshly installed mod treats the world exactly like vanilla.</li>
 *   <li>Pure display and window features may default to {@code true}.</li>
 * </ul>
 */
public final class Settings {

	/** Directory below the instance root that holds the mod's files. */
	public static final String CONFIG_DIR = "config";

	/** Named after the mod so it is obvious who owns it. */
	public static final String FILE_NAME = "feinschliff.txt";

	private static final ConfigSpec SPEC = new ConfigSpec();

	public static final BooleanOption DEBUG_LOGGING;

	public static final BooleanOption HUD_DEBUG_OVERLAY;
	public static final BooleanOption HUD_DEBUG_OVERLAY_COLOURS;
	public static final BooleanOption HUD_DEBUG_OVERLAY_BACKGROUND;
	public static final BooleanOption HUD_DEBUG_OVERLAY_RENDER_STATS;
	public static final BooleanOption HUD_DEBUG_OVERLAY_LOOKING_AT;
	public static final BooleanOption HUD_DEBUG_OVERLAY_SEED;

	public static final KeyOption HOTKEY_RELOAD_CONFIG;
	public static final KeyOption HOTKEY_DEBUG_OVERLAY;

	static {
		SPEC.section("General");

		DEBUG_LOGGING = SPEC.add(new BooleanOption(
			"general.debugLogging", false,
			"Write extra diagnostic output to the log.",
			"Turn this on before reporting a problem."));

		SPEC.section("Debug overlay",
			"The screen the debug key switches on. While it is on it replaces the",
			"vanilla F3 screen instead of adding a second block of text next to it.");

		HUD_DEBUG_OVERLAY = SPEC.add(new BooleanOption(
			"hud.debugOverlay", true,
			"Show position, facing, light level and world time on the debug",
			"screen. Off leaves the vanilla F3 screen exactly as it is."));

		HUD_DEBUG_OVERLAY_COLOURS = SPEC.add(new BooleanOption(
			"hud.debugOverlay.colours", true,
			"Colour the overlay. Off draws it in plain white."));

		HUD_DEBUG_OVERLAY_BACKGROUND = SPEC.add(new BooleanOption(
			"hud.debugOverlay.background", true,
			"Put a dark translucent panel behind the text so it stays readable",
			"over snow, sand and sky."));

		HUD_DEBUG_OVERLAY_RENDER_STATS = SPEC.add(new BooleanOption(
			"hud.debugOverlay.renderStats", true,
			"Keep the vanilla renderer counters, the lines starting with C:, E:",
			"and P:."));

		HUD_DEBUG_OVERLAY_LOOKING_AT = SPEC.add(new BooleanOption(
			"hud.debugOverlay.lookingAt", true,
			"Show the block under the crosshair as blockId:metadata, with its",
			"position and the face you are pointing at."));

		HUD_DEBUG_OVERLAY_SEED = SPEC.add(new BooleanOption(
			"hud.debugOverlay.seed", true,
			"Show the world seed."));

		SPEC.section("Hotkeys",
			"Key names are the LWJGL 2 names: A-Z, 0-9, F1-F12, SPACE, RETURN,",
			"LSHIFT, RSHIFT, LCONTROL, TAB, and so on.");

		HOTKEY_RELOAD_CONFIG = SPEC.add(new KeyOption(
			"hotkey.reloadConfig", "F10",
			"Re-read this file without restarting the game.",
			"Note: display settings are read before the window is created and",
			"therefore still need a restart."));

		HOTKEY_DEBUG_OVERLAY = SPEC.add(new KeyOption(
			"hotkey.debugOverlay", "F3",
			"Press to switch the debug overlay on, press again to switch it off.",
			"Rebinding this leaves F3 without a function, because the vanilla",
			"screen is switched off while the overlay is on. NONE brings the",
			"vanilla screen back."));
	}

	private Settings() {
	}

	public static ConfigSpec spec() {
		return SPEC;
	}
}
