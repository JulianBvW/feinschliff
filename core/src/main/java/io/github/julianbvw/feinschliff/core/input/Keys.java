package io.github.julianbvw.feinschliff.core.input;

import io.github.julianbvw.feinschliff.core.platform.KeyState;

/**
 * The keyboard, as far as the core is concerned.
 *
 * <p>Polls key state instead of consuming events, so the vanilla
 * {@code Keyboard.next()} loops never see anything missing.
 */
public final class Keys {

	private static KeyState state = keyCode -> false;

	private Keys() {
	}

	public static void setKeyState(KeyState state) {
		Keys.state = state;
	}

	public static boolean isDown(int keyCode) {
		return keyCode >= 0 && state.isDown(keyCode);
	}
}
