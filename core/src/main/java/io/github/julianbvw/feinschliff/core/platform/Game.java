package io.github.julianbvw.feinschliff.core.platform;

/**
 * The one way from the core to the running game.
 *
 * <p>A single holder rather than a setter per feature: every feature that needs
 * to know whether a world is loaded or a screen is open reads the same host,
 * and a new one needs no plumbing of its own.
 *
 * <p>The host is {@code null} until the adapter has started up, so anything
 * reading it has to say what it does without one.
 */
public final class Game {

	private static GameHost host;

	private Game() {
	}

	public static void setHost(GameHost host) {
		Game.host = host;
	}

	/** Null before the adapter bootstraps. */
	public static GameHost host() {
		return host;
	}
}
