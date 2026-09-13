package io.github.julianbvw.feinschliff.core.platform;

/**
 * Logging sink. Implemented by the version-specific adapter so that the core
 * does not depend on any particular logging framework.
 */
public interface Log {

	void info(String message);

	void warn(String message);

	void error(String message, Throwable cause);

	void debug(String message);
}
