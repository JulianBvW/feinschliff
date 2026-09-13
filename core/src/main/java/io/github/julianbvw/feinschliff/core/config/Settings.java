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
	public static final KeyOption HOTKEY_RELOAD_CONFIG;

	static {
		SPEC.section("General");

		DEBUG_LOGGING = SPEC.add(new BooleanOption(
			"general.debugLogging", false,
			"Write extra diagnostic output to the log.",
			"Turn this on before reporting a problem."));

		SPEC.section("Hotkeys",
			"Key names are the LWJGL 2 names: A-Z, 0-9, F1-F12, SPACE, RETURN,",
			"LSHIFT, RSHIFT, LCONTROL, TAB, and so on.");

		HOTKEY_RELOAD_CONFIG = SPEC.add(new KeyOption(
			"hotkey.reloadConfig", "F10",
			"Re-read this file without restarting the game.",
			"Note: display settings are read before the window is created and",
			"therefore still need a restart."));
	}

	private Settings() {
	}

	public static ConfigSpec spec() {
		return SPEC;
	}
}
