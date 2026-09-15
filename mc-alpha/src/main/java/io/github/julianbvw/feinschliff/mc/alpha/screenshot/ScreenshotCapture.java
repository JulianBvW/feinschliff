package io.github.julianbvw.feinschliff.mc.alpha.screenshot;

import java.nio.ByteBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.MemoryTracker;

import org.lwjgl.opengl.GL11;

import io.github.julianbvw.feinschliff.core.screenshot.Screenshots;

/** Reads the finished frame back off the graphics card. */
public final class ScreenshotCapture {

	/**
	 * Read as RGBA rather than RGB, which costs a quarter more memory and buys
	 * a row length that is always a multiple of four. Anything else would have
	 * to move GL_PACK_ALIGNMENT off its default and put it back again.
	 */
	private static final int BYTES_PER_PIXEL = 4;

	private ScreenshotCapture() {
	}

	/**
	 * Serves a waiting request, once the whole frame is drawn.
	 *
	 * <p>This is the last moment the picture exists and the first moment it is
	 * complete: the world, the hud and any open screen are in the back buffer,
	 * and the swap that puts them on the monitor is still a few statements away.
	 */
	public static void endOfFrame(Minecraft minecraft) {
		if (!Screenshots.takePending()) {
			return;
		}

		try {
			capture(minecraft);
		} catch (Throwable t) {
			Screenshots.failed(t);
		}
	}

	private static void capture(Minecraft minecraft) {
		// The window's own pixels, not the scaled gui coordinates: the file is
		// meant to be the size of what is on the monitor.
		int width = minecraft.width;
		int height = minecraft.height;

		// A direct buffer, which is the only kind LWJGL can hand to the driver.
		ByteBuffer buffer = MemoryTracker.createByteBuffer(width * height * BYTES_PER_PIXEL);

		// The read buffer is left alone: for a double buffered context GL_BACK
		// is the default, and nothing in the game ever moves it.
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		Screenshots.save(rows(buffer, width, height), width, height);
	}

	/**
	 * Turns the read on its head. OpenGL counts rows from the bottom of the
	 * screen and an image file starts at the top, so a picture copied straight
	 * across comes out upside down.
	 */
	private static int[] rows(ByteBuffer buffer, int width, int height) {
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			int source = (height - 1 - y) * width * BYTES_PER_PIXEL;
			int target = y * width;

			for (int x = 0; x < width; x++) {
				int at = source + x * BYTES_PER_PIXEL;

				// The fourth byte is dropped: the screen has nothing behind it,
				// so its alpha says nothing worth keeping.
				pixels[target + x] = (buffer.get(at) & 0xFF) << 16
					| (buffer.get(at + 1) & 0xFF) << 8
					| buffer.get(at + 2) & 0xFF;
			}
		}

		return pixels;
	}
}
