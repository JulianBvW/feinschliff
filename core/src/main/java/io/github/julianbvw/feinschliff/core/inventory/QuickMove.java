package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Where a stack goes when it is sent away rather than carried.
 *
 * <p>Two passes over the targets, in this order: first onto stacks of the same
 * kind that still have room, then into empty slots. The second order is what
 * keeps a half stack of stone landing on the half stack of stone instead of
 * beside it.
 *
 * <p>Nothing here knows what an item is. The slots answer what fits where, and
 * this decides which of them to ask first.
 */
public final class QuickMove {

	/**
	 * The hotbar first. Whatever comes back out of a chest, a furnace or a
	 * crafting grid is usually wanted in hand next, and the hotbar is the part
	 * of the inventory that is still there once the menu closes.
	 */
	private static final SlotRole[] INTO_PLAYER = {SlotRole.HOTBAR, SlotRole.MAIN};
	private static final SlotRole[] INTO_OTHER = {SlotRole.OTHER};
	private static final SlotRole[] INTO_FURNACE = {SlotRole.FURNACE_INPUT, SlotRole.FURNACE_FUEL};
	private static final SlotRole[] INTO_ARMOUR_OR_MAIN = {SlotRole.ARMOUR, SlotRole.MAIN};
	private static final SlotRole[] INTO_ARMOUR_OR_HOTBAR = {SlotRole.ARMOUR, SlotRole.HOTBAR};

	private QuickMove() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_SHIFT_CLICK.on();
	}

	/**
	 * How many of the stack in {@code from} the targets would take, without
	 * moving anything.
	 *
	 * <p>Crafting asks this before it takes a result out, because taking one
	 * also spends the ingredients and there is nowhere safe to put a result
	 * back.
	 */
	public static int roomFor(Slots slots, int from) {
		return run(slots, from, false);
	}

	/** Moves as much as fits and answers how many items that was. */
	public static int move(Slots slots, int from) {
		return run(slots, from, true);
	}

	/**
	 * Where a stack out of this role should be offered, in order.
	 *
	 * <p>A player slot in front of a chest or a furnace has exactly one
	 * destination and no fallback: a full chest leaves the stack where it is
	 * rather than quietly shuffling it between hotbar and inventory, which is
	 * not what anyone pressing shift in front of a chest meant.
	 */
	private static SlotRole[] targets(Slots slots, SlotRole from) {
		if (from != SlotRole.HOTBAR && from != SlotRole.MAIN) {
			return INTO_PLAYER;
		}
		if (holds(slots, SlotRole.OTHER)) {
			return INTO_OTHER;
		}
		if (holds(slots, SlotRole.FURNACE_INPUT)) {
			return INTO_FURNACE;
		}
		return from == SlotRole.HOTBAR ? INTO_ARMOUR_OR_MAIN : INTO_ARMOUR_OR_HOTBAR;
	}

	private static boolean holds(Slots slots, SlotRole role) {
		for (int index = 0; index < slots.count(); index++) {
			if (slots.role(index) == role) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The one walk, run twice: once to answer and once to act. Both take the
	 * same route, so what {@link #roomFor} promises is what {@link #move}
	 * delivers.
	 */
	private static int run(Slots slots, int from, boolean apply) {
		SlotRole[] targets = targets(slots, slots.role(from));

		// Counted here rather than read back from the slot, so that the answer
		// pass and the acting pass can share every other line.
		int left = slots.size(from);
		int moved = 0;

		for (int pass = 0; pass < 2; pass++) {
			boolean ontoPartialStacks = pass == 0;

			for (int target = 0; target < targets.length; target++) {
				for (int to = 0; to < slots.count(); to++) {
					if (left <= 0) {
						return moved;
					}
					if (to == from || slots.role(to) != targets[target]) {
						continue;
					}

					int room;
					if (ontoPartialStacks) {
						if (slots.empty(to) || !slots.stackable(to, from)) {
							continue;
						}
						room = slots.capacity(to, from) - slots.size(to);
					} else {
						if (!slots.empty(to)) {
							continue;
						}
						room = slots.capacity(to, from);
					}

					int amount = room < left ? room : left;
					if (amount <= 0) {
						continue;
					}

					if (apply) {
						slots.move(from, to, amount);
					}
					left -= amount;
					moved += amount;
				}
			}
		}

		return moved;
	}
}
