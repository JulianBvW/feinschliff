package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.inventory.PickBlock;

/** Pick block, for the part of the inventory the game does not reach. */
public final class PickBlocks {

	private static final int HOTBAR_SIZE = 9;

	private static final int NOT_CARRIED = -1;

	private PickBlocks() {
	}

	/**
	 * Answers whether the stack has been brought within reach. A {@code false}
	 * leaves the game to its own answer, which is right in both other cases:
	 * the stack is already on the hotbar, or it is not carried at all.
	 */
	public static boolean handled(PlayerInventory inventory, int id) {
		if (!PickBlock.enabled()) {
			return false;
		}

		int found = carriedAt(inventory, id);
		if (found < HOTBAR_SIZE) {
			return false;
		}

		boolean[] free = new boolean[HOTBAR_SIZE];
		for (int place = 0; place < HOTBAR_SIZE; place++) {
			free[place] = inventory.items[place] == null;
		}

		// A trade, not a copy: whatever was on that place goes where the stack
		// came from, so nothing is created and nothing is lost.
		int place = PickBlock.place(free, inventory.selectedSlot);
		ItemStack held = inventory.items[place];
		inventory.items[place] = inventory.items[found];
		inventory.items[found] = held;
		inventory.selectedSlot = place;
		return true;
	}

	/**
	 * The first slot carrying that id, in the order the game itself searches.
	 * Ids alone, without the number beside them -- the same comparison the
	 * game makes, and the one that matters here, since pick block is asking
	 * about a block and not about a particular one.
	 */
	private static int carriedAt(PlayerInventory inventory, int id) {
		// Read, never assumed: this array is one longer before a player has
		// been saved than after.
		for (int slot = 0; slot < inventory.items.length; slot++) {
			ItemStack stack = inventory.items[slot];
			if (stack != null && stack.id == id) {
				return slot;
			}
		}
		return NOT_CARRIED;
	}
}
