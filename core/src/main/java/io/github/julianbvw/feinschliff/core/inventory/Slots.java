package io.github.julianbvw.feinschliff.core.inventory;

/**
 * The slots of one open menu, as far as the core is concerned.
 *
 * <p>Every index is a position in the menu the game built, never a computed
 * one. That is deliberate and it is a safety property, not a style: the
 * inventory behind a slot may be longer than the menu shows, and writing to an
 * index the menu does not list lands in a place the player can neither see nor
 * get back.
 *
 * <p>The hand is not a slot and has no index. It is asked for by name,
 * because an index that stands for the hand rather than for a place in the
 * menu is exactly the mix-up that loses items.
 *
 * <p>Everything the game knows and the core does not is folded into
 * {@link #capacity}: whether a helmet fits that armour slot, whether that lump
 * burns, whether that ore melts. The core only decides in which order to ask.
 */
public interface Slots {

	int count();

	SlotRole role(int index);

	boolean empty(int index);

	/** How many items are in the slot, zero for an empty one. */
	int size(int index);

	/** Both hold the same kind of item and could therefore be one stack. */
	boolean stackable(int a, int b);

	/**
	 * How many of whatever is in {@code from} the slot {@code to} could hold in
	 * total, counting what is already in it. Zero when it will not take it at
	 * all.
	 */
	int capacity(int to, int from);

	/**
	 * Moves exactly {@code amount} items. The caller guarantees the amount is
	 * at least one, no more than the source holds, and no more than the target
	 * has room for.
	 */
	void move(int from, int to, int amount);

	/** How many items the hand is holding, zero when it is empty. */
	int cursorSize();

	/**
	 * The largest the held stack could grow to, zero when the hand is empty.
	 */
	int cursorMax();

	/**
	 * The held stack and what is in the slot are the same kind and could
	 * therefore be one stack.
	 */
	boolean cursorStackable(int index);

	/**
	 * Moves exactly {@code amount} items out of the slot and into the hand.
	 * The caller guarantees the hand holds something of the same kind, the
	 * amount is at least one, no more than the slot holds, and no more than
	 * the hand has room for.
	 */
	void toCursor(int from, int amount);
}
