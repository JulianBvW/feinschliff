package io.github.julianbvw.feinschliff.mc.alpha.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.platform.Log;

/** Routes the core's log output into the game's log4j instance. */
public final class Log4jLog implements Log {

	private static final Logger LOGGER = LogManager.getLogger(Feinschliff.MOD_NAME);

	@Override
	public void info(String message) {
		LOGGER.info(message);
	}

	@Override
	public void warn(String message) {
		LOGGER.warn(message);
	}

	@Override
	public void error(String message, Throwable cause) {
		LOGGER.error(message, cause);
	}

	@Override
	public void debug(String message) {
		// Deliberately logged at info level: the launcher console hides debug
		// output by default, and this is only reached when the user switched
		// general.debugLogging on to collect a report.
		LOGGER.info("[debug] " + message);
	}
}
