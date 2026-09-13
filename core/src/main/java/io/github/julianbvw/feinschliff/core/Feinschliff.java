package io.github.julianbvw.feinschliff.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.julianbvw.feinschliff.core.config.Config;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.hud.DebugOverlay;
import io.github.julianbvw.feinschliff.core.input.Hotkey;
import io.github.julianbvw.feinschliff.core.platform.Log;

/**
 * Entry point of the version-independent core.
 *
 * <p>The version-specific adapter supplies the platform pieces and then calls
 * {@link #init}, {@link #clientTick} and nothing else. Keeping the call surface
 * this small is what makes the mixins trivial and the port to another Minecraft
 * version cheap.
 */
public final class Feinschliff {

	public static final String MOD_ID = "feinschliff";
	public static final String MOD_NAME = "Feinschliff";

	private static Log log = NoopLog.INSTANCE;
	private static Config config;
	private static Hotkey reloadConfigHotkey;
	private static boolean tickDisabled;

	private Feinschliff() {
	}

	/**
	 * @param gameDir the instance root, the directory that also holds
	 *                {@code options.txt} and {@code saves}. The mod's own files
	 *                go into {@code config/} below it, never into a world folder.
	 */
	public static void init(Path gameDir, Log platformLog) {
		log = new DebugGatedLog(platformLog);

		config = new Config(
			gameDir.resolve(Settings.CONFIG_DIR).resolve(Settings.FILE_NAME),
			Settings.spec(),
			log,
			MOD_NAME + " -- quality of life for Minecraft Alpha.",
			"",
			"This file is written once and never overwritten. Delete it to get a",
			"fresh one with all defaults and comments back.",
			"",
			"Anything this file cannot answer falls back to the default, so a typo",
			"costs you one setting, never the game.");
		config.loadOrCreate();

		reloadConfigHotkey = new Hotkey(Settings.HOTKEY_RELOAD_CONFIG);

		log.info(MOD_NAME + " ready, settings at " + config.file().toAbsolutePath());
	}

	/**
	 * Called once per client tick from the adapter.
	 *
	 * <p>Guarded: a bug in here runs 20 times a second and would otherwise take
	 * the whole game down. On the first failure it reports once and stops doing
	 * per-tick work, leaving the game playable.
	 */
	public static void clientTick() {
		if (tickDisabled) {
			return;
		}
		try {
			Hotkey.updateAll();

			if (reloadConfigHotkey != null && reloadConfigHotkey.pressed()) {
				reloadConfig();
			}

			DebugOverlay.tick();
		} catch (Throwable t) {
			tickDisabled = true;
			log.error(MOD_NAME + ": per-tick work failed and has been switched off"
				+ " for this session. The game keeps running. Please report this.", t);
		}
	}

	public static void reloadConfig() {
		if (config == null) {
			return;
		}
		config.load();
		log.info("reloaded " + Settings.FILE_NAME);
	}

	public static Log log() {
		return log;
	}

	/** Drops debug output unless the user asked for it. */
	private static final class DebugGatedLog implements Log {

		private final Log delegate;

		DebugGatedLog(Log delegate) {
			this.delegate = delegate;
		}

		@Override
		public void info(String message) {
			this.delegate.info(message);
		}

		@Override
		public void warn(String message) {
			this.delegate.warn(message);
		}

		@Override
		public void error(String message, Throwable cause) {
			this.delegate.error(message, cause);
		}

		@Override
		public void debug(String message) {
			if (Settings.DEBUG_LOGGING.on()) {
				this.delegate.debug(message);
			}
		}
	}

	/** Used only if something calls the log before {@link #init}. */
	private enum NoopLog implements Log {
		INSTANCE;

		@Override
		public void info(String message) {
		}

		@Override
		public void warn(String message) {
		}

		@Override
		public void error(String message, Throwable cause) {
		}

		@Override
		public void debug(String message) {
		}
	}
}
