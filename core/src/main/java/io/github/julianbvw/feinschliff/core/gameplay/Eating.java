package io.github.julianbvw.feinschliff.core.gameplay;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * When a meal is worth eating.
 *
 * <p>This version of the game has no hunger bar -- food heals, nothing else.
 * Eating at full health therefore always destroys the item for nothing, with
 * no exception and no food that would still be worth it.
 */
public final class Eating {

	private Eating() {
	}

	/** True while the player should keep the food instead of using it up. */
	public static boolean blockedAtFullHealth(int health, int maxHealth) {
		return Settings.GAMEPLAY_NO_EATING_AT_FULL_HEALTH.on() && health >= maxHealth;
	}
}
