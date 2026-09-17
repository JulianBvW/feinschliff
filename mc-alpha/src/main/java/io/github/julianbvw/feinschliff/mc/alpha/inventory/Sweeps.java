package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.inventory.QuickMove;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/**
 * Keeping shift held and drawing across slots, sending every one of them away.
 *
 * <p>Nothing is put off here the way a drag puts off its press: a shift-click
 * has already acted by the time the pointer moves on, so each slot the line
 * reaches is simply another shift-click. Which is also why a slot is only ever
 * visited once.
 */
public final class Sweeps {

	private static final int LEFT_BUTTON = 0;

	private static final int NO_SLOT = -1;

	/** What the game passes as the button of a plain pointer movement. */
	private static final int MOVED = -1;

	private static Screen screen;
	private static int[] done;
	private static int count;

	private Sweeps() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_SHIFT_DRAG.on();
	}

	/** Begins a line at the slot a shift-click has just dealt with. */
	public static void startedOn(MenuSlots slots, int slot) {
		if (!enabled()) {
			return;
		}

		screen = FeinschliffClient.minecraft().screen;
		done = new int[slots.count()];
		done[0] = slot;
		count = 1;
	}

	/**
	 * Every mouse event that is not a press: a movement while the button reads
	 * {@code -1}, a real release otherwise.
	 */
	public static void released(List menuSlots, int mouseX, int mouseY, int released) {
		if (screen == null) {
			return;
		}

		Minecraft minecraft = FeinschliffClient.minecraft();
		// Letting go of either the button or shift ends the line, and so does
		// a release the window never delivered.
		if (minecraft == null || minecraft.screen != screen || released != MOVED
				|| !Mouse.isButtonDown(LEFT_BUTTON) || !QuickMoves.shiftDown()) {
			forget();
			return;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			forget();
			return;
		}

		int over = slots.under(mouseX, mouseY);
		if (over == NO_SLOT || known(over) || slots.empty(over)) {
			return;
		}
		// A line that happens to cross the result would craft the grid empty
		// on its way past. Clicking it is how that is asked for.
		if (slots.role(over) == SlotRole.RESULT) {
			return;
		}

		if (count < done.length) {
			done[count++] = over;
		}

		try {
			QuickMove.move(slots, over);
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a slot on a shift line could not be sent"
				+ " away; the line has been dropped and the game left alone", t);
			forget();
		}
	}

	private static boolean known(int slot) {
		for (int i = 0; i < count; i++) {
			if (done[i] == slot) {
				return true;
			}
		}
		return false;
	}

	private static void forget() {
		screen = null;
		done = null;
		count = 0;
	}
}
