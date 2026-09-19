package io.github.julianbvw.feinschliff.mc.alpha.world;

import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.world.Sponge;

/**
 * Turns the water around a sponge into air.
 *
 * <p>This is the first place the mod writes a block rather than a number, and
 * it writes exactly one kind of change: water becomes air, inside a cube of
 * five, at positions whose chunk is already there. Air over water is nothing a
 * bucket could not have done one block at a time.
 *
 * <p>The water goes quietly, and that is the whole trick of it. Settled water
 * never stirs by itself -- it has no random tick, and the only thing that wakes
 * it is being told that a block beside it has changed. Announcing each removal
 * would therefore wake the whole pool, which would flow at the cube, be drunk,
 * and wake the pool again: a mill that never stops turning. Removing the water
 * without a word leaves everything outside the cube asleep, and one message to
 * the renderer at the end is all that is needed for the cube to look empty.
 *
 * <p>Nothing is suppressed beyond those removals. Breaking the sponge is still
 * the game's own {@code onRemoved}, which wakes all hundred and twenty-five
 * positions and lets the water back in.
 */
public final class Sponges {

	private Sponges() {
	}

	/** True when the sponge has drunk and the game has nothing left to do. */
	public static boolean soak(World world, int x, int y, int z) {
		if (!Sponge.soaksUp()) {
			return false;
		}

		int radius = Sponge.RADIUS;
		boolean drank = false;
		for (int px = x - radius; px <= x + radius; px++) {
			for (int py = y - radius; py <= y + radius; py++) {
				for (int pz = z - radius; pz <= z + radius; pz++) {
					drank |= drain(world, px, py, pz);
				}
			}
		}

		if (drank) {
			world.notifyRegionChanged(x - radius, y - radius, z - radius,
				x + radius, y + radius, z + radius);
		}

		return true;
	}

	/**
	 * The same again, after something beside the sponge has changed. The game
	 * only tells a block about its six touching neighbours, so water coming back
	 * from further out is not announced -- but it cannot come closer than that
	 * last block without announcing itself, and that is the moment this fires.
	 */
	public static void soakAgain(World world, int x, int y, int z) {
		if (Sponge.keepsDry()) {
			soak(world, x, y, z);
		}
	}

	/**
	 * Reading a block loads the chunk it is in, and loading a chunk that was
	 * never made makes it. A sponge two blocks from a chunk border reaches
	 * across, so every position is asked whether it is there before it is asked
	 * what it holds. The same test refuses anything above or below the world.
	 *
	 * <p>The quiet setter still writes the block, still runs the height map and
	 * both kinds of light, and still marks the chunk for saving. The one thing
	 * it leaves out is telling the six neighbours, which is exactly the thing
	 * worth leaving out here.
	 */
	private static boolean drain(World world, int x, int y, int z) {
		return world.isChunkLoaded(x, y, z)
			&& Sponge.isWater(world.getBlock(x, y, z))
			&& world.setBlockQuietly(x, y, z, 0);
	}
}
