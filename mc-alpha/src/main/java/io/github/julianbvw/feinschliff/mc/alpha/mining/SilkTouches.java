package io.github.julianbvw.feinschliff.mc.alpha.mining;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.mining.SilkTouch;

/**
 * Lays the block itself on the ground in place of what it breaks into.
 */
public final class SilkTouches {

	/** How far from the centre of the block a drop may land, as the game does it. */
	private static final float SPREAD = 0.7F;

	/** Ticks before a dropped stack may be picked up, as the game does it. */
	private static final int PICK_UP_DELAY = 10;

	private SilkTouches() {
	}

	/**
	 * Drops the block whole, if a golden tool did the breaking and this is one
	 * of the blocks that would otherwise come apart.
	 *
	 * @param tool what is held, which is null for a bare hand and may be a tool
	 *             that has just broken on this very block
	 * @return true when the block has been dealt with and the game has nothing
	 *         left to drop
	 */
	public static boolean handled(World world, int x, int y, int z, Block block, ItemStack tool) {
		if (tool == null || !Mining.golden(Item.BY_ID[tool.id])) {
			return false;
		}

		int drop = SilkTouch.drop(block.id);
		if (drop == 0) {
			return false;
		}

		// The block is gone either way. Only the drop is skipped on a client
		// that is not the one keeping the world, which is what the game's own
		// dropping does.
		if (!world.isMultiplayer) {
			ItemEntity itemEntity = new ItemEntity(
				world, x + offset(world), y + offset(world), z + offset(world), new ItemStack(drop));
			itemEntity.pickUpDelay = PICK_UP_DELAY;
			world.addEntity(itemEntity);
		}

		return true;
	}

	private static double offset(World world) {
		return world.random.nextFloat() * SPREAD + (1.0F - SPREAD) * 0.5;
	}
}
