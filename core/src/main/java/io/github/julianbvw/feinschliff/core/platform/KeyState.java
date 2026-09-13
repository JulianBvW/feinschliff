package io.github.julianbvw.feinschliff.core.platform;

/**
 * Reads the current physical state of a key. Implemented by the adapter so
 * that hotkey edge detection can live in the core.
 */
public interface KeyState {

	boolean isDown(int keyCode);
}
