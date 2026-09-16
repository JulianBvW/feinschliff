package io.github.julianbvw.feinschliff.core.platform;

/** The shape of the game window, as far as the mod is allowed to change it. */
public interface WindowMode {

	/**
	 * Fills the whole screen without a frame around it, or puts the window
	 * back the way it was.
	 *
	 * @throws RuntimeException if the window cannot be changed, in which case
	 *                          nothing about it has changed either
	 */
	void setBorderless(boolean borderless);

	/**
	 * Takes the pointer again, for a window that was just rebuilt under it.
	 * Harmless when it already has it, and does nothing at all while the
	 * pointer belongs to an open screen.
	 */
	void takePointer();
}
