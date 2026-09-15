package io.github.julianbvw.feinschliff.mc.alpha.screenshot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;

import org.lwjgl.opengl.GL11;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.screenshot.Screenshots;

/**
 * The line that says a picture was taken.
 *
 * <p>It sits where the game puts its own short notices, centred above the
 * hotbar, and it is part of the hud rather than drawn over it: whatever hides
 * the hud hides this too, which is what makes a clean picture clean.
 */
public final class ScreenshotMessage {

	/** Distance from the bottom edge, the line vanilla announces a record on. */
	private static final int BOTTOM_OFFSET = 52;

	private static final int WHITE = 0xFFFFFF;

	private static boolean failed;

	private ScreenshotMessage() {
	}

	public static void render(Minecraft minecraft, float partialTick) {
		if (failed) {
			return;
		}

		String message = Screenshots.message();
		if (message == null) {
			return;
		}

		// The picture is taken after the hud is drawn, so without this the
		// message from the last one would stand in the middle of the next.
		if (Screenshots.pending()) {
			return;
		}

		int alpha = Screenshots.messageAlpha(partialTick);
		if (alpha <= 0) {
			return;
		}

		// This runs once per frame while the message is up. Reporting a failure
		// every time would flood the log, so the message steps aside instead.
		try {
			draw(minecraft, message, alpha);
		} catch (Throwable t) {
			failed = true;
			Feinschliff.log().error("the screenshot message failed and has been"
				+ " switched off for this session. Pictures are still being"
				+ " taken, and the log says where they go.", t);
		}
	}

	private static void draw(Minecraft minecraft, String message, int alpha) {
		// The hud is drawn in scaled coordinates, so the text has to be centred
		// against the scaled width rather than the window width.
		Window window = new Window(minecraft.width, minecraft.height);
		TextRenderer text = minecraft.textRenderer;

		int x = (window.getWidth() - text.getWidth(message)) / 2;
		int y = window.getHeight() - BOTTOM_OFFSET;

		// A fading colour needs blending, and the alpha test would throw the
		// faint end of it away. Vanilla sets up exactly this for its own notice
		// a few lines earlier in the same method, and leaves the hud with the
		// alpha test on and blending off, which is what is restored below.
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		// An alpha byte of zero means opaque to the text renderer, not
		// invisible, so the fade has to stop before it gets there. It does:
		// the caller leaves as soon as the alpha reaches zero.
		text.drawWithShadow(message, x, y, WHITE | alpha << 24);

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
