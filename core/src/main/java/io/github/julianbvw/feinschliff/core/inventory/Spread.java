package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Laying a held stack out over the slots a gesture touched.
 *
 * <p>Evenly means every slot that can take something gets the same share and
 * whatever does not divide stays in the hand. One each means exactly that,
 * which is what the right button is for.
 *
 * <p>The slots are served in the order they were touched, not in the order
 * they sit in the menu: the hand drew a line, and the line is the answer.
 */
public final class Spread {

	private Spread() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_DRAG.on();
	}

	/** Whether a slot could take any of what the hand is holding. */
	public static boolean accepts(Slots slots, int index) {
		if (!slots.empty(index) && !slots.cursorStackable(index)) {
			return false;
		}
		return slots.cursorCapacity(index) - slots.size(index) > 0;
	}

	/**
	 * Puts the held stack down over the targets and answers how much was laid
	 * out. {@code placed} is filled with what each target got, which is what a
	 * gesture still in progress needs in order to take it back and lay it out
	 * again over one slot more.
	 */
	public static int over(Slots slots, int[] targets, int count, boolean evenly, int[] placed) {
		int eligible = 0;
		for (int i = 0; i < count; i++) {
			if (accepts(slots, targets[i])) {
				eligible++;
			}
		}
		if (eligible == 0) {
			for (int i = 0; i < count; i++) {
				placed[i] = 0;
			}
			return 0;
		}

		// A share of nothing would be a gesture that does nothing, which is
		// never what someone dragging over four slots with three items meant.
		// The first three get one each and the fourth gets nothing.
		int share = evenly ? slots.cursorSize() / eligible : 1;
		if (share < 1) {
			share = 1;
		}

		int laidOut = 0;
		for (int i = 0; i < count; i++) {
			placed[i] = 0;

			int held = slots.cursorSize();
			if (held <= 0) {
				continue;
			}

			int to = targets[i];
			if (!accepts(slots, to)) {
				continue;
			}

			int room = slots.cursorCapacity(to) - slots.size(to);
			int amount = share < room ? share : room;
			if (amount > held) {
				amount = held;
			}
			if (amount <= 0) {
				continue;
			}

			slots.fromCursor(to, amount);
			placed[i] = amount;
			laidOut += amount;
		}

		return laidOut;
	}
}
