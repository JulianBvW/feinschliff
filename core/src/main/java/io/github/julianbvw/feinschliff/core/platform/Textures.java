package io.github.julianbvw.feinschliff.core.platform;

/** The texture store of the running game. */
public interface Textures {

	/**
	 * Reads every texture the game has already loaded once more, into the same
	 * places it put them the first time.
	 *
	 * <p>The game does this itself whenever a display setting changes, so it is
	 * a path it already walks; what the core adds is a second reason to walk it.
	 */
	void reloadAll();
}
