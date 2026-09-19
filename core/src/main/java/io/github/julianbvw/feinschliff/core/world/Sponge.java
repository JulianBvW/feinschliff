package io.github.julianbvw.feinschliff.core.world;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * What a sponge drinks, and how far it reaches.
 *
 * <p>The block is a torso in this version. Placing one walks the five by five
 * by five around it and asks every position whether it holds water, and the
 * body of that question is empty. The other half is finished: taking the
 * sponge up again wakes the same cube so the water flows back. That is why the
 * radius below is read off the game rather than chosen -- two is what both
 * loops already say, and the returning water knows no other number.
 *
 * <p>Water is two block ids, one for water still on the move and one for water
 * that has settled, and a sponge drinks both. Ids are what this is keyed on
 * because a material is a game type and has no place on this side.
 */
public final class Sponge {

	/** Two in every direction, so five by five by five around the sponge. */
	public static final int RADIUS = 2;

	private static final int FLOWING_WATER = 8;
	private static final int WATER = 9;

	private Sponge() {
	}

	/** True while a sponge takes the water around it away. */
	public static boolean soaksUp() {
		return Settings.SPONGE_SOAKS_UP_WATER.on();
	}

	/** True while it drinks again every time something beside it changes. */
	public static boolean keepsDry() {
		return soaksUp() && Settings.SPONGE_KEEPS_DRY.on();
	}

	/** Whether that is water of either kind. */
	public static boolean isWater(int blockId) {
		return blockId == FLOWING_WATER || blockId == WATER;
	}
}
