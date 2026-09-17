package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.awt.Toolkit;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.inventory.Gather;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/** The double click, from recognising the second one to filling the hand. */
public final class Gathers {

	private static final int LEFT_BUTTON = 0;

	/**
	 * The name the desktop stores its own double click interval under. Asking
	 * it means the gesture takes exactly as long here as it does everywhere
	 * else on this machine, instead of asking everyone to click at one fixed
	 * speed.
	 */
	private static final String DESKTOP_INTERVAL = "awt.multiClickInterval";

	/** Used when the desktop has no answer, and as the lower end of one. */
	private static final long FALLBACK_MILLIS = 250L;
	private static final long LONGEST_MILLIS = 1000L;

	private static final int NO_SLOT = -1;

	/** Nought until the desktop has been asked, which happens once. */
	private static long interval;

	/**
	 * The click before this one. The screen is part of it because a slot index
	 * means nothing without the menu it counts in.
	 */
	private static Screen screen;
	private static int slot = NO_SLOT;
	private static long when;

	private Gathers() {
	}

	/**
	 * Answers whether this click was the second of a pair and has been dealt
	 * with. A {@code false} leaves the click to the game, untouched.
	 */
	public static boolean handled(List menuSlots, int mouseX, int mouseY, int button) {
		if (button != LEFT_BUTTON || !Gather.enabled()) {
			return false;
		}

		MenuSlots slots = Menus.of(menuSlots);
		if (slots == null) {
			return false;
		}

		Minecraft minecraft = FeinschliffClient.minecraft();
		long now = System.currentTimeMillis();
		int index = slots.under(mouseX, mouseY);

		boolean second = index != NO_SLOT && index == slot && minecraft.screen == screen
			&& now - when <= interval();

		screen = minecraft.screen;
		slot = index;
		when = now;

		// The first click of the pair is what filled the hand, and without a
		// full hand there is nothing to say what should be gathered.
		if (!second || slots.cursorSize() <= 0) {
			return false;
		}

		// A third click in the same place starts a new pair rather than
		// gathering again on top of what is already held.
		slot = NO_SLOT;

		try {
			Gather.into(slots);
		} catch (Throwable t) {
			// Screen.handleMouse has no net of its own, and an escape here
			// would be a crash report in the middle of a move.
			Feinschliff.log().error("a double click could not be finished; the"
				+ " click has been dropped and the game left alone", t);
		}
		return true;
	}
	/**
	 * How far apart two clicks may be and still be one gesture. A desktop that
	 * says nothing, or something absurd, is answered with the quarter second
	 * every other game uses.
	 */
	private static long interval() {
		if (interval != 0) {
			return interval;
		}

		interval = FALLBACK_MILLIS;
		try {
			Object property = Toolkit.getDefaultToolkit().getDesktopProperty(DESKTOP_INTERVAL);
			if (property instanceof Integer) {
				long desktop = ((Integer)property).intValue();
				if (desktop > interval && desktop <= LONGEST_MILLIS) {
					interval = desktop;
				}
			}
		} catch (Throwable t) {
			Feinschliff.log().warn("could not read the desktop double click interval ("
				+ t + "), using " + FALLBACK_MILLIS + " ms");
		}

		Feinschliff.log().debug("double click interval: " + interval + " ms");
		return interval;
	}
}
