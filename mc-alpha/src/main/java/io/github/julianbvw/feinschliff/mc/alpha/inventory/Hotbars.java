package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Keyboard;


import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;
import io.github.julianbvw.feinschliff.core.inventory.Swap;

/** The number keys over a slot, from the keypress to the exchanged stacks. */
public final class Hotbars {

	/**
	 * The game reads these the same way, as raw codes counting up from the
	 * one: they are not among the ten bindings it lets anyone change.
	 */
	private static final int FIRST_KEY = Keyboard.KEY_1;

	private static final int NO_SLOT = -1;

	private Hotbars() {
	}

	/**
	 * Answers whether this keypress moved something. A {@code false} leaves
	 * the key to the game, which closes the menu on two of them and ignores
	 * the rest.
	 */
	public static boolean handled(List menuSlots, int key) {
		int place = key - FIRST_KEY;
		if (place < 0 || place >= Swap.places() || !Swap.enabled()) {
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		int under = Menus.under(slots);
		if (under == NO_SLOT) {
			return false;
		}

		try {
			boolean result = slots.role(under) == SlotRole.RESULT;
			if (Swap.into(slots, under, place) && result) {
				// Exactly once per result taken: this is what eats the
				// ingredients.
				slots.slot(under).onItemRemoved();
			}
		} catch (Throwable t) {
			// Screen.handleKeyboard has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a number key could not be finished; it has"
				+ " been dropped and the game left alone", t);
		}
		return true;
	}

}
