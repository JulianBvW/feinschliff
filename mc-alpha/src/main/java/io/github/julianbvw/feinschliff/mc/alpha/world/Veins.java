package io.github.julianbvw.feinschliff.mc.alpha.world;

import io.github.julianbvw.feinschliff.core.world.Ores;

/**
 * Where the vein currently being measured out has been moved to.
 *
 * <p>It is held here rather than handed along because the two ends of the
 * journey are two separate injections: one sets the vein down in its new place,
 * the others take the blocks back to the old one.
 *
 * <p>The previous pair is handed back and restored because veins nest. Reading
 * a block is how the game notices it has no chunk there yet, and making that
 * chunk lays down veins of its own, in the middle of the vein that asked.
 */
public final class Veins {

	private static int shiftX;
	private static int shiftZ;

	private Veins() {
	}

	public static int shiftX() {
		return shiftX;
	}

	public static int shiftZ() {
		return shiftZ;
	}

	public static void moveTo(int x, int z) {
		shiftX = Ores.shiftFor(x);
		shiftZ = Ores.shiftFor(z);
	}

	public static void restore(int outerX, int outerZ) {
		shiftX = outerX;
		shiftZ = outerZ;
	}
}
