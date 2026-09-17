package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import net.minecraft.client.Minecraft;

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
}
