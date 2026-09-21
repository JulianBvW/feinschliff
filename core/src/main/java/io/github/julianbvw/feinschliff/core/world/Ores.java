package io.github.julianbvw.feinschliff.core.world;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Why a vein is smaller west and north of the origin.
 *
 * <p>The game lays a vein out as a line of spheres and then walks a box around
 * each one, testing every block in the box against the sphere. The box is built
 * by casting the sphere's edges to {@code int}, and that cast rounds towards
 * zero rather than downwards -- so at negative coordinates the box sits a block
 * off the sphere it is meant to contain, and the part that sticks out is never
 * reached. Each negative horizontal axis costs a vein some of itself, and the
 * two multiply: measured over four hundred and forty-one generated chunks, a
 * chunk at negative x and negative z holds 2.5 diamond ore against 4.1 at
 * positive x and positive z.
 *
 * <p>Nothing here changes the arithmetic. The vein is moved to where the cast
 * rounds downwards of its own accord, and the blocks are read and written back
 * at the place they really belong.
 *
 * <p>How far it moves is the whole of the care in this class. The game works
 * the vein out in {@code float}, and a float keeps its fraction only while the
 * number carrying it stays small: at sixty-seven million it can no longer tell
 * anything finer than eight whole blocks apart. Moving a vein by some large
 * constant would therefore not correct it but grind it to dust. Twice the
 * coordinate carries it to its own magnitude instead, where a float keeps
 * exactly as much of the fraction as it kept before, and a coordinate that is
 * already positive does not move at all -- so ground that was right comes out
 * bit for bit as it did, ore for ore.
 *
 * <p>The height is deliberately left alone. It is never negative in a world a
 * hundred and twenty-eight blocks tall, so there is nothing there to correct.
 */
public final class Ores {

	private Ores() {
	}

	/** True while every vein is measured out the same way, wherever it lies. */
	public static boolean evenly() {
		return Settings.WORLD_EVEN_ORE_DISTRIBUTION.on();
	}

	/**
	 * How far this axis has to move for the cast to round the way it should:
	 * onto its own magnitude, or not at all when it is already positive.
	 */
	public static int shiftFor(int coordinate) {
		return evenly() && coordinate < 0 ? -2 * coordinate : 0;
	}
}
