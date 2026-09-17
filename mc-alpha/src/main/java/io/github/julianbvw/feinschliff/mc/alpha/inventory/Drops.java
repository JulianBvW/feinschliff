package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.game.inventory.InventoryMenuSlot;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.inventory.Drop;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/** The drop key: how much it throws in the world, and that it works in a menu at all. */
public final class Drops {

	private static final int NO_SLOT = -1;

	private Drops() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_DROP_FROM_MENU.on();
	}

	/**
	 * How many the game should hand to its own removeItem when the drop key is
	 * pressed in the world.
	 */
	public static int amount(int vanilla) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null || minecraft.player == null) {
			return vanilla;
		}

		ItemStack held = minecraft.player.inventory.getSelectedItem();
		return Drop.amount(Modifiers.control(), held == null ? 0 : held.size);
	}

	/**
	 * Answers whether this keypress threw something out of the menu. A
	 * {@code false} leaves the key to the game.
	 *
	 * <p>The gesture is about the slot under the pointer and nothing else, so
	 * whatever the hand is holding stays in it -- the same rule a shift-click
	 * follows. Putting a held stack down is what the world outside the window
	 * is for.
	 */
	public static boolean handled(List menuSlots, int key) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null || minecraft.player == null || !enabled()) {
			return false;
		}
		// The binding, not a fixed key: this one belongs to the game's own
		// controls screen and the player may have moved it.
		if (key != minecraft.options.dropKey.keyCode) {
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		int under = Menus.under(slots);
		if (under == NO_SLOT || slots.empty(under)) {
			return false;
		}

		try {
			throwOut(minecraft, slots, under);
		} catch (Throwable t) {
			// Screen.handleKeyboard has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a stack could not be thrown out of the"
				+ " menu; the key has been dropped and the game left alone", t);
		}
		return true;
	}

	private static void throwOut(Minecraft minecraft, MenuSlots slots, int index) {
		InventoryMenuSlot slot = slots.slot(index);
		ItemStack stack = slot.getItem();

		// A result is made rather than held: the inventory behind it hands
		// over the whole stack whatever is asked for, and one keypress is one
		// craft.
		boolean result = slots.role(index) == SlotRole.RESULT;
		int amount = result ? stack.size : Drop.amount(Modifiers.control(), stack.size);

		ItemStack taken = slot.inventory.removeItem(slot.id, amount);
		if (taken == null) {
			return;
		}
		if (taken.size > amount) {
			slot.setItem(taken.split(taken.size - amount));
		}
		// Vanilla marks the clicked slot once at the end of every click, and a
		// key that ends here never reaches that line.
		slot.markDirty();

		if (result) {
			// Exactly once per result taken: this is what eats the ingredients.
			slot.onItemRemoved();
		}

		// The same throw the drop key makes in the world, in the direction the
		// player is looking.
		minecraft.player.dropItem(taken, false);
	}

}
