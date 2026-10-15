package io.github.julianbvw.feinschliff.core.block;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Whether a leaf still has a tree to belong to.
 *
 * <p>This version answers that with a number kept in the leaf's own metadata:
 * sixteen beside a trunk, one less with every step, and below ten the leaf is
 * gone. The number travels from leaf to leaf, which is what makes it wrong --
 * fell a tree in a wood and its crown is still within six steps of the next
 * tree's trunk, through the leaves the two crowns share, so it hangs there for
 * good. A solid block underneath counts as a trunk as well, and leaves are
 * solid, so a stack of leaves also holds itself up.
 *
 * <p>The rule here asks the question directly instead: is there a trunk inside
 * the cube of seven around this leaf. Nothing is remembered between asks and
 * nothing travels, so neither of those two things can happen.
 *
 * <p>Three in every direction is the shape this wants, not a sphere of the same
 * size. A big oak hangs its clumps of leaves above and beside the end of a
 * branch, so it needs reach along the diagonal -- which a cube has, 5.2 blocks
 * into its corner. The next tree in a wood stands flat alongside, so it must
 * not be reached -- which is the cube's three blocks along an axis. A sphere
 * reaches the same distance everywhere and cannot have both.
 */
public final class LeafDecay {

	/** Three in every direction, so seven by seven by seven around the leaf. */
	public static final int RADIUS = 3;

	/**
	 * A trunk. An id rather than a material, because a material is a game type
	 * and has no place on this side.
	 */
	public static final int LOG = 17;

	/**
	 * Every position of that cube except its middle, as {@code dx, dy, dz} laid
	 * flat one after another, nearest first. Searched in this order a leaf on a
	 * tree meets its trunk within the first few steps and the rest of the cube
	 * is never read; only a leaf that is about to go pays for all of it, once.
	 */
	public static final int[] OFFSETS;

	/** How many leaves go between two lines in the log. */
	private static final int REPORT_EVERY = 100;

	private static int gone;

	static {
		int side = 2 * RADIUS + 1;
		int[] offsets = new int[(side * side * side - 1) * 3];
		int at = 0;

		// Ordered by distance by walking the distances rather than sorting: the
		// squared distance is a small integer, so the shells can simply be
		// collected one after another.
		int corner = 3 * RADIUS * RADIUS;
		for (int squared = 1; squared <= corner; squared++) {
			for (int dx = -RADIUS; dx <= RADIUS; dx++) {
				for (int dy = -RADIUS; dy <= RADIUS; dy++) {
					for (int dz = -RADIUS; dz <= RADIUS; dz++) {
						if (dx * dx + dy * dy + dz * dz == squared) {
							offsets[at++] = dx;
							offsets[at++] = dy;
							offsets[at++] = dz;
						}
					}
				}
			}
		}

		OFFSETS = offsets;
	}

	private LeafDecay() {
	}

	/** True while a leaf with no trunk near it goes. */
	public static boolean decays() {
		return Settings.LEAVES_DECAY.on();
	}

	/** One more leaf has gone. Counted so a session can be read back. */
	public static void letGo() {
		gone++;
		if (gone % REPORT_EVERY == 0) {
			Feinschliff.log().debug("leaf decay: " + gone + " leaves gone so far");
		}
	}
}
