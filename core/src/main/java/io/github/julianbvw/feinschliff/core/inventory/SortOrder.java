package io.github.julianbvw.feinschliff.core.inventory;

/** What a sorted row of slots is sorted by. */
public enum SortOrder {

	/**
	 * By item id, then by the number beside it. Blocks come before things
	 * made of them, because the game numbers them that way.
	 */
	ID,

	/** The biggest piles first, ties broken by id so the order never wobbles. */
	COUNT
}
