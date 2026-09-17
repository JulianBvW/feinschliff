package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Mouse;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.inventory.QuickMove;
import io.github.julianbvw.feinschliff.core.inventory.SlotRole;

/**
 * The wheel over a slot, one item at a time.
 *
 * <p>One turn is one item, however far the wheel was pushed: a fast spin makes
 * several events of its own, and a small nudge should hand over a small
 * number. Two sticks for a pickaxe out of a stack of sixty-four is the whole
 * point of it.
 */
public final class Scrolls {

	/** What the game passes as the button of an event that is not a press. */
	private static final int MOVED = -1;

	private static final int NO_SLOT = -1;

	private static final int ONE = 1;

	private Scrolls() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_SCROLL.on();
	}

	/**
	 * Reads the wheel out of the event the game is handing over. It is still
	 * the current one here, because this runs inside the loop that drains
	 * them.
	 */
	public static void released(List menuSlots, int mouseX, int mouseY, int button) {
		if (button != MOVED || !enabled()) {
			return;
		}

		int wheel = Mouse.getEventDWheel();
		if (wheel == 0) {
			return;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return;
		}

		int over = slots.under(mouseX, mouseY);
		if (over == NO_SLOT || slots.empty(over)) {
			return;
		}
		// A result is made, not moved: it comes out whole and it costs
		// ingredients. Taking one at a time out of it is neither.
		if (slots.role(over) == SlotRole.RESULT) {
			return;
		}

		try {
			if (wheel < 0) {
				QuickMove.move(slots, over, ONE);
			} else {
				QuickMove.pull(slots, over, ONE);
			}
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a turn of the wheel could not be finished;"
				+ " it has been dropped and the game left alone", t);
		}
	}
}
