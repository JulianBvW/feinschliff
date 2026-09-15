package io.github.julianbvw.feinschliff.mc.alpha.hud;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.camera.Freecam;
import io.github.julianbvw.feinschliff.core.hud.DebugOverlay;
import io.github.julianbvw.feinschliff.core.hud.DebugSnapshot;

/**
 * Reads the debug overlay's numbers off the game and draws what the core makes
 * of them.
 */
public final class DebugOverlayRenderer {

	/** Highest block layer this version has; light above it is not stored. */
	private static final int MAX_BLOCK_Y = 127;

	/** The text colour the codes in a line start from. */
	private static final int BASE_COLOUR = 0xFFFFFF;

	/** Height of a drawn line of text. */
	private static final int FONT_HEIGHT = 8;

	/** Space between the text and the edge of the panel behind it. */
	private static final int PANEL_PADDING = 2;

	/** Dark enough to carry white text over snow, soft enough to see through. */
	private static final int PANEL_COLOUR = 0x90000000;

	/**
	 * The line the game draws above the left column. Only its width is needed,
	 * so that the first panel comes out wide enough to sit under it.
	 */
	private static final String VANILLA_VERSION_LINE = "Minecraft Alpha v1.1.2_01";

	/** Refilled every frame rather than reallocated; only the render thread uses it. */
	private static final DebugSnapshot SNAPSHOT = new DebugSnapshot();

	private static boolean failed;

	private DebugOverlayRenderer() {
	}

	public static void render(Minecraft minecraft) {
		if (failed || !DebugOverlay.showing()) {
			return;
		}

		// This runs once per frame. Reporting a failure every time would flood
		// the log and the frame rate, so the overlay steps aside after the first.
		try {
			read(SNAPSHOT, minecraft);
			draw(SNAPSHOT, minecraft);
		} catch (Throwable t) {
			failed = true;
			Feinschliff.log().error("the debug overlay failed and has been switched"
				+ " off for this session. The game keeps running. Please report this.", t);
		}
	}

	private static void read(DebugSnapshot snapshot, Minecraft minecraft) {
		snapshot.fpsInfo = minecraft.fpsDebugInfo;
		snapshot.renderChunkInfo = minecraft.getRenderChunkDebugInfo();
		snapshot.renderEntityInfo = minecraft.getRenderEntityDebugInfo();
		snapshot.worldInfo = minecraft.getWorldDebugInfo();

		Runtime runtime = Runtime.getRuntime();
		snapshot.maxMemory = runtime.maxMemory();
		snapshot.totalMemory = runtime.totalMemory();
		snapshot.usedMemory = snapshot.totalMemory - runtime.freeMemory();

		World world = minecraft.world;
		PlayerEntity player = minecraft.player;
		snapshot.inWorld = world != null && player != null;
		if (!snapshot.inWorld) {
			return;
		}

		snapshot.x = player.x;
		snapshot.y = player.y;
		snapshot.z = player.z;

		snapshot.freecam = Freecam.active();
		if (snapshot.freecam) {
			// Whole ticks, the way the player's own numbers above are read.
			// Interpolating would make the last decimal jitter every frame.
			snapshot.cameraX = Freecam.x(1.0F);
			snapshot.cameraY = Freecam.y(1.0F);
			snapshot.cameraZ = Freecam.z(1.0F);
		}

		snapshot.blockX = floor(player.x);
		snapshot.blockY = floor(player.y);
		snapshot.blockZ = floor(player.z);
		snapshot.yaw = player.yaw;
		snapshot.pitch = player.pitch;
		snapshot.worldTicks = world.ticks;
		snapshot.seed = world.seed;

		readLight(snapshot, world);
		readTarget(snapshot, world, minecraft.crosshairTarget);
	}

	/**
	 * Light is only read where the chunk is already in memory. Asking outside a
	 * loaded chunk would make World.getBlock() generate it, and the chunk cache
	 * writes whatever it later evicts to disk -- an overlay that only looks at
	 * the world must not be able to grow the save.
	 */
	private static void readLight(DebugSnapshot snapshot, World world) {
		int y = clamp(snapshot.blockY, 0, MAX_BLOCK_Y);

		snapshot.lightKnown = world.isChunkLoaded(snapshot.blockX, y, snapshot.blockZ);
		if (!snapshot.lightKnown) {
			return;
		}

		snapshot.lightEffective = world.getRawBrightness(snapshot.blockX, y, snapshot.blockZ);
		snapshot.lightSky = world.getLight(LightType.SKY, snapshot.blockX, y, snapshot.blockZ);
		snapshot.lightBlock = world.getLight(LightType.BLOCK, snapshot.blockX, y, snapshot.blockZ);
	}

	private static void readTarget(DebugSnapshot snapshot, World world, HitResult target) {
		snapshot.targetIsBlock = false;
		snapshot.targetIsEntity = false;

		if (target == null) {
			return;
		}
		if (target.entity != null) {
			snapshot.targetIsEntity = true;
			return;
		}
		if (!world.isChunkLoaded(target.x, target.y, target.z)) {
			return;
		}

		snapshot.targetIsBlock = true;
		snapshot.targetBlockId = world.getBlock(target.x, target.y, target.z);
		snapshot.targetMetadata = world.getBlockMetadata(target.x, target.y, target.z);
		snapshot.targetX = target.x;
		snapshot.targetY = target.y;
		snapshot.targetZ = target.z;
		snapshot.targetFace = target.face;
	}

	private static void draw(DebugSnapshot snapshot, Minecraft minecraft) {
		// The gui is drawn in scaled coordinates, so the right hand column has
		// to be placed against the scaled width, not the window width.
		Window window = new Window(minecraft.width, minecraft.height);
		TextRenderer text = minecraft.textRenderer;

		// The first panel on the left reaches up to the top margin, because the
		// game keeps drawing its version line there, above our first line.
		drawColumn(text, DebugOverlay.leftBlocks(snapshot), DebugOverlay.LEFT_TOP,
			DebugOverlay.MARGIN, text.getWidth(VANILLA_VERSION_LINE), 0);
		drawColumn(text, DebugOverlay.rightBlocks(snapshot), DebugOverlay.MARGIN,
			DebugOverlay.MARGIN, 0, window.getWidth());
	}

	/**
	 * Draws one column of blocks, each on a panel of its own and separated by a
	 * blank line.
	 *
	 * @param firstLineY   where the first line of text goes
	 * @param firstPanelTop top edge of the first panel, which may sit higher
	 *                      than the text to take in the version line above it
	 * @param firstPanelMinWidth width the first panel must cover no matter how
	 *                           short its own lines are
	 * @param rightEdge    right aligns against this screen width, or 0 to left
	 *                     align against the margin
	 */
	private static void drawColumn(TextRenderer text, List<List<String>> blocks, int firstLineY,
			int firstPanelTop, int firstPanelMinWidth, int rightEdge) {
		boolean rightAligned = rightEdge > 0;
		boolean background = DebugOverlay.background();

		int lineY = firstLineY;
		int panelTop = firstPanelTop;
		int minWidth = firstPanelMinWidth;

		for (List<String> block : blocks) {
			if (block.isEmpty()) {
				continue;
			}

			int width = Math.max(minWidth, widestLine(text, block));
			int left = rightAligned ? rightEdge - DebugOverlay.MARGIN - width : DebugOverlay.MARGIN;

			if (background) {
				int bottom = lineY + (block.size() - 1) * DebugOverlay.LINE_HEIGHT + FONT_HEIGHT;
				panel(left, panelTop, width, bottom - panelTop);
			}

			for (String line : block) {
				int x = rightAligned ? rightEdge - DebugOverlay.MARGIN - text.getWidth(line) : DebugOverlay.MARGIN;
				text.drawWithShadow(line, x, lineY, BASE_COLOUR);
				lineY += DebugOverlay.LINE_HEIGHT;
			}

			// One blank line of air, then the next panel hugs its own text.
			lineY += DebugOverlay.LINE_HEIGHT;
			panelTop = lineY;
			minWidth = 0;
		}
	}

	private static int widestLine(TextRenderer text, List<String> lines) {
		int widest = 0;
		for (String line : lines) {
			widest = Math.max(widest, text.getWidth(line));
		}
		return widest;
	}

	/** A translucent rectangle behind a block of text, padding included. */
	private static void panel(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			return;
		}

		float alpha = (PANEL_COLOUR >>> 24) / 255.0F;
		float red = (PANEL_COLOUR >> 16 & 0xFF) / 255.0F;
		float green = (PANEL_COLOUR >> 8 & 0xFF) / 255.0F;
		float blue = (PANEL_COLOUR & 0xFF) / 255.0F;

		int x1 = x - PANEL_PADDING;
		int y1 = y - PANEL_PADDING;
		int x2 = x + width + PANEL_PADDING;
		int y2 = y + height + PANEL_PADDING;

		// Leaves the state the hud runs in: textures on, blending off.
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(red, green, blue, alpha);

		Tesselator tesselator = Tesselator.INSTANCE;
		tesselator.begin();
		tesselator.vertex(x1, y2, 0.0);
		tesselator.vertex(x2, y2, 0.0);
		tesselator.vertex(x2, y1, 0.0);
		tesselator.vertex(x1, y1, 0.0);
		tesselator.end();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private static int floor(double value) {
		return (int) Math.floor(value);
	}

	private static int clamp(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}
}
