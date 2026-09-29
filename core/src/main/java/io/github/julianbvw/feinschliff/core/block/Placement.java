package io.github.julianbvw.feinschliff.core.block;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Which way a block points once you have put it down.
 *
 * <p>Two blocks in this version work their facing out from what stands around
 * them rather than from where you stand. A staircase reads its neighbours
 * again every time one of them changes, so it turns under you while you build;
 * a furnace reads them once and looks for the side with nothing against it.
 * Neither can ask about the player: the hook the game calls after a block is
 * placed is handed the face that was clicked and nothing else.
 *
 * <p>A yaw comes in and one of four quarters comes out. Quarter zero is
 * looking towards positive z, and the count runs the way the yaw does: one is
 * negative x, two is negative z, three is positive x. Both tables below are
 * indexed by that quarter, and both hold metadata values this version already
 * writes for itself -- a block turned by anything outside those ranges would
 * be one the game cannot draw.
 */
public final class Placement {

	/**
	 * Stairs metadata: which side the full-height half sits on, where zero is
	 * positive x, one negative x, two positive z and three negative z. A
	 * staircase rises the way you are looking, so you walk up it going forward.
	 */
	private static final int[] STAIRS_BY_QUARTER = { 2, 1, 3, 0 };

	/**
	 * Furnace metadata: the face the front sits on, in the game's own face
	 * numbering, where two is negative z, three positive z, four negative x and
	 * five positive x. The front comes back at you, so it is the face opposite
	 * the way you are looking.
	 */
	private static final int[] FRONT_BY_QUARTER = { 2, 5, 3, 4 };

	private Placement() {
	}

	/** True while a block placed takes its facing from you. */
	public static boolean facesYou() {
		return Settings.PLACING_FACES_YOU.on();
	}

	/** The metadata a staircase is placed with. */
	public static int stairsFacing(float yaw) {
		return STAIRS_BY_QUARTER[quarter(yaw)];
	}

	/** The metadata a block with a front is placed with. */
	public static int frontFacing(float yaw) {
		return FRONT_BY_QUARTER[quarter(yaw)];
	}

	/**
	 * Rounding to the nearest quarter turn rather than cutting into quarters,
	 * which is why the half is added before the floor. A yaw is unbounded in
	 * both directions, so the floor has to be a real one and the wrap has to
	 * survive negative numbers.
	 */
	private static int quarter(float yaw) {
		return ((int)Math.floor(yaw * 4.0F / 360.0F + 0.5)) & 3;
	}
}
