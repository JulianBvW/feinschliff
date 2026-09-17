package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Putting away everything the other side has already started on.
 *
 * <p>What the chest has never held stays where it is. That one rule is the
 * whole difference between tidying up and emptying your pockets into the first
 * box you pass, and it is what makes this safe to press without looking.
 */
public final class Stow {

	private Stow() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_QUICK_STACK.on();
	}

	/**
	 * Sends every stack of the inventory that the other side already has some
	 * of, and answers how many items moved.
	 *
	 * <p>The hotbar is left out, for the same reason it is left out of
	 * sorting: what was put within reach on purpose should not disappear into
	 * a box at the press of a key.
	 */
	public static int into(Slots slots) {
		int moved = 0;

		for (int from = 0; from < slots.count(); from++) {
			if (slots.role(from) != SlotRole.MAIN || slots.empty(from)) {
				continue;
			}
			if (!alreadyThere(slots, from)) {
				continue;
			}
			moved += QuickMove.move(slots, from);
		}

		return moved;
	}

	private static boolean alreadyThere(Slots slots, int from) {
		for (int index = 0; index < slots.count(); index++) {
			if (slots.role(index) == SlotRole.OTHER && !slots.empty(index)
					&& slots.stackable(index, from)) {
				return true;
			}
		}
		return false;
	}
}
