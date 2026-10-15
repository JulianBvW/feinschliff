package io.github.julianbvw.feinschliff.mc.alpha.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.block.LeafDecay;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.LeavesBlockInvoker;

/**
 * Looks around a leaf for a trunk and lets it go when there is none.
 *
 * <p>The letting go is the game's own, reached through {@link
 * LeavesBlockInvoker}: it throws what the leaf drops and then turns the leaf
 * into air. A leaf gives a sapling one time in twenty, and it is the same
 * method rolling it as before, so a wood yields at least as many saplings as it
 * did -- more, in fact, because leaves the game used to leave hanging now come
 * down and roll at all.
 *
 * <p>There is exactly one {@code LeavesBlock} in this version, held in {@link
 * Block#LEAVES}, which is the leaf the game is ticking whichever leaf that is.
 */
public final class Leaves {

	private Leaves() {
	}

	/** True when the leaf has gone and the game has nothing left to do here. */
	public static boolean letGo(World world, int x, int y, int z) {
		if (!LeafDecay.decays() || hasTrunkNear(world, x, y, z)) {
			return false;
		}

		((LeavesBlockInvoker)Block.LEAVES).feinschliff$letGo(world, x, y, z);
		LeafDecay.letGo();
		return true;
	}

	/**
	 * Whether a trunk stands inside the cube -- or whether part of that cube is
	 * somewhere we may not look, which counts the same way.
	 *
	 * <p>Reading a block loads the chunk it is in, and loading a chunk that was
	 * never made makes it. A leaf three blocks from a chunk border reaches
	 * across, and the outer ring of the chunks being ticked has neighbours that
	 * are not there. Worse than the making: a chunk arriving evicts whichever
	 * of the thirty-two by thirty-two slots it lands in, writing that chunk out
	 * and unloading it -- and the loop that is ticking this very leaf is
	 * holding one of those chunks in a local. So the whole cube is asked
	 * whether it is there before any of it is asked what it holds, with a test
	 * that only looks the chunks up and never loads them.
	 *
	 * <p>Not being able to look counts as a trunk, never as none. The other way
	 * round would take leaves away along chunk borders for no reason but that
	 * the trunk holding them up was out of sight. The game's own rule decides
	 * those until the chunk arrives.
	 *
	 * <p>A position above or below the world is free to ask: the getter answers
	 * zero for those before it goes looking for a chunk at all.
	 */
	private static boolean hasTrunkNear(World world, int x, int y, int z) {
		int radius = LeafDecay.RADIUS;
		if (!world.isAreaLoaded(x - radius, y - radius, z - radius,
				x + radius, y + radius, z + radius)) {
			return true;
		}

		int[] offsets = LeafDecay.OFFSETS;
		for (int i = 0; i < offsets.length; i += 3) {
			int block = world.getBlock(x + offsets[i], y + offsets[i + 1], z + offsets[i + 2]);
			if (block == LeafDecay.LOG) {
				return true;
			}
		}

		return false;
	}
}
