package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import org.lwjgl.input.Keyboard;

/**
 * The keys a gesture is qualified with.
 *
 * <p>Polled rather than read off an event, for the same reason every other key
 * in this mod is: it must not take anything out of the vanilla event loops.
 * See Hotkey.
 *
 * <p>These are modifiers, not bindings, and they are deliberately not in the
 * config. The game's own controls screen does not own them either.
 */
public final class Modifiers {

	private Modifiers() {
	}

	public static boolean shift() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	public static boolean control() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
}
