package io.github.julianbvw.feinschliff.core.input;

import java.util.ArrayList;
import java.util.List;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.KeyOption;

/**
 * A configurable hotkey, sampled once per client tick.
 *
 * <p>Polls the physical key state instead of consuming keyboard events, so it
 * never interferes with the vanilla {@code Keyboard.next()} loops in
 * {@code Minecraft.tick()} and {@code Screen.handleInputs()}.
 *
 * <p>That is also why an open screen does not filter these the way it filters
 * the movement keys: nothing takes the events away, because none are taken in
 * the first place. Whoever binds a key that can be typed has to ask whether a
 * screen is open before acting on it.
 */
public final class Hotkey {

	private static final List<Hotkey> ALL = new ArrayList<>();

	private final KeyOption option;

	private boolean down;
	private boolean pressed;

	public Hotkey(KeyOption option) {
		this.option = option;
		ALL.add(this);
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
		boolean nowDown = Keys.isDown(this.option.code());

		this.pressed = nowDown && !this.down;
		if (nowDown != this.down) {
			this.down = nowDown;
			Feinschliff.log().debug(this.option.key() + (nowDown ? ": pressed" : ": released"));
		}
	}
}
