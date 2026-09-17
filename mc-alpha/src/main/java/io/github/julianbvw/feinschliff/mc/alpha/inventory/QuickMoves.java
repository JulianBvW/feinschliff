package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.game.inventory.InventoryMenuSlot;
import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.QuickMove;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/** Shift-click, from the click to the last item moved. */
public final class QuickMoves {

	private static final int LEFT_BUTTON = 0;

	/**
	 * No slot of a crafting grid holds more than a stack, so this many rounds
	 * empties the fullest grid there can be. It is a backstop, not the reason
	 * the loop ends.
	 */
	private static final int MAX_CRAFTS = 64;

	private QuickMoves() {
	}

	/**
	 * Answers whether this click was a quick move and has been dealt with. A
	 * {@code false} leaves the click to the game, untouched.
	 */
	public static boolean handled(List menuSlots, int mouseX, int mouseY, int button) {
		if (button != LEFT_BUTTON || !QuickMove.enabled() || !Modifiers.shift()) {
			return false;
		}

		// What the hand is holding stays in the hand: a quick move is about the
		// slot that was clicked and nothing else. An empty slot falls through
		// below, so a held stack can still be put down with shift held.
		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		Minecraft minecraft = FeinschliffClient.minecraft();
		int from = slots.under(mouseX, mouseY);
		if (from < 0 || slots.empty(from)) {
			return false;
		}

		try {
			if (slots.role(from) == SlotRole.RESULT) {
				craftAll(minecraft, slots, from);
			} else {
				QuickMove.move(slots, from);
			}
			// Keeping the button down and moving on carries this across the
			// next slots.
			Sweeps.startedOn(slots, from);
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a shift-click could not be finished; the"
				+ " click has been dropped and the game left alone", t);
		}
		return true;
	}

	/**
	 * Takes the result out again and again, for as long as the grid keeps
	 * producing and there is room for the whole of what it produces.
	 *
	 * <p>The order is what makes this safe. Taking a result also spends the
	 * ingredients, and there is nowhere to put one back: the result slot is a
	 * bare field that the next recipe overwrites, and a chest or furnace slot
	 * quietly trims anything above a stack. So nothing is taken until it is
	 * known to fit, whole, right now.
	 */
	private static void craftAll(Minecraft minecraft, MenuSlots slots, int result) {
		Screen screen = minecraft.screen;
		PlayerInventory inventory = minecraft.player.inventory;
		InventoryMenuSlot slot = slots.slot(result);

		for (int crafted = 0; crafted < MAX_CRAFTS; crafted++) {
			// Dying takes the screen away and hands out a new inventory while
			// these slots still point at the old one.
			if (minecraft.screen != screen || minecraft.player == null
					|| minecraft.player.inventory != inventory) {
				return;
			}

			ItemStack made = slot.getItem();
			if (made == null || QuickMove.roomFor(slots, result) < made.size) {
				return;
			}
			if (QuickMove.move(slots, result) <= 0) {
				// Nothing happened, so nothing will happen next time either.
				return;
			}

			// Exactly once per result taken: this is what eats the ingredients.
			slot.onItemRemoved();
		}
	}
}
