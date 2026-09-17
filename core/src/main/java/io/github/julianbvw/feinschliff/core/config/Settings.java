package io.github.julianbvw.feinschliff.core.config;

/**
 * Every setting the mod has, in the order they appear in
 * {@code feinschliff.txt}.
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

	public static final BooleanOption HUD_HIDE;

	public static final BooleanOption HUD_DEBUG_OVERLAY;
	public static final BooleanOption HUD_DEBUG_OVERLAY_COLOURS;
	public static final BooleanOption HUD_DEBUG_OVERLAY_BACKGROUND;
	public static final BooleanOption HUD_DEBUG_OVERLAY_RENDER_STATS;
	public static final BooleanOption HUD_DEBUG_OVERLAY_LOOKING_AT;
	public static final BooleanOption HUD_DEBUG_OVERLAY_SEED;

	public static final BooleanOption WINDOW_BORDERLESS;
	public static final BooleanOption WINDOW_BORDERLESS_ON_START;
	public static final BooleanOption WINDOW_VSYNC;
	public static final BooleanOption WINDOW_EXIT_ON_CLOSE;
	public static final BooleanOption WINDOW_QUIT_BUTTON;

	public static final BooleanOption SCREENSHOT_ENABLED;

	public static final BooleanOption CAMERA_FREECAM;
	public static final DoubleOption CAMERA_FREECAM_SPEED;
	public static final BooleanOption CAMERA_FREECAM_SHOW_PLAYER;
	public static final BooleanOption CAMERA_FREECAM_LIMIT;

	public static final BooleanOption MOVEMENT_FLY;
	public static final DoubleOption MOVEMENT_FLY_SPEED;

	public static final BooleanOption INVENTORY_SHIFT_CLICK;
	public static final BooleanOption INVENTORY_DOUBLE_CLICK;
	public static final BooleanOption INVENTORY_DRAG;

	public static final BooleanOption GAMEPLAY_NO_EATING_AT_FULL_HEALTH;

	public static final KeyOption HOTKEY_RELOAD_CONFIG;
	public static final KeyOption HOTKEY_HIDE_HUD;
	public static final KeyOption HOTKEY_DEBUG_OVERLAY;
	public static final KeyOption HOTKEY_SCREENSHOT;
	public static final KeyOption HOTKEY_FREECAM;
	public static final KeyOption HOTKEY_FLY;

	static {
		SPEC.section("General");

		DEBUG_LOGGING = SPEC.add(new BooleanOption(
			"general.debugLogging", false,
			"Write extra diagnostic output to the log.",
			"Turn this on before reporting a problem."));

		SPEC.section("HUD",
			"The hotbar, the crosshair, the hearts and everything else the game",
			"draws on top of the world.");

		HUD_HIDE = SPEC.add(new BooleanOption(
			"hud.hide", true,
			"Take the whole hud off the screen at a keypress, both debug screens",
			"and your own hand along with it. The water and fire tints stay, so a",
			"cleared screen still tells you that you are drowning or burning.",
			"Opening a menu brings the hud back until you close it again."));

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

		WINDOW_BORDERLESS = SPEC.add(new BooleanOption(
			"window.borderless", true,
			"Put the fullscreen key on a borderless window instead: the whole",
			"screen at the resolution the desktop already runs at, with nothing",
			"drawn around it. The key keeps working as a switch, one press each",
			"way. Off hands the key back to the game's own fullscreen, which",
			"changes the monitor's display mode and, in this version, forgets to",
			"tell the game how large the screen now is."));

		WINDOW_BORDERLESS_ON_START = SPEC.add(new BooleanOption(
			"window.borderless.onStart", false,
			"Fill the screen straight away instead of waiting for the key."));

		WINDOW_VSYNC = SPEC.add(new BooleanOption(
			"window.vsync", true,
			"Wait for the monitor before showing a frame. Removes tearing and",
			"stops the game from rendering hundreds of frames nobody ever sees.",
			"A graphics driver set to force vertical sync on or off overrides",
			"this either way, and the game cannot tell which of the two won.",
			"The frame rate on the debug overlay is the honest answer."));

		WINDOW_EXIT_ON_CLOSE = SPEC.add(new BooleanOption(
			"window.exitOnClose", true,
			"Leave the process once the game loop ends, which is what closing",
			"the window starts. Without it the window stays on screen and the",
			"process lives on for another thirty seconds, until the mod loader",
			"gives up waiting and halts it."));

		WINDOW_QUIT_BUTTON = SPEC.add(new BooleanOption(
			"window.quitButton", true,
			"Put a Quit Game button on the title screen, beside Options..., both",
			"of them half as wide. This version has no way out of its own, and a",
			"window filling the screen shows no close button to reach for."));

		SPEC.section("Screenshots",
			"Pictures land in screenshots/ beside your worlds, named after the",
			"moment they were taken.");

		SCREENSHOT_ENABLED = SPEC.add(new BooleanOption(
			"screenshot.enabled", true,
			"Save a picture of the screen to a file. It holds exactly what you",
			"see, including the hud, the debug screen and your own hand."));

		SPEC.section("Camera",
			"The free camera flies on its own while you stay where you are. You",
			"cannot mine, build, attack or change items while it is out, and",
			"nothing you do with it touches the world.");

		CAMERA_FREECAM = SPEC.add(new BooleanOption(
			"camera.freecam", true,
			"Detach the camera and fly it around with the movement keys, jump",
			"and sneak. Your body stays put and stays vulnerable."));

		CAMERA_FREECAM_SPEED = SPEC.add(new DoubleOption(
			"camera.freecam.speed", 10.0, 0.5, 100.0,
			"Flying speed in blocks per second, as it is every time the camera",
			"comes out. Walking is about four. The mouse wheel changes it while",
			"flying, and that change lasts until the camera is put away."));

		CAMERA_FREECAM_SHOW_PLAYER = SPEC.add(new BooleanOption(
			"camera.freecam.showPlayer", true,
			"Draw your own body, which is otherwise only visible in third",
			"person."));

		CAMERA_FREECAM_LIMIT = SPEC.add(new BooleanOption(
			"camera.freecam.limitToRendered", true,
			"Stop the camera at the edge of the world the game has drawn. Only",
			"chunks around the player are rendered, so beyond that edge there is",
			"nothing to see anyway."));

		SPEC.section("Movement",
			"Unlike the free camera, flying moves you. It therefore loads and",
			"generates terrain wherever you go, exactly as walking there would,",
			"and it is no kind of shield: lava burns you, deep water drowns you.");

		MOVEMENT_FLY = SPEC.add(new BooleanOption(
			"movement.fly", true,
			"Fly with the movement keys, jump and sneak. You keep colliding with",
			"the world, so there is no flying through walls."));

		MOVEMENT_FLY_SPEED = SPEC.add(new DoubleOption(
			"movement.fly.speed", 11.0, 0.5, 30.0,
			"Flying speed in blocks per second. Walking is about four. The upper",
			"limit is deliberate: much faster and you outrun the terrain being",
			"made for you, and the game stops moving you at all until it catches",
			"up."));

		SPEC.section("Inventory",
			"Comfort in the menus. Nothing here changes what an item is or does,",
			"only how far your hand has to travel to move it.");

		INVENTORY_SHIFT_CLICK = SPEC.add(new BooleanOption(
			"inventory.shiftClick", true,
			"Hold shift and click a stack to send it across instead of picking",
			"it up: between you and a chest, into a furnace as fuel or as",
			"something to smelt, out of a furnace, onto your armour, and",
			"otherwise between your hotbar and the rest of your inventory.",
			"On the crafting result it makes as many as the grid and your free",
			"space allow."));

		INVENTORY_DOUBLE_CLICK = SPEC.add(new BooleanOption(
			"inventory.doubleClick", true,
			"Click a stack twice in quick succession to pull everything of the",
			"same kind in the menu into your hand, up to a full stack. It takes",
			"the part-used stacks first, so what is left behind is whole ones."));

		INVENTORY_DRAG = SPEC.add(new BooleanOption(
			"inventory.drag", true,
			"Hold a stack, press and drag across several slots to lay it out",
			"over them: the left button shares it out evenly, the right button",
			"puts one in each. What does not divide stays in your hand. A press",
			"that never leaves its slot is an ordinary click."));

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

		HOTKEY_HIDE_HUD = SPEC.add(new KeyOption(
			"hotkey.hideHud", "F1",
			"Press to clear the screen, press again to bring it back. It does",
			"nothing while a menu is open, so a letter put on this key cannot",
			"blank the screen while you write on a sign."));

		HOTKEY_DEBUG_OVERLAY = SPEC.add(new KeyOption(
			"hotkey.debugOverlay", "F3",
			"Press to switch the debug overlay on, press again to switch it off.",
			"Rebinding this leaves F3 without a function, because the vanilla",
			"screen is switched off while the overlay is on. NONE brings the",
			"vanilla screen back."));

		HOTKEY_SCREENSHOT = SPEC.add(new KeyOption(
			"hotkey.screenshot", "F2",
			"Take a picture. The game stutters for a moment while the file is",
			"written, which is the whole of it."));

		HOTKEY_FREECAM = SPEC.add(new KeyOption(
			"hotkey.freecam", "F6",
			"Press to send the camera off, press again to bring it back. F6",
			"shows the vanilla profiler chart while held, which this takes over",
			"unless the camera is put on another key."));

		HOTKEY_FLY = SPEC.add(new KeyOption(
			"hotkey.fly", "L",
			"Press to take off, press again to drop. Letting go in mid-air means",
			"falling, with everything that comes with it."));
	}

	private Settings() {
	}

	public static ConfigSpec spec() {
		return SPEC;
	}
}
