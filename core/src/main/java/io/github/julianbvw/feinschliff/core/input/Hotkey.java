package io.github.julianbvw.feinschliff.core.input;

import java.util.ArrayList;
import java.util.List;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.KeyOption;
import io.github.julianbvw.feinschliff.core.platform.KeyState;

/**
 * A configurable hotkey, sampled once per client tick.
 *
 * <p>Polls the physical key state instead of consuming keyboard events, so it
 * never interferes with the vanilla {@code Keyboard.next()} loops in
 * {@code Minecraft.tick()} and {@code Screen.handleInputs()}.
 */
public final class Hotkey {

	private static final List<Hotkey> ALL = new ArrayList<>();

	private static KeyState keyState = keyCode -> false;

	private final KeyOption option;

	private boolean down;
	private boolean pressed;

	public Hotkey(KeyOption option) {
		this.option = option;
		ALL.add(this);
	}

	public static void setKeyState(KeyState keyState) {
		Hotkey.keyState = keyState;
	}

	/** Samples every hotkey. Called once per client tick, before anything reads one. */
	public static void updateAll() {
		for (int i = 0; i < ALL.size(); i++) {
			ALL.get(i).update();
		}
	}

	/** True for the one tick in which a press was recognised. */
	public boolean pressed() {
		return this.pressed;
	}

	private void update() {
		// An unbound key reads as -1, which is never down.
		int code = this.option.code();
		boolean nowDown = code >= 0 && keyState.isDown(code);

		this.pressed = nowDown && !this.down;
		if (nowDown != this.down) {
			this.down = nowDown;
			Feinschliff.log().debug(this.option.key() + (nowDown ? ": pressed" : ": released"));
		}
	}
}
