package io.github.julianbvw.feinschliff.core.world;

import java.util.Random;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Whether a dungeon chest holds a sponge instead of what it rolled.
 *
 * <p>A dungeon chest is the only generated chest this version has, and its
 * eight rolls are the whole loot system. A sponge belongs in it because there
 * is no other way to one: it is in no chest, on no mob and in no recipe, and
 * the creative list it sits in cannot be opened in single player.
 *
 * <p>The sponge takes a place rather than adding one, and that is not a matter
 * of taste. The slot a rolled item goes into is drawn from the generator's own
 * random, and that draw only happens when the roll produced something -- so
 * turning one of the empty rolls into a sponge would take one number more out
 * of that stream than the game did. Everything the chunk generates after the
 * dungeon hangs off the same stream: the other dungeon tries, clay, dirt,
 * gravel, coal, iron, gold, redstone, diamond, how many trees and of which
 * kind, flowers, mushrooms, sugar cane, cactus and seventy water and lava
 * springs. One extra draw moves all of it. This one draws nothing: it decides
 * from a random of its own, seeded from the world and the chest, and hands back
 * a different stack for a slot the game had already picked.
 */
public final class DungeonLoot {

	/**
	 * How often a replaceable stack becomes a sponge. A chest ends up with
	 * around five of those, so roughly every second chest holds one.
	 */
	private static final int ONE_IN = 7;

	/** The finds one opens a dungeon chest for. These are never the ones taken. */
	private static final int[] KEEP = {
		322,  // golden apple
		329,  // saddle
		2256, // record 13
		2257  // record cat
	};

	private DungeonLoot() {
	}

	/**
	 * @param seed   the world seed
	 * @param slot   the slot the game drew for this stack, which is what makes
	 *               the eight rolls of one chest decide separately
	 * @param itemId what the chest rolled, and what the sponge would replace
	 */
	public static boolean sponge(long seed, int x, int y, int z, int slot, int itemId) {
		if (!Settings.SPONGE_IN_DUNGEONS.on() || kept(itemId)) {
			return false;
		}

		long mixed = seed
			^ (x * 341873128712L)
			^ (z * 132897987541L)
			^ (y * 2147483647L)
			^ (slot * 1013904223L);
		return new Random(mixed).nextInt(ONE_IN) == 0;
	}

	private static boolean kept(int itemId) {
		for (int i = 0; i < KEEP.length; i++) {
			if (KEEP[i] == itemId) {
				return true;
			}
		}
		return false;
	}
}
