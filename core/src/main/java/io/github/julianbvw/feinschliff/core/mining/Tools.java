package io.github.julianbvw.feinschliff.core.mining;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Which tool a block is for, and what a tool is worth on it.
 *
 * <p>The game keeps one list per tool class -- thirteen blocks for the pickaxe,
 * four for the axe, seven for the shovel, none at all for the hoe -- and a tool
 * holding a block in its list is twice its tier plus two, everything else being
 * a flat one. Those lists were written before most of the blocks that need
 * them, so twenty-one blocks belong to no tool and are mined with bare hands
 * however good a pickaxe you are swinging.
 *
 * <p>The tables here are the difference between those lists and the ones every
 * later version of the game agrees on, which makes them the only part of this
 * that is version data: each entry falls away the moment a version ships with
 * it, and the tables shrink towards empty rather than growing. Block ids are
 * what they are keyed on because ids are the one thing that has never moved
 * between these versions.
 *
 * <p>Nothing here is ever slower than the game already was. Every answer is the
 * larger of the game's and this one's, so no combination of settings can take a
 * bonus away, and a block that a later version left toolless keeps whatever
 * this version gave it.
 */
public final class Tools {

	/** Pickaxe blocks this version's pickaxe has never heard of. */
	private static final int[] PICKAXE = {
		45, // bricks
		49, // obsidian
		52, // mob spawner
		61, // furnace
		62, // lit furnace
		66, // rail
		67, // stone stairs
		70, // stone pressure plate
		71, // iron door
		73, // redstone ore
		74, // lit redstone ore
		77  // stone button
	};

	/** Axe blocks. The game's own list stops at planks, log, bookshelf and chest. */
	private static final int[] AXE = {
		53, // oak stairs
		58, // crafting table
		63, // standing sign
		64, // wooden door
		65, // ladder
		68, // wall sign
		72, // wooden pressure plate
		84, // jukebox
		85  // fence
	};

	private static final int[] SHOVEL = {
		60 // farmland
	};

	/** The hoe's first two blocks: until now it could only till, never dig. */
	private static final int[] HOE = {
		18, // leaves
		19  // sponge
	};

	/** What a sword gets through, standing in for shears this version has not got. */
	private static final int[] SWORD = {
		18, // leaves
		35, // wool
		81  // cactus
	};

	private static final int[] NONE = {};

	/**
	 * The wooden pressure plate. It is made of planks, sounds like planks and
	 * looks like planks, but its constructor is handed no material and takes the
	 * default, which is stone -- so the game asks for a pickaxe before it will
	 * let go of one. This is the only place where a tool decides a drop rather
	 * than a duration.
	 */
	private static final int WOODEN_PRESSURE_PLATE = 72;

	/** What shears are worth on wool in the versions that have them. */
	private static final float SHEARS = 5.0F;

	/** What a golden tool is worth once it is allowed to be the fast one. */
	private static final float GOLDEN = 12.0F;

	private Tools() {
	}

	/** True while any of this section is on and a tool answer may change. */
	public static boolean anyOn() {
		return Settings.MINING_TOOL_ASSIGNMENTS.on()
			|| Settings.MINING_GOLD_IS_FAST.on()
			|| Settings.MINING_SWORD_CUTS.on();
	}

	/**
	 * How fast the tool in hand gets through the block.
	 *
	 * @param kind    what the tool is
	 * @param tier    wood 0, stone 1, iron 2, diamond 3 -- and gold, which
	 *                shares wood's place
	 * @param golden  whether it is a golden tool, which the tier cannot say
	 * @param blockId the block being broken
	 * @param vanilla what the game answered, and the floor of what is returned
	 */
	public static float speed(ToolKind kind, int tier, boolean golden, int blockId, float vanilla) {
		float speed = vanilla;

		// A tool the game already counts as the right one. Only gold changes
		// here, and only upwards. A sword is left out of this: it is a flat one
		// and a half on every block in the game, so a speed above one says
		// nothing about what the game thinks the block is for.
		if (kind != ToolKind.SWORD && vanilla > 1.0F) {
			speed = Math.max(speed, tierSpeed(tier, golden));
		}

		if (Settings.MINING_TOOL_ASSIGNMENTS.on() && listed(blocksFor(kind), blockId)) {
			speed = Math.max(speed, tierSpeed(tier, golden));
		}

		if (Settings.MINING_SWORD_CUTS.on() && kind == ToolKind.SWORD && listed(SWORD, blockId)) {
			speed = Math.max(speed, SHEARS);
		}

		return speed;
	}

	/**
	 * Whether the block gives anything up for the tool in hand.
	 *
	 * @param vanilla the game's answer, which is only ever turned from no to yes
	 */
	public static boolean drops(int blockId, boolean vanilla) {
		if (vanilla || !Settings.MINING_TOOL_ASSIGNMENTS.on()) {
			return vanilla;
		}
		return blockId == WOODEN_PRESSURE_PLATE;
	}

	/**
	 * What a tool of that material is worth on a block it is made for. The
	 * formula is the game's own; the exception is gold, which sits at wood's
	 * tier and therefore digs at wood's speed, the one thing gold is not for.
	 */
	private static float tierSpeed(int tier, boolean golden) {
		if (golden && Settings.MINING_GOLD_IS_FAST.on()) {
			return GOLDEN;
		}
		return (tier + 1) * 2.0F;
	}

	private static int[] blocksFor(ToolKind kind) {
		switch (kind) {
			case PICKAXE:
				return PICKAXE;
			case AXE:
				return AXE;
			case SHOVEL:
				return SHOVEL;
			case HOE:
				return HOE;
			default:
				return NONE;
		}
	}

	private static boolean listed(int[] blocks, int blockId) {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] == blockId) {
				return true;
			}
		}
		return false;
	}
}
