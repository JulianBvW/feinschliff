package io.github.julianbvw.feinschliff.mc.alpha.adapter;

import org.lwjgl.opengl.Display;

/** Display settings, against LWJGL 2. */
public final class LwjglDisplay {

	private LwjglDisplay() {
	}

	/**
	 * Does nothing until the display exists, so an early call is harmless:
	 * {@code Display.setSwapInterval} remembers the value and only forwards it
	 * to the drawable once there is one.
	 */
	public static void setVSync(boolean enabled) {
		Display.setVSyncEnabled(enabled);
	}
}
