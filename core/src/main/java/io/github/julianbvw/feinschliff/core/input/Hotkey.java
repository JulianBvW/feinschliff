package io.github.julianbvw.feinschliff.core.input;

import io.github.julianbvw.feinschliff.core.config.KeyOption;
import io.github.julianbvw.feinschliff.core.platform.KeyState;

/**
 * Edge detection for a configurable hotkey.
 *
 * <p>Polls the physical key state rather than consuming keyboard events, so it
 * never interferes with the vanilla {@code Keyboard.next()} loops in
 * {@code Minecraft.tick()} and {@code Screen.handleInputs()}.
 */
public final class Hotkey {

	private static KeyState keyState = keyCode -> false;

	private final KeyOption option;

	private boolean wasDown;

	public Hotkey(KeyOption option) {
		this.option = option;
	}

	public static void setKeyState(KeyState keyState) {
		Hotkey.keyState = keyState;
	}

	/** True exactly once per press, false while the key is held or released. */
	public boolean pressed() {
		int code = this.option.code();
		if (code < 0) {
			this.wasDown = false;
			return false;
		}

		boolean down = keyState.isDown(code);
		boolean pressed = down && !this.wasDown;
		this.wasDown = down;
		return pressed;
	}

	/** True for as long as the key is held. */
	public boolean held() {
		int code = this.option.code();
		return code >= 0 && keyState.isDown(code);
	}
}
