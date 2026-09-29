package io.github.julianbvw.feinschliff.core.mining;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Which blocks come up whole rather than as what they are made of.
 *
 * <p>Most blocks in this version already hand themselves back when they are
 * mined, and those are no business of this class. Listed here are only the ones
 * that do not: stone that comes up as cobblestone, a grass block as dirt, ore
 * as the thing inside it, and the five that leave nothing behind at all.
 *
 * <p>Every answer is a block id this version already has an item for, so
 * nothing here can put a stack in a chest that the game without the mod would
 * refuse to read.
 */
public final class SilkTouch {

	/** A lit redstone ore is a redstone ore that has been walked on. */
	private static final int REDSTONE_ORE = 73;
	private static final int LIT_REDSTONE_ORE = 74;

	/** The blocks whose own item is not what breaking them gives you. */
	private static final int[] COVERED = {
		1,  // stone -> cobblestone
		2,  // grass block -> dirt
		13, // gravel -> flint, one time in ten
		16, // coal ore -> coal
		18, // leaves -> a sapling, one time in twenty
		20, // glass -> nothing
		47, // bookshelf -> nothing
		56, // diamond ore -> diamond
		73, // redstone ore -> redstone
		74, // lit redstone ore -> redstone
		78, // snow layer -> a snowball
		79, // ice -> nothing
		80, // snow -> four snowballs
		82  // clay -> four clay
	};

	private SilkTouch() {
	}

	/**
	 * The block id to leave behind for a golden tool, or zero to let the game
	 * answer for itself.
	 */
	public static int drop(int blockId) {
		if (!Settings.MINING_GOLD_SILK_TOUCH.on() || !listed(blockId)) {
			return 0;
		}
		return blockId == LIT_REDSTONE_ORE ? REDSTONE_ORE : blockId;
	}

	private static boolean listed(int blockId) {
		for (int i = 0; i < COVERED.length; i++) {
			if (COVERED[i] == blockId) {
				return true;
			}
		}
		return false;
	}
}
