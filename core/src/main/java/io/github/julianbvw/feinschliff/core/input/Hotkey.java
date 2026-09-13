package io.github.julianbvw.feinschliff.core.input;

import java.util.ArrayList;
import java.util.List;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.KeyOption;
import io.github.julianbvw.feinschliff.core.platform.KeyState;

/**
 * A configurable hotkey, sampled once per client tick.
 *
 * <p>Polls the physical key state rather than consuming keyboard events, so it
 * never interferes with the vanilla {@code Keyboard.next()} loops in
 * {@code Minecraft.tick()} and {@code Screen.handleInputs()}.
 *
 * <p>A held key does not read as held the whole time. LWJGL 2 reports it as
 * released for a moment between two auto-repeats whenever the X server does not
 * support detectable key repeat: its Linux backend clears the key state on the
 * repeat's release event and restores it only once the matching press is
 * processed, so anything polling in between sees a gap. Left alone, one long
 * press therefore arrives as a stream of presses.
 *
 * <p>Filtering that out with a single timeout does not work, because the two
 * cases pull in opposite directions -- two deliberate taps in quick succession
 * need a short window, a two second press needs a long one. The key's own
 * history decides instead: auto-repeat cannot have started before the X repeat
 * delay has passed, so a key released quickly is taken at its word, and only a
 * key that has been down long enough to repeat has its releases scrutinised.
 */
public final class Hotkey {

	/** Below the shortest X repeat delay anyone can configure. */
	private static final long REPEAT_POSSIBLE_AFTER_NANOS = 250_000_000L;

	/** How long a key that may be repeating has to read as released. */
	private static final long HELD_RELEASE_GRACE_NANOS = 500_000_000L;

	private static final List<Hotkey> ALL = new ArrayList<>();

	private static KeyState keyState = keyCode -> false;

	private final KeyOption option;

	private boolean down;
	private boolean pressed;
	private long downSince;

	private boolean releasing;
	private long releasingSince;

	public Hotkey(KeyOption option) {
		this.option = option;
		ALL.add(this);
	}

	public static void setKeyState(KeyState keyState) {
		Hotkey.keyState = keyState;
	}

	/** Samples every hotkey. Called once per client tick, before anything reads one. */
	public static void updateAll() {
		long now = System.nanoTime();
		for (int i = 0; i < ALL.size(); i++) {
			ALL.get(i).update(now);
		}
	}

	/** True for the one tick in which a press was recognised. */
	public boolean pressed() {
		return this.pressed;
	}

	/** True while the key is held down. */
	public boolean held() {
		return this.down;
	}

	private void update(long now) {
		this.pressed = false;

		int code = this.option.code();
		if (code < 0) {
			this.down = false;
			this.releasing = false;
			return;
		}

		if (keyState.isDown(code)) {
			if (this.releasing) {
				this.releasing = false;
				Feinschliff.log().debug(this.option.key() + ": ignored a "
					+ millis(now - this.releasingSince) + " ms gap while the key was held");
			}
			if (!this.down) {
				this.down = true;
				this.downSince = now;
				this.pressed = true;
				Feinschliff.log().debug(this.option.key() + ": pressed");
			}
			return;
		}

		if (!this.down) {
			return;
		}

		if (!this.releasing) {
			this.releasing = true;
			this.releasingSince = now;
		}

		if (now - this.releasingSince >= this.releaseGrace()) {
			this.down = false;
			this.releasing = false;
			Feinschliff.log().debug(this.option.key() + ": released");
		}
	}

	/** A key too short-lived to have repeated is released the moment it reads so. */
	private long releaseGrace() {
		boolean couldRepeat = this.releasingSince - this.downSince >= REPEAT_POSSIBLE_AFTER_NANOS;
		return couldRepeat ? HELD_RELEASE_GRACE_NANOS : 0L;
	}

	private static long millis(long nanos) {
		return nanos / 1_000_000L;
	}
}
