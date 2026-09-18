package io.github.julianbvw.feinschliff.core.mining;

/**
 * What the thing in your hand is, as far as breaking blocks is concerned.
 *
 * <p>The game has no such notion: it asks the item itself how fast it is on a
 * block and every item answers for itself. The kind is what the adapter reads
 * off the item's class so the table below can be written once.
 */
public enum ToolKind {

	PICKAXE,
	AXE,
	SHOVEL,
	HOE,
	SWORD
}
