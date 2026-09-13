package io.github.julianbvw.feinschliff.mc.alpha.adapter;

import java.util.Locale;

import org.lwjgl.input.Keyboard;

import io.github.julianbvw.feinschliff.core.Feinschliff;

/** Key name and key state lookups against LWJGL 2. */
public final class LwjglKeys {

	private LwjglKeys() {
	}

	/**
	 * Resolves an LWJGL 2 key name. Unknown names are reported and treated as
	 * unbound rather than as an error, so one typo cannot break startup.
	 */
	public static int resolve(String keyName) {
		if (keyName == null) {
			return -1;
		}

		String name = keyName.trim().toUpperCase(Locale.ROOT);
		if (name.isEmpty()) {
			return -1;
		}

		int code = Keyboard.getKeyIndex(name);
		if (code == Keyboard.KEY_NONE) {
			Feinschliff.log().warn("unknown key name '" + keyName + "', hotkey left unbound");
			return -1;
		}
		return code;
	}

	public static boolean isDown(int keyCode) {
		// Keyboard.isKeyDown polls state and does not consume events, so it
		// cannot interfere with the vanilla Keyboard.next() loops.
		return Keyboard.isKeyDown(keyCode);
	}
}
