package io.github.julianbvw.feinschliff.mc.alpha.block;

import net.minecraft.block.Block;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.block.Placement;

/**
 * Turns a freshly placed block towards the player.
 *
 * <p>This runs after every block the player puts down, so everything the game
 * has no facing for has to fall straight through it. The two that do are
 * picked out by their class rather than by their id, because that is the thing
 * about them that does not move between versions.
 *
 * <p>What the player held is not always what stands there afterwards: a
 * staircase set under something solid turns into the block it is made of
 * before this runs, and that block has no facing to be given. So the position
 * is asked what it holds before anything is written to it.
 *
 * <p>The write is the same one a torch makes when it settles against a wall:
 * metadata only, on a block that was placed this very tick and whose chunk is
 * therefore as loaded as a chunk gets. The renderer has already been told the
 * block changed and rebuilds the chunk at the end of the frame, by which time
 * the facing is in.
 */
public final class Placements {

	private Placements() {
	}

	/** Gives a staircase or a furnace the facing it was placed with. */
	public static void orient(World world, int x, int y, int z, Block block, PlayerEntity player) {
		if (!Placement.facesYou() || world.getBlock(x, y, z) != block.id) {
			return;
		}

		if (block instanceof StairsBlock) {
			world.setBlockMetadata(x, y, z, Placement.stairsFacing(player.yaw));
		} else if (block instanceof FurnaceBlock) {
			world.setBlockMetadata(x, y, z, Placement.frontFacing(player.yaw));
		}
	}
}
