package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/** The way into an open menu, and the one check every gesture in it shares. */
public final class Menus {

	private Menus() {
	}

	/**
	 * The slots of the open menu, or {@code null} when nothing should touch
	 * them: no game yet, no player, or a screen still holding the inventory of
	 * a player who has since died and been handed a new one.
	 */
	public static MenuSlots of(List menuSlots) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null || minecraft.player == null) {
			return null;
		}

		MenuSlots slots = new MenuSlots(menuSlots, minecraft.player.inventory);
		return slots.belongToThePlayer() ? slots : null;
	}

	/**
	 * The slot under the pointer, or {@code -1} when there is none.
	 *
	 * <p>For the gestures that arrive without a position of their own: a
	 * keypress says which key, never where. The pointer is read and scaled
	 * exactly the way the game scales it for a click.
	 */
	public static int under(MenuSlots slots) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null) {
			return -1;
		}

		Screen screen = minecraft.screen;
		if (screen == null || minecraft.width <= 0 || minecraft.height <= 0) {
			return -1;
		}

		int mouseX = Mouse.getX() * screen.width / minecraft.width;
		int mouseY = screen.height - Mouse.getY() * screen.height / minecraft.height - 1;
		return slots.under(mouseX, mouseY);
	}
}
