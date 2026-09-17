package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.game.inventory.InventoryMenuSlot;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.Sort;
import io.github.julianbvw.feinschliff.core.inventory.SortOrder;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;

/** The middle button, from the click to the tidied row. */
public final class Sorts {

	private static final int MIDDLE_BUTTON = 2;

	private static final int NO_SLOT = -1;

	private Sorts() {
	}

	/**
	 * Answers whether this click sorted something. A {@code false} leaves the
	 * click to the game, which does nothing with this button in a menu.
	 */
	public static boolean handled(List menuSlots, int mouseX, int mouseY, int button) {
		if (button != MIDDLE_BUTTON || !Sort.enabled()) {
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		int under = slots.under(mouseX, mouseY);
		if (under == NO_SLOT) {
			return false;
		}

		// A chest sorts as one row, and so do the three rows above the hotbar.
		// The hotbar is arranged by hand -- sword here, pickaxe there -- and a
		// sort would undo that every time. Armour, furnace and grid have an
		// order of their own that means something.
		SlotRole role = slots.role(under);
		if (role != SlotRole.OTHER && role != SlotRole.MAIN) {
			return false;
		}

		try {
			sort(slots, role, shiftDown() ? SortOrder.COUNT : SortOrder.ID);
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a rewrite.
			Feinschliff.log().error("a sort could not be finished; it has been"
				+ " dropped and the game left alone", t);
		}
		return true;
	}

	private static void sort(MenuSlots slots, SlotRole role, SortOrder order) {
		int[] region = new int[slots.count()];
		int count = 0;
		for (int index = 0; index < slots.count(); index++) {
			if (slots.role(index) == role) {
				region[count++] = index;
			}
		}
		if (count < 2) {
			return;
		}

		int[] ids = new int[count];
		int[] metas = new int[count];
		int[] sizes = new int[count];
		int[] maxSizes = new int[count];

		for (int i = 0; i < count; i++) {
			InventoryMenuSlot slot = slots.slot(region[i]);
			ItemStack stack = slot.getItem();
			if (stack == null) {
				continue;
			}
			// An id with no item behind it comes out of a hand-edited save.
			// How much of it fits in a stack is then anybody's guess, and
			// guessing is the one way this could make items out of nothing.
			if (stack.getItem() == null) {
				Feinschliff.log().warn("not sorting: slot " + region[i] + " holds item id "
					+ stack.id + ", which this version has nothing for");
				return;
			}

			ids[i] = stack.id;
			metas[i] = stack.metadata;
			sizes[i] = stack.size;
			int perStack = stack.getMaxSize();
			int perSlot = slot.inventory.getMaxStackSize();
			maxSizes[i] = perStack < perSlot ? perStack : perSlot;
		}

		int[][] plan = Sort.plan(ids, metas, sizes, maxSizes, order);
		if (plan.length > count) {
			throw new IllegalStateException("a sort of " + count + " slots came back as "
				+ plan.length + " stacks");
		}

		for (int i = 0; i < count; i++) {
			InventoryMenuSlot slot = slots.slot(region[i]);
			// A stack in this version is its id, its size and the number
			// beside it and nothing else, so one can be built again from
			// those three without losing anything. setItem marks the slot,
			// which is the only thing that ever tells a furnace or a chest it
			// has changed.
			slot.setItem(i < plan.length ? new ItemStack(plan[i][0], plan[i][2], plan[i][1]) : null);
		}
	}

	private static boolean shiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
