package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Filling the hand with everything of the same kind the menu has.
 *
 * <p>Two passes over the whole menu, in this order: first the stacks that are
 * not full, then the rest. Taking the part-used ones first is what turns a
 * shelf of leftovers into one stack instead of moving a full stack around and
 * leaving the leftovers where they were.
 *
 * <p>The hand only ever grows here. No slot is written to, which is why this
 * needs no room anywhere and can stop at any point without leaving anything
 * half done.
 */
public final class Gather {

	private Gather() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_DOUBLE_CLICK.on();
	}

	/** Takes as much as the hand can still hold and answers how many that was. */
	public static int into(Slots slots) {
		// Empty hand: nothing to match against, and nowhere to put it either.
		int room = slots.cursorMax() - slots.cursorSize();
		int taken = 0;

		for (int pass = 0; pass < 2; pass++) {
			boolean partialStacksOnly = pass == 0;

			for (int from = 0; from < slots.count(); from++) {
				if (room <= 0) {
					return taken;
				}
				if (slots.empty(from) || !slots.cursorStackable(from)) {
					continue;
				}
				// Taking a result is what pays for it: it spends the
				// ingredients, and a gesture meant to tidy up must not craft.
				if (slots.role(from) == SlotRole.RESULT) {
					continue;
				}

				int size = slots.size(from);
				if (partialStacksOnly && size >= slots.cursorMax()) {
					continue;
				}

				int amount = size < room ? size : room;
				slots.toCursor(from, amount);
				room -= amount;
				taken += amount;
			}
		}

		return taken;
	}
}
