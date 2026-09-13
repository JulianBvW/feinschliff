package io.github.julianbvw.feinschliff.core.platform;

/**
 * Translates a readable key name from the config file into the key code the
 * game's input layer uses. Implemented by the adapter, because the mapping is
 * an LWJGL detail.
 */
public interface KeyCodeResolver {

	/** Key code, or {@code -1} when the name is unknown or unbound. */
	int resolve(String keyName);
}
