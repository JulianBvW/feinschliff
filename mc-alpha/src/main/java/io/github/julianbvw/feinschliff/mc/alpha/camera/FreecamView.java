package io.github.julianbvw.feinschliff.mc.alpha.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;

import org.lwjgl.opengl.GL11;

import io.github.julianbvw.feinschliff.core.camera.Freecam;

/** Points the view matrix at the free camera instead of at the player. */
public final class FreecamView {

	/** Vanilla nudges the world along the view axis before drawing it. */
	private static final float NEAR_NUDGE = -0.1F;

	private FreecamView() {
	}

	/**
	 * Replaces what {@code GameRenderer.transformCamera} would have done.
	 *
	 * <p>Rotation first, then the offset, because the offset has to be read in
	 * eye space for the camera to end up where it is asked to be.
	 *
	 * <p>The offset is the difference between player and camera, not the camera
	 * position, because every routine that draws the world submits its geometry
	 * relative to the player and keeps doing so. That is deliberate: those same
	 * routines are the ones that would load and generate chunks if they were
	 * told the camera had moved.
	 */
	public static void applyCamera(Minecraft minecraft, float partialTick) {
		ClientPlayerEntity player = minecraft.player;

		GL11.glTranslatef(0.0F, 0.0F, NEAR_NUDGE);
		GL11.glRotatef(Freecam.pitch(), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(Freecam.yaw() + 180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslated(
			interpolate(player.prevX, player.x, partialTick) - Freecam.x(partialTick),
			interpolate(player.prevY, player.y, partialTick) - Freecam.y(partialTick),
			interpolate(player.prevZ, player.z, partialTick) - Freecam.z(partialTick));
	}

	/** The same interpolation {@code WorldRenderer.render} uses for the player. */
	private static double interpolate(double previous, double current, float partialTick) {
		return previous + (current - previous) * partialTick;
	}
}
