package io.github.julianbvw.feinschliff.mc.alpha.world;

import net.minecraft.block.Block;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.world.DungeonLoot;

/**
 * Hands the core the world and the chest as numbers, and builds the stack it
 * asks for.
 *
 * <p>A chest knows where it stands: the chunk fills in its world and its three
 * coordinates before anything is put into it.
 */
public final class DungeonChests {

	private DungeonChests() {
	}

	/** What goes into that slot: the sponge, or what the chest rolled. */
	public static ItemStack loot(ChestBlockEntity chest, int slot, ItemStack rolled) {
		if (DungeonLoot.sponge(chest.world.seed, chest.x, chest.y, chest.z, slot, rolled.id)) {
			return new ItemStack(Block.SPONGE);
		}
		return rolled;
	}
}
