package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Where a stack fetched out of the inventory should land on the hotbar.
 *
 * <p>The nearest free place going up from the one in hand, and the one in hand
 * when every place is taken. Starting where the hand already is keeps the
 * stack close to it, and going round rather than stopping means a full hotbar
 * still answers -- by trading, which is the only thing left to do.
 */
public final class PickBlock {

	private PickBlock() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_PICK_BLOCK.on();
	}

	/**
	 * @param free      one flag per hotbar place, true where nothing is
	 * @param selected  the place in hand
	 */
	public static int place(boolean[] free, int selected) {
		if (free.length == 0) {
			return selected;
		}

		int from = selected < 0 || selected >= free.length ? 0 : selected;
		for (int step = 0; step < free.length; step++) {
			int place = (from + step) % free.length;
			if (free[place]) {
				return place;
			}
		}
		return from;
	}
}
