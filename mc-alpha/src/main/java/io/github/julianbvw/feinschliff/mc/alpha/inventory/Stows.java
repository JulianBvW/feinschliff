package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.Stow;

/** The middle button with control held, from the click to the emptied pockets. */
public final class Stows {

	private static final int MIDDLE_BUTTON = 2;

	private Stows() {
	}

	/**
	 * Answers whether this click put anything away. A {@code false} leaves the
	 * click to whoever is next in line.
	 */
	public static boolean handled(List menuSlots, int mouseX, int mouseY, int button) {
		if (button != MIDDLE_BUTTON || !Stow.enabled() || !Modifiers.control()) {
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		try {
			Stow.into(slots);
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a quick stack could not be finished; it"
				+ " has been dropped and the game left alone", t);
		}
		return true;
	}
}
