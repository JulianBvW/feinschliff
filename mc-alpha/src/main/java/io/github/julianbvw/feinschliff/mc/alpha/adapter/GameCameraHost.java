package io.github.julianbvw.feinschliff.mc.alpha.adapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;

import io.github.julianbvw.feinschliff.core.platform.CameraHost;

/** Answers the free camera's questions about the running game. */
public final class GameCameraHost implements CameraHost {

	/** Widest the render chunk grid ever gets, from {@code WorldRenderer.reload}. */
	private static final int MAX_GRID_WIDTH = 400;

	/** Keeps the camera a little inside the fog instead of right at its edge. */
	private static final double EDGE_MARGIN = 8.0;

	/** However short the view distance is, this much room stays usable. */
	private static final double MIN_RADIUS = 16.0;

	private final Minecraft minecraft;

	public GameCameraHost(Minecraft minecraft) {
		this.minecraft = minecraft;
	}

	@Override
	public boolean inWorld() {
		return this.minecraft.world != null && this.minecraft.player != null;
	}

	@Override
	public boolean screenOpen() {
		return this.minecraft.screen != null;
	}

	@Override
	public double playerX() {
		return this.minecraft.player.x;
	}

	/** Eye height, not foot height: this version keeps the eyes in {@code y}. */
	@Override
	public double playerY() {
		return this.minecraft.player.y;
	}

	@Override
	public double playerZ() {
		return this.minecraft.player.z;
	}

	@Override
	public float playerYaw() {
		return this.minecraft.player.yaw;
	}

	@Override
	public float playerPitch() {
		return this.minecraft.player.pitch;
	}

	/**
	 * The player's own bindings, so a rebound keyboard steers the camera the
	 * same way it steers the player.
	 */
	@Override
	public int movementKey(Move direction) {
		GameOptions options = this.minecraft.options;
		if (options == null) {
			return -1;
		}

		switch (direction) {
			case FORWARD:
				return options.forwardKey.keyCode;
			case BACK:
				return options.backKey.keyCode;
			case LEFT:
				return options.leftKey.keyCode;
			case RIGHT:
				return options.rightKey.keyCode;
			case UP:
				return options.jumpKey.keyCode;
			case DOWN:
				return options.sneakKey.keyCode;
			default:
				return -1;
		}
	}

	/**
	 * Whichever runs out first: the render chunk grid, which is a square of
	 * {@code 64 << 3 - viewDistance} blocks around the player, or the far
	 * clipping plane at {@code 256 >> viewDistance}.
	 */
	@Override
	public double renderedRadius() {
		GameOptions options = this.minecraft.options;
		if (options == null) {
			return MIN_RADIUS;
		}

		int viewDistance = options.viewDistance;
		double gridRadius = Math.min(MAX_GRID_WIDTH, 64 << 3 - viewDistance) / 2.0;
		double farPlane = 256 >> viewDistance;
		return Math.max(MIN_RADIUS, Math.min(gridRadius, farPlane) - EDGE_MARGIN);
	}
}
