package io.github.julianbvw.feinschliff.core.config;

/**
 * Every setting the mod has, in the order they appear in {@code settings.txt}.
 *
 * <p>Conventions:
 * <ul>
 *   <li>Keys are grouped by a dotted prefix: {@code hud.}, {@code hotkey.}, ...</li>
 *   <li>Every feature defaults to {@code true}. Whoever installs the mod wants
 *       what it does, so switching one back to vanilla is the deliberate act,
 *       not switching it on.</li>
 *   <li>Diagnostics are the exception and stay off until someone needs them.</li>
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

	public static final BooleanOption WINDOW_VSYNC;

	public static final BooleanOption GAMEPLAY_NO_EATING_AT_FULL_HEALTH;

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

		SPEC.section("Window");

		WINDOW_VSYNC = SPEC.add(new BooleanOption(
			"window.vsync", true,
			"Wait for the monitor before showing a frame. Removes tearing and",
			"stops the game from rendering hundreds of frames nobody ever sees.",
			"A graphics driver set to force vertical sync on or off overrides",
			"this either way, and the game cannot tell which of the two won.",
			"The frame rate on the debug overlay is the honest answer."));

		SPEC.section("Gameplay",
			"Unlike the sections above, these change how the game plays rather than",
			"how it looks.");

		GAMEPLAY_NO_EATING_AT_FULL_HEALTH = SPEC.add(new BooleanOption(
			"gameplay.noEatingAtFullHealth", true,
			"Keep food in your hand instead of eating it when you are already at",
			"full health. This version has no hunger bar, so such a meal heals",
			"nothing and is simply gone."));

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
