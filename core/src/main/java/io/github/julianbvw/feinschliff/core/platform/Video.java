package io.github.julianbvw.feinschliff.core.platform;

/** The display settings the mod can change while the game runs. */
public interface Video {

	/**
	 * Waits for the monitor before showing a frame, or stops waiting.
	 *
	 * <p>A request, not a guarantee: a driver configured to force vertical sync
	 * on or off ignores what the application asks for, and there is no way to
	 * read back what actually happened.
	 */
	void setVSync(boolean enabled);
}
