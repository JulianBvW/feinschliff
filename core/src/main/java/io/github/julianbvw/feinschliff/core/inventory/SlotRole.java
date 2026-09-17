package io.github.julianbvw.feinschliff.core.inventory;

/**
 * What a slot of an open menu is for.
 *
 * <p>The core decides where a stack should go in terms of these and nothing
 * else. Which slot carries which role is the adapter's answer, because that
 * needs the game.
 */
public enum SlotRole {

	/** The nine slots that are also on screen while no menu is open. */
	HOTBAR,

	/** The three rows above the hotbar. */
	MAIN,

	/** Helmet, chestplate, leggings, boots. */
	ARMOUR,

	/** A square of the crafting grid, two by two or three by three. */
	CRAFTING,

	/** What the crafting grid currently produces. */
	RESULT,

	/** What the furnace is smelting. */
	FURNACE_INPUT,

	/** What the furnace is burning. */
	FURNACE_FUEL,

	/** What the furnace has finished. */
	FURNACE_OUTPUT,

	/** A chest, or anything else the screen brought along. */
	OTHER
}
