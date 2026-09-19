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
	public static final BooleanOption INVENTORY_SHIFT_DRAG;
	public static final BooleanOption INVENTORY_SORT;
	public static final BooleanOption INVENTORY_SCROLL;
	public static final BooleanOption INVENTORY_QUICK_STACK;
	public static final BooleanOption INVENTORY_HOTBAR_KEYS;
	public static final BooleanOption INVENTORY_PICK_BLOCK;
	public static final BooleanOption INVENTORY_DROP_FROM_MENU;
	public static final BooleanOption INVENTORY_DROP_STACK;

	public static final BooleanOption MINING_TOOL_ASSIGNMENTS;
	public static final BooleanOption MINING_GOLD_IS_FAST;
	public static final BooleanOption MINING_SWORD_CUTS;

	public static final BooleanOption BOAT_HANDLING;
	public static final DoubleOption BOAT_TOP_SPEED;
	public static final DoubleOption BOAT_DRAG;
	public static final BooleanOption BOAT_FREE_VIEW;

	public static final BooleanOption PIG_SADDLE_DROP;
	public static final BooleanOption PIG_STEER;
	public static final DoubleOption PIG_SPEED;
	public static final BooleanOption PIG_FEED;
	public static final DoubleOption PIG_FEED_SPEED;
	public static final DoubleOption PIG_FEED_SECONDS;
	public static final BooleanOption PIG_MIND;
	public static final DoubleOption PIG_MIND_EVERY;
	public static final DoubleOption PIG_MIND_FOR;

	public static final BooleanOption SPONGE_SOAKS_UP_WATER;
	public static final BooleanOption SPONGE_KEEPS_DRY;
	public static final BooleanOption SPONGE_IN_DUNGEONS;

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
			"Comfort in the menus and with what you are carrying. Nothing here",
			"changes what an item is or does, only how far your hand has to",
			"travel to move it.");

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
			"puts one in each. What does not divide stays in your hand. The",
			"stack is laid out as you drag and shared out again at every",
			"further slot, so what you see is already the outcome. A press",
			"that never leaves its slot is an ordinary click."));

		INVENTORY_SHIFT_DRAG = SPEC.add(new BooleanOption(
			"inventory.shiftDrag", true,
			"Keep shift and the button held after a shift-click and draw across",
			"more slots to send each of them across as well. A row of your",
			"inventory into a chest, or five stacks of cobble back out, in one",
			"movement. The crafting result is left out of it, so a line passing",
			"over it does not craft the grid empty."));

		INVENTORY_SORT = SPEC.add(new BooleanOption(
			"inventory.sort", true,
			"Press the middle mouse button over a chest to tidy it: everything",
			"of one kind together, stacks filled up, empty slots at the end.",
			"Over the three rows above your hotbar it tidies those instead; the",
			"hotbar itself stays as you arranged it. Plain sorts by item id,",
			"with shift held it sorts by how much of each you have. This",
			"version has no names to sort by -- items have numbers and nothing",
			"else."));

		INVENTORY_SCROLL = SPEC.add(new BooleanOption(
			"inventory.scroll", true,
			"Turn the wheel over a stack to move it one item at a time: down",
			"sends one across, up brings one back. Across means the same place",
			"a shift-click would send it, so the wheel walks the same road in",
			"both directions. Two sticks out of a stack of sixty-four without",
			"counting them out by hand."));

		INVENTORY_QUICK_STACK = SPEC.add(new BooleanOption(
			"inventory.quickStack", true,
			"Hold control and press the middle mouse button to put away",
			"everything the chest already has some of. What it has never held",
			"stays with you, and so does your hotbar -- this tidies up, it does",
			"not empty your pockets into the first box you pass."));

		INVENTORY_HOTBAR_KEYS = SPEC.add(new BooleanOption(
			"inventory.hotbarKeys", true,
			"Point at a stack in a menu and press 1 to 9 to put it on that",
			"place of your hotbar, trading places with whatever was there, or",
			"point at an empty slot to fetch that place to you. It reaches the",
			"crafting grid as well, since pointing at a square is how a square",
			"gets picked. On a crafting result and a furnace output it works",
			"only onto a free place: a full one would mean putting something",
			"into a slot that never gives anything back."));

		INVENTORY_PICK_BLOCK = SPEC.add(new BooleanOption(
			"inventory.pickBlock", true,
			"Let the middle mouse button reach the rest of your inventory. It",
			"already picks a block you are looking at when you have it on the",
			"hotbar; this brings it to the hotbar when you have it further up,",
			"onto the nearest free place or, if there is none, in exchange for",
			"what you are holding. It still creates nothing."));

		INVENTORY_DROP_FROM_MENU = SPEC.add(new BooleanOption(
			"inventory.dropFromMenu", true,
			"Point at a stack in a menu and press the drop key to throw it out",
			"of there, without taking it into your hand first. On a crafting",
			"result one press is one craft, thrown whole."));

		INVENTORY_DROP_STACK = SPEC.add(new BooleanOption(
			"inventory.dropStack", true,
			"Hold control while pressing the drop key to throw the whole stack",
			"instead of one of it -- out of your hand in the world, and out of",
			"the slot you are pointing at in a menu. The key itself stays yours",
			"to bind in the game's own controls screen."));

		SPEC.section("Mining",
			"Which tool a block is for, and what a tool is worth on it. Only the",
			"time it takes changes, and only ever downwards -- nothing here makes",
			"anything slower than it already was.");

		MINING_TOOL_ASSIGNMENTS = SPEC.add(new BooleanOption(
			"mining.toolAssignments", true,
			"Give the twenty-one blocks that have no tool in this version the one",
			"they are meant for. A pickaxe gets bricks, obsidian, redstone ore,",
			"furnaces, spawners, stone stairs, pressure plates, the iron door,",
			"buttons and rails; an axe gets stairs, the crafting table, doors,",
			"signs, fences, jukeboxes and ladders; a shovel gets farmland; and the",
			"hoe, which until now could only till, gets leaves and sponge. The",
			"wooden pressure plate is the one thing here that changes a drop as",
			"well: the game counts it as stone, so without a pickaxe it breaks",
			"into nothing at all."));

		MINING_GOLD_IS_FAST = SPEC.add(new BooleanOption(
			"mining.goldIsFast", true,
			"Let gold dig like gold. A golden tool is the fastest there is in",
			"later versions and the slowest here, level with wood, because it",
			"shares wood's place in the one table that sets both how fast a tool",
			"is and what it may harvest. This raises the speed and nothing else:",
			"a golden pickaxe still brings up no iron, gold, diamond or redstone,",
			"which is the rule in every version and stays."));

		MINING_SWORD_CUTS = SPEC.add(new BooleanOption(
			"mining.swordCuts", true,
			"Cut wool, leaves and cactus with a sword. Shears are four years away",
			"from this version, and a sword is already a flat one and a half on",
			"every block in the game -- that is the state of things before shears,",
			"not a mistake. This makes it a machete on the three things one would",
			"reach for shears for, whatever the blade is made of, since shears",
			"have no material either."));

		SPEC.section("Boats",
			"A boat keeps 99% of its speed every tick, which is why it takes",
			"eleven seconds to get up to speed, five to slow down again, and",
			"turns like a barge. Everything else about a boat stays as it is:",
			"it still goes where you look, and it still breaks on the first",
			"thing it touches at speed.");

		BOAT_HANDLING = SPEC.add(new BooleanOption(
			"boat.handling", true,
			"Use the two numbers below instead of the vanilla ones. Off gives",
			"you the vanilla boat back exactly, whatever they say."));

		BOAT_TOP_SPEED = SPEC.add(new DoubleOption(
			"boat.topSpeed", 9.0, 1.0, 40.0,
			"How fast a boat goes, in blocks per second. Vanilla is 8, walking",
			"is about 4. A boat that goes faster also crosses the speed at",
			"which it shatters on contact more of the time -- that threshold",
			"is vanilla's and is left alone."));

		BOAT_DRAG = SPEC.add(new DoubleOption(
			"boat.drag", 0.92, 0.5, 0.99,
			"What is left of the speed after a tick. This one number is the",
			"whole feel of the boat: it sets how quickly you get going, how",
			"quickly you stop, and how tightly you turn. Vanilla is 0.99, and",
			"the lower it goes the more the boat obeys and the less it glides."));

		BOAT_FREE_VIEW = SPEC.add(new BooleanOption(
			"boat.freeView", true,
			"Stop a boat from turning your head. Vanilla points the boat at",
			"whatever direction it happens to be drifting in and drags your",
			"view after it, so looking at one spot while sailing past it is a",
			"fight. Off restores the tug."));

		SPEC.section("Pigs",
			"A saddled pig can be sat on and does nothing else with you on it:",
			"it wanders wherever it was going anyway, a third slower than you",
			"walk. Hold wheat and it comes round to where you are looking.",
			"Every stretch of time below is rolled fresh within a third either",
			"way, so nothing the pig does arrives on a schedule.");

		PIG_SADDLE_DROP = SPEC.add(new BooleanOption(
			"pig.saddleDrop", true,
			"A saddled pig gives the saddle back when it dies. Without this it",
			"is gone with the pig, which is what vanilla does."));

		PIG_STEER = SPEC.add(new BooleanOption(
			"pig.steer", true,
			"Hold wheat while riding and the pig walks where you look, without",
			"you touching a movement key. No wheat, no steering."));

		PIG_SPEED = SPEC.add(new DoubleOption(
			"pig.speed", 4.5, 1.0, 12.0,
			"How fast that is, in blocks per second. For scale: walking is 4.3,",
			"sneaking 1.3, and a pig left to itself trots at 3.0. The default",
			"is a shade above walking, so getting on one is never a step down."));

		PIG_FEED = SPEC.add(new BooleanOption(
			"pig.feed", true,
			"Right-click while riding to feed the pig one wheat. It hops, and",
			"then runs. Feeding again while it is still running adds to the",
			"time left, up to what one piece could have bought at best, so",
			"there is a point to a second piece and a limit to a sack of them.",
			"Pointing at the pig itself still gets you off it, and a click that",
			"opens a chest or a door is that click and costs no wheat."));

		PIG_FEED_SPEED = SPEC.add(new DoubleOption(
			"pig.feed.speed", 5.7, 1.0, 20.0,
			"How fast a fed pig runs, in blocks per second. 5.7 is what",
			"sprinting is worth in the versions that have it."));

		PIG_FEED_SECONDS = SPEC.add(new DoubleOption(
			"pig.feed.seconds", 15.0, 1.0, 300.0,
			"How long one piece of wheat lasts, give or take a third, and also",
			"the ceiling that feeding again cannot push past. Nothing",
			"interrupts it: a pig with wheat in its mouth has no opinions."));

		PIG_MIND = SPEC.add(new BooleanOption(
			"pig.mind", true,
			"The pig has a mind of its own and now and then uses it. Off makes",
			"it obedient, which is faster and much less of a pig."));

		PIG_MIND_EVERY = SPEC.add(new DoubleOption(
			"pig.mind.every", 45.0, 5.0, 600.0,
			"Seconds of being steered between one of those moments and the",
			"next. Time spent running on wheat does not count towards it."));

		PIG_MIND_FOR = SPEC.add(new DoubleOption(
			"pig.mind.for", 3.0, 0.5, 30.0,
			"How long it then does as it pleases. It grunts as it starts, so",
			"you know the next few steps are not yours."));

		SPEC.section("Sponges",
			"A sponge is the one block here with a job written down and nothing to",
			"show for it. The game still walks the five by five by five around a",
			"freshly laid sponge looking for water, and does nothing with what it",
			"finds.");

		SPONGE_SOAKS_UP_WATER = SPEC.add(new BooleanOption(
			"sponge.soaksUpWater", true,
			"Put a sponge down and the water two blocks in every direction goes",
			"away. Take it up again and the water comes back, which is the half",
			"of this the version already does. The sponge is not used up: there",
			"is no wet sponge here to turn into, so it stays what it is and",
			"works again wherever you carry it."));

		SPONGE_KEEPS_DRY = SPEC.add(new BooleanOption(
			"sponge.soaksUpWater.keepsDry", true,
			"Keep that space dry instead of drying it once. The sponge drinks",
			"again whenever something beside it changes, so water pressing in",
			"from outside gets no further than the last block before it. Off",
			"dries the space the moment you place the sponge and leaves it at",
			"that."));

		SPONGE_IN_DUNGEONS = SPEC.add(new BooleanOption(
			"sponge.inDungeons", true,
			"Put a sponge in a dungeon chest, in about every second one. There is",
			"otherwise no way to a sponge at all in this version: it is in no",
			"chest, on no mob and in no recipe, and the creative list it sits in",
			"cannot be opened in single player. It takes the place of something",
			"the chest had already rolled, in that same slot, so a chest holds as",
			"many stacks as it always would, and a saddle, a golden apple or a",
			"record is never what it takes. Nothing is drawn from the generator",
			"for it, so the ores, trees and springs of that chunk lie exactly",
			"where they would have. Only dungeons made from here on -- the ones",
			"already in your world are never filled again."));

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
