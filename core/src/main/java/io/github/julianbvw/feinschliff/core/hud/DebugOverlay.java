package io.github.julianbvw.feinschliff.core.hud;

import static io.github.julianbvw.feinschliff.core.hud.TextFormat.AQUA;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.BLUE;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.DARK_GRAY;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.GRAY;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.GREEN;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.RED;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.WHITE;
import static io.github.julianbvw.feinschliff.core.hud.TextFormat.YELLOW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.input.Hotkey;

/**
 * Content and colouring of the debug overlay.
 *
 * <p>Both columns are built as a list of blocks. A block is a group of lines
 * that belong together and that the adapter puts on a panel of its own, with a
 * line of space between neighbouring blocks. Drawing is the adapter's job --
 * this class knows nothing about Minecraft beyond the numbers it is handed.
 *
 * <p>The left column answers "where am I", the right one "what is the game
 * doing". The game's own version line stays where it is and heads the first
 * block on the left.
 */
public final class DebugOverlay {

	/** Baseline distance between two lines, the same spacing vanilla uses. */
	public static final int LINE_HEIGHT = 10;

	/** Gap to the top and to the left or right screen edge. */
	public static final int MARGIN = 2;

	/**
	 * The game keeps drawing its version line at {@link #MARGIN}, so the left
	 * column starts one line further down and uses it as its heading.
	 */
	public static final int LEFT_TOP = MARGIN + LINE_HEIGHT;

	/** Yaw 0 points towards positive Z, and turning right adds to it. */
	private static final String[] COMPASS = { "south", "west", "north", "east" };
	private static final String[] COMPASS_AXIS = { "Z+", "X-", "Z-", "X+" };

	/** Block face indices as the game numbers them. */
	private static final String[] FACES = { "down", "up", "north", "south", "west", "east" };

	/** Lines after the first in a block hang under their heading. */
	private static final String INDENT = " ";

	private static final Hotkey HOTKEY = new Hotkey(Settings.HOTKEY_DEBUG_OVERLAY);

	private static boolean visible;

	private DebugOverlay() {
	}

	/** Flips the overlay on and off. Called once per client tick. */
	public static void tick() {
		if (HOTKEY.pressed()) {
			visible = !visible;
		}
	}

	/**
	 * True when this overlay has taken the place of the vanilla debug screen.
	 *
	 * <p>An unbound key gives the vanilla screen back rather than leaving the
	 * game with no debug screen at all.
	 */
	public static boolean enabled() {
		return Settings.HUD_DEBUG_OVERLAY.on() && Settings.HOTKEY_DEBUG_OVERLAY.isBound();
	}

	/** True while the overlay is switched on and toggled visible. */
	public static boolean showing() {
		return enabled() && visible;
	}

	/** True when the text sits on a translucent panel. */
	public static boolean background() {
		return Settings.HUD_DEBUG_OVERLAY_BACKGROUND.on();
	}

	public static List<List<String>> leftBlocks(DebugSnapshot snapshot) {
		List<List<String>> blocks = new ArrayList<>();

		List<String> game = new ArrayList<>();
		game.add(colourLeadingNumber(snapshot.fpsInfo, 50, 25));
		if (Settings.HUD_DEBUG_OVERLAY_RENDER_STATS.on()) {
			game.add(GRAY + snapshot.renderChunkInfo);
			game.add(GRAY + snapshot.renderEntityInfo);
			game.add(GRAY + snapshot.worldInfo);
		}
		blocks.add(game);

		if (!snapshot.inWorld) {
			return finish(blocks);
		}

		List<String> position = new ArrayList<>();
		position.add(GRAY + "XYZ: "
			+ RED + decimals(snapshot.x) + " "
			+ GREEN + decimals(snapshot.y) + " "
			+ BLUE + decimals(snapshot.z));
		if (snapshot.freecam) {
			position.add(GRAY + "Camera: "
				+ RED + decimals(snapshot.cameraX) + " "
				+ GREEN + decimals(snapshot.cameraY) + " "
				+ BLUE + decimals(snapshot.cameraZ));
		}
		position.add(GRAY + "Chunk: " + WHITE + (snapshot.blockX >> 4) + " " + (snapshot.blockZ >> 4)
			+ DARK_GRAY + " (" + GRAY + "in chunk " + WHITE + (snapshot.blockX & 15) + " " + (snapshot.blockZ & 15)
			+ DARK_GRAY + ")");
		position.addAll(facingLines(snapshot));
		blocks.add(position);

		// Only worth a block while there really is something under the crosshair.
		if (Settings.HUD_DEBUG_OVERLAY_LOOKING_AT.on()) {
			blocks.add(lookingAtBlock(snapshot));
		}

		return finish(blocks);
	}

	public static List<List<String>> rightBlocks(DebugSnapshot snapshot) {
		List<List<String>> blocks = new ArrayList<>();

		long max = Math.max(snapshot.maxMemory, 1L);
		long usedPercent = snapshot.usedMemory * 100L / max;
		long allocatedPercent = snapshot.totalMemory * 100L / max;

		List<String> memory = new ArrayList<>();
		memory.add(GRAY + "Used memory: " + memoryColour(usedPercent) + usedPercent + "%"
			+ GRAY + " (" + WHITE + megabytes(snapshot.usedMemory) + GRAY + ") of "
			+ WHITE + megabytes(max));
		memory.add(GRAY + "Allocated memory: " + WHITE + allocatedPercent + "%"
			+ GRAY + " (" + WHITE + megabytes(snapshot.totalMemory) + GRAY + ")");
		blocks.add(memory);

		if (!snapshot.inWorld) {
			return finish(blocks);
		}

		List<String> world = new ArrayList<>();
		if (Settings.HUD_DEBUG_OVERLAY_SEED.on()) {
			world.add(GRAY + "Seed: " + WHITE + snapshot.seed);
		}
		world.add(lightLine(snapshot));
		world.add(timeLine(snapshot));
		blocks.add(world);

		return finish(blocks);
	}

	private static List<String> lookingAtBlock(DebugSnapshot snapshot) {
		if (snapshot.targetIsEntity) {
			return Collections.singletonList(GRAY + "Looking at " + WHITE + "an entity");
		}
		if (!snapshot.targetIsBlock) {
			return Collections.emptyList();
		}

		List<String> lines = new ArrayList<>(2);
		lines.add(GRAY + "Looking at " + WHITE
			+ snapshot.targetX + " " + snapshot.targetY + " " + snapshot.targetZ);
		lines.add(INDENT + GRAY + "block " + WHITE + snapshot.targetBlockId + ":" + snapshot.targetMetadata
			+ GRAY + " face " + WHITE + face(snapshot.targetFace));
		return lines;
	}

	private static List<String> facingLines(DebugSnapshot snapshot) {
		int index = (int) Math.floor((double) snapshot.yaw * 4.0 / 360.0 + 0.5) & 3;

		List<String> lines = new ArrayList<>(2);
		lines.add(GRAY + "Facing: " + AQUA + COMPASS[index] + DARK_GRAY + " (" + COMPASS_AXIS[index] + ")");
		lines.add(INDENT + GRAY + "yaw " + WHITE + degrees(wrapDegrees(snapshot.yaw))
			+ GRAY + " pitch " + WHITE + degrees(snapshot.pitch));
		return lines;
	}

	private static String lightLine(DebugSnapshot snapshot) {
		if (!snapshot.lightKnown) {
			return GRAY + "Light: " + DARK_GRAY + "unknown, chunk not loaded";
		}

		// Below 8 the game may spawn monsters here, see MonsterEntity.canSpawn().
		String colour = snapshot.lightEffective >= 12 ? GREEN
			: snapshot.lightEffective >= 8 ? YELLOW : RED;
		return GRAY + "Light: " + colour + snapshot.lightEffective
			+ DARK_GRAY + " (" + GRAY + "sky " + WHITE + snapshot.lightSky
			+ GRAY + ", block " + WHITE + snapshot.lightBlock + DARK_GRAY + ")";
	}

	private static String timeLine(DebugSnapshot snapshot) {
		long day = snapshot.worldTicks / 24000L;
		int tick = (int) (snapshot.worldTicks % 24000L);

		// Tick 0 is sunrise, which the clock in the inventory shows as 06:00.
		int hour = (tick / 1000 + 6) % 24;
		int minute = (int) (tick % 1000L * 60L / 1000L);
		String colour = tick < 12000 ? YELLOW : BLUE;

		return GRAY + "Time: " + WHITE + "day " + day + DARK_GRAY + ", "
			+ colour + String.format(Locale.ROOT, "%02d:%02d", hour, minute);
	}

	private static String face(int index) {
		return index >= 0 && index < FACES.length ? FACES[index] : String.valueOf(index);
	}

	/**
	 * Colours the number a string starts with and greys out the rest, so a line
	 * such as {@code 60 fps, 0 chunk updates} reads at a glance. Strings that do
	 * not start with a number are simply greyed out.
	 */
	private static String colourLeadingNumber(String text, int good, int acceptable) {
		int end = 0;
		while (end < text.length() && end < 9 && Character.isDigit(text.charAt(end))) {
			end++;
		}
		if (end == 0) {
			return GRAY + text;
		}

		int value = Integer.parseInt(text.substring(0, end));
		String colour = value >= good ? GREEN : value >= acceptable ? YELLOW : RED;
		return colour + text.substring(0, end) + GRAY + text.substring(end);
	}

	private static String memoryColour(long percent) {
		return percent <= 60L ? GREEN : percent <= 85L ? YELLOW : RED;
	}

	private static String megabytes(long bytes) {
		return bytes / 1024L / 1024L + "MB";
	}

	private static String decimals(double value) {
		return String.format(Locale.ROOT, "%.3f", value);
	}

	private static String degrees(float value) {
		return String.format(Locale.ROOT, "%.1f", value);
	}

	/** Folds an angle into the -180..180 range players expect to read. */
	private static float wrapDegrees(float value) {
		float wrapped = value % 360.0F;
		if (wrapped >= 180.0F) {
			wrapped -= 360.0F;
		}
		if (wrapped < -180.0F) {
			wrapped += 360.0F;
		}
		return wrapped;
	}

	private static List<List<String>> finish(List<List<String>> blocks) {
		if (Settings.HUD_DEBUG_OVERLAY_COLOURS.on()) {
			return blocks;
		}

		List<List<String>> plain = new ArrayList<>(blocks.size());
		for (List<String> block : blocks) {
			List<String> lines = new ArrayList<>(block.size());
			for (String line : block) {
				lines.add(TextFormat.strip(line));
			}
			plain.add(lines);
		}
		return plain;
	}
}
