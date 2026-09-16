package io.github.julianbvw.feinschliff.mc.alpha.window;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;

import io.github.julianbvw.feinschliff.mc.alpha.mixin.MinecraftInvoker;

/**
 * Borderless fullscreen, against LWJGL 2 and the awt frame the mod loader
 * wraps this version of the game in.
 *
 * <p>That frame is the whole difficulty. The loader starts pre-1.3 versions
 * through {@code AppletMain}, so the game runs as an applet in an awt frame and
 * lwjgl draws into a canvas inside it. The border therefore belongs to the
 * frame, and awt only lets a frame drop its border while it has no native
 * window yet -- which means disposing it, which destroys the canvas underneath
 * lwjgl and takes the gl context with it.
 *
 * <p>So the frame is left alone. Instead lwjgl is handed back its parent and
 * builds a top level window of its own, where the undecorated property it has
 * always had does apply, and the frame is put away for as long as that window
 * is up. The gl context, and with it every texture, survives the move: lwjgl
 * only rebuilds the window around it.
 */
public final class BorderlessWindow {

	/**
	 * Read when lwjgl builds a window, not when it is set, so it has to stand
	 * before the window is rebuilt and means nothing afterwards.
	 */
	private static final String UNDECORATED = "org.lwjgl.opengl.Window.undecorated";

	private BorderlessWindow() {
	}

	public static void set(Minecraft minecraft, boolean borderless) {
		Canvas canvas = minecraft.canvas;
		Window frame = canvas == null ? null : windowAbove(canvas);
		if (frame == null) {
			throw new IllegalStateException("the game does not draw into an awt"
				+ " frame, so there is no border to take off");
		}

		try {
			if (borderless) {
				System.setProperty(UNDECORATED, "true");
				Display.setParent(null);
				Display.setDisplayMode(Display.getDesktopDisplayMode());
				Display.setLocation(0, 0);
				setVisible(frame, false);
			} else {
				setVisible(frame, true);
				System.clearProperty(UNDECORATED);
				Display.setParent(canvas);
			}
		} catch (LWJGLException e) {
			throw new IllegalStateException("lwjgl could not rebuild the window", e);
		}

		// The game reads its own size off the canvas every frame, and the
		// canvas is not what it is drawing into any more. Vanilla forgets this
		// step on its way into fullscreen, which is why the picture there ends
		// up in a corner at the old size.
		((MinecraftInvoker)minecraft).feinschliff$resize(Display.getWidth(), Display.getHeight());
		takePointer(minecraft);
	}

	/**
	 * Takes the pointer, which a rebuilt window does not inherit.
	 *
	 * <p>Lwjgl's display outlives the window and keeps its own flag for the
	 * grab, while the grab itself belonged to the window that was thrown away.
	 * The flag therefore still says taken, lwjgl acts only on a change to it,
	 * and the new window is left with a pointer that wanders off to the edge of
	 * the screen and stops turning the view. Letting go first is what makes the
	 * next take a change.
	 *
	 * <p>Going through the game's own mouse for the second half rather than
	 * straight to lwjgl: it drops the movement collected across the switch,
	 * which would otherwise arrive as one jerk of the view.
	 *
	 * <p>Does nothing while a screen is open, where the pointer belongs to the
	 * player rather than to the view.
	 */
	public static void takePointer(Minecraft minecraft) {
		if (!minecraft.focused) {
			return;
		}
		Mouse.setGrabbed(false);
		minecraft.mouse.lock();
	}

	/**
	 * Hides or shows the frame, and waits for it.
	 *
	 * <p>Waiting is what the way back needs: a pointer cannot be taken on a
	 * window the display server has not put up yet, and lwjgl asks for it the
	 * moment it is pointed at the canvas again. The flush is part of that --
	 * without it the show is a request sitting in a buffer.
	 *
	 * <p>Even so this only gets the request as far as the server, not past the
	 * window manager, which is why the pointer is asked for a second time a
	 * tick later.
	 */
	private static void setVisible(final Window frame, final boolean visible) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					frame.setVisible(visible);
					if (visible) {
						frame.toFront();
					}
					Toolkit.getDefaultToolkit().sync();
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("interrupted while changing the window", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("could not change the window", e.getCause());
		}
	}

	private static Window windowAbove(Component component) {
		for (Component above = component.getParent(); above != null; above = above.getParent()) {
			if (above instanceof Window) {
				return (Window)above;
			}
		}
		return null;
	}
}
