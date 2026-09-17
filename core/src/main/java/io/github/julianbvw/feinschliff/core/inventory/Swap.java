package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Putting what is under the pointer on a numbered place of the hotbar.
 *
 * <p>A number key means one slot and no other, so the crafting grid is a
 * place like any other here -- unlike a stack sent away, which has no
 * sensible square to land on.
 *
 * <p>Whether that means moving or exchanging is decided by what is already
 * there, and an exchange only happens when <em>both</em> sides would hold what
 * the other has. That is not a nicety: a crafting result and a furnace output
 * hand things out and take nothing back, so a swap with either of them would
 * put an item somewhere it can never be got out of. Where an exchange is
 * refused, nothing at all happens.
 */
public final class Swap {

	/** The hotbar is nine places wide in every version this has to cross. */
	private static final int PLACES = 9;

	private Swap() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_HOTBAR_KEYS.on();
	}

	public static int places() {
		return PLACES;
	}

	/**
	 * Acts on the slot under the pointer and the numbered place, and answers
	 * whether anything happened.
	 */
	public static boolean into(Slots slots, int from, int place) {
		int to = hotbar(slots, place);
		if (to < 0 || to == from) {
			return false;
		}

		boolean nothingUnder = slots.empty(from);
		if (nothingUnder && slots.empty(to)) {
			return false;
		}

		if (slots.empty(to)) {
			return give(slots, from, to);
		}
		if (nothingUnder) {
			// The other way round, and the place under the pointer has to be
			// willing to take it -- pointing at a crafting result is not a way
			// of putting something into one.
			return give(slots, to, from);
		}

		if (slots.capacityPointedAt(to, from) < slots.size(from)) {
			return false;
		}
		if (slots.capacityPointedAt(from, to) < slots.size(to)) {
			return false;
		}

		slots.swap(from, to);
		return true;
	}

	/** Whole or not at all: half a stack on a numbered key is nobody's idea of one. */
	private static boolean give(Slots slots, int from, int to) {
		int size = slots.size(from);
		if (slots.capacityPointedAt(to, from) < size) {
			return false;
		}
		slots.move(from, to, size);
		return true;
	}

	/**
	 * Where the numbered place sits in this menu. Every screen lays its hotbar
	 * out in order, so the nth slot of that role is the nth place -- counted
	 * rather than worked out from an index.
	 */
	private static int hotbar(Slots slots, int place) {
		int seen = 0;
		for (int index = 0; index < slots.count(); index++) {
			if (slots.role(index) != SlotRole.HOTBAR) {
				continue;
			}
			if (seen == place) {
				return index;
			}
			seen++;
		}
		return -1;
	}
}
