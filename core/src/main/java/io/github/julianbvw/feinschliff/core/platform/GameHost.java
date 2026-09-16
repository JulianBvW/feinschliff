package io.github.julianbvw.feinschliff.core.platform;

/**
 * What the core is allowed to know about the running game.
 *
 * <p>Implemented by the adapter and reached through {@link Game}. Features pull
 * through this interface rather than being pushed at, which keeps the call
 * surface between adapter and core at the three methods on
 * {@code Feinschliff}.
 */
public interface GameHost {

	/** The six directions the camera can be steered in. */
	enum Move {
		FORWARD,
		BACK,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}

	/** False while no world is loaded, and during the moment a world is swapped. */
	boolean inWorld();

	/** True while a menu, an inventory or the pause screen is open. */
	boolean screenOpen();

	/** False from the moment the player dies until they respawn. */
	boolean playerAlive();

	double playerX();

	double playerY();

	double playerZ();

	float playerYaw();

	float playerPitch();

	/** Key code the player has bound to that direction, or {@code -1}. */
	int movementKey(Move direction);

	/**
	 * How far the rendered world reaches from the player, in blocks.
	 *
	 * <p>Beyond this there is nothing to see: the render chunk grid is anchored
	 * to the player and the far clipping plane sits at a fixed distance from it.
	 */
	double renderedRadius();
}
