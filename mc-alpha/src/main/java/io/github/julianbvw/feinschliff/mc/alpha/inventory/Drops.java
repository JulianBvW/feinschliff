package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.inventory.Drop;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

/** The drop key, and how much of the stack in hand goes with it. */
public final class Drops {

	private Drops() {
	}

	/** Answers the number the game should hand to its own removeItem. */
	public static int amount(int vanilla) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null || minecraft.player == null) {
			return vanilla;
		}

		ItemStack held = minecraft.player.inventory.getSelectedItem();
		return Drop.amount(Modifiers.control(), held == null ? 0 : held.size);
	}
}
