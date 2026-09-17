package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.Spread;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.InventoryMenuScreenInvoker;

/**
 * Dragging a held stack across slots, from the press to the release.
 *
 * <p>The press is held back rather than let through, because at that moment
 * there is no telling a drag from a click -- and once the game has put the
 * stack down there is nothing left to spread. Until a second slot is touched
 * nothing has moved at all, so a press that turns out to be a click is still
 * the game's own, untouched.
 *
 * <p>From the second slot on the stack really is laid out, then and there, and
 * every further slot takes it back and lays it out again over one more. What
 * you see while dragging is therefore not a picture of the outcome, it is the
 * outcome -- which is also why letting go, changing your mind or dying halfway
 * through leaves exactly what is on screen.
 */
public final class Drags {

	private static final int LEFT_BUTTON = 0;
	private static final int RIGHT_BUTTON = 1;

	/** What the game passes as the button of a plain pointer movement. */
	private static final int MOVED = -1;

	private static final int NO_SLOT = -1;

	private static Screen screen;
	private static int button = MOVED;
	private static int pressX;
	private static int pressY;
	private static int[] visited;
	private static int[] placed;
	private static int count;

	/** What is being laid out, so that it can be told apart when taken back. */
	private static int kindId;
	private static int kindMetadata;

	private static boolean replaying;

	private Drags() {
	}

	/** True while a press is being handed back to the game. */
	public static boolean replaying() {
		return replaying;
	}

	/**
	 * Answers whether this press has been taken over. A {@code false} leaves it
	 * to the game, untouched.
	 */
	public static boolean startedOn(List menuSlots, int mouseX, int mouseY, int pressed) {
		if (pressed != LEFT_BUTTON && pressed != RIGHT_BUTTON || !Spread.enabled()) {
			return false;
		}

		// A second button during a gesture ends it. Nothing has moved, so the
		// new press is simply the game's again.
		if (button != MOVED) {
			forget();
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		// An empty hand has nothing to lay out, and everything the game does
		// with one it does on the press.
		if (slots == null || slots.cursorSize() <= 0) {
			return false;
		}

		int from = slots.under(mouseX, mouseY);
		if (from == NO_SLOT) {
			return false;
		}

		ItemStack held = slots.cursorItem();
		screen = FeinschliffClient.minecraft().screen;
		button = pressed;
		pressX = mouseX;
		pressY = mouseY;
		kindId = held.id;
		kindMetadata = held.metadata;
		visited = new int[slots.count()];
		placed = new int[slots.count()];
		visited[0] = from;
		count = 1;
		return true;
	}

	/**
	 * Every mouse event that is not a press: a movement while the button reads
	 * {@code -1}, a real release otherwise.
	 */
	public static void released(List menuSlots, int mouseX, int mouseY, int released) {
		if (button == MOVED) {
			return;
		}

		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null || minecraft.screen != screen) {
			forget();
			return;
		}

		if (released == MOVED) {
			moved(menuSlots, mouseX, mouseY);
			// A release the window never delivered, because it lost the
			// pointer while the button was down.
			if (!Mouse.isButtonDown(button)) {
				finish(menuSlots);
			}
			return;
		}

		if (released != button) {
			forget();
			return;
		}
		finish(menuSlots);
	}

	private static void moved(List menuSlots, int mouseX, int mouseY) {
		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			forget();
			return;
		}

		int over = slots.under(mouseX, mouseY);
		if (over == NO_SLOT || known(over)) {
			return;
		}

		// Everything goes back into the hand first, so the new slot is judged
		// against the menu as it was before the gesture started -- and so the
		// share is worked out from the whole stack rather than from whatever
		// is left of it.
		takeBack(slots);

		// Slots that cannot take what is held are not part of the line. A
		// gesture that only wobbled onto one stays a plain click.
		if (Spread.accepts(slots, over) && count < visited.length) {
			visited[count++] = over;
		}
		if (count > 1) {
			Spread.over(slots, visited, count, button == LEFT_BUTTON, placed);
		}
	}

	/** Undoes the last lay-out, as far as it is still there to undo. */
	private static void takeBack(MenuSlots slots) {
		for (int i = 0; i < count; i++) {
			int amount = placed[i];
			placed[i] = 0;
			if (amount <= 0) {
				continue;
			}

			// A furnace that has since smelted some of it, or anything else
			// that took a hand in, is followed rather than argued with.
			int slot = visited[i];
			if (!slots.holds(slot, kindId, kindMetadata)) {
				continue;
			}
			int there = slots.size(slot);
			if (amount > there) {
				amount = there;
			}
			if (amount > 0) {
				slots.toCursor(slot, amount);
			}
		}
	}

	private static void finish(List menuSlots) {
		Screen owner = screen;
		int pressed = button;
		int x = pressX;
		int y = pressY;
		int touched = count;
		forget();

		try {
			// Anything longer than a single slot is already lying where it
			// belongs; only a press that never became a gesture is still owed
			// to the game.
			if (touched <= 1) {
				replay(owner, x, y, pressed);
			}
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a drag could not be finished; the gesture"
				+ " has been dropped and the game left alone", t);
		}
	}

	/**
	 * Hands the press back to the game, at the place it happened rather than
	 * where the pointer has since wandered.
	 */
	private static void replay(Screen owner, int x, int y, int pressed) {
		replaying = true;
		try {
			((InventoryMenuScreenInvoker)owner).feinschliff$mouseClicked(x, y, pressed);
		} finally {
			replaying = false;
		}
	}

	private static boolean known(int slot) {
		for (int i = 0; i < count; i++) {
			if (visited[i] == slot) {
				return true;
			}
		}
		return false;
	}

	private static void forget() {
		screen = null;
		button = MOVED;
		visited = null;
		placed = null;
		count = 0;
	}
}
