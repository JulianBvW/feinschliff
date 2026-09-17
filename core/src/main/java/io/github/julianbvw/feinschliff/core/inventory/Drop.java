package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * How much of what is in hand the drop key throws away.
 *
 * <p>The answer is a number and nothing else. Suppressing the throw itself
 * would take the stack out of the inventory and then drop nothing, which is
 * not a cancelled action but a destroyed item -- the amount is the only place
 * this can safely be decided.
 */
public final class Drop {

	/** What the key throws without help. */
	private static final int ONE = 1;

	private Drop() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_DROP_STACK.on();
	}

	/**
	 * Never less than one, whatever is asked: a zero would still be taken out
	 * of the inventory and would land in the world as a stack of nothing.
	 */
	public static int amount(boolean wholeStack, int carried) {
		if (!enabled() || !wholeStack || carried < ONE) {
			return ONE;
		}
		return carried;
	}
}
