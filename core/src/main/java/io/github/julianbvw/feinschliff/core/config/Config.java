package io.github.julianbvw.feinschliff.core.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.github.julianbvw.feinschliff.core.platform.Log;

/**
 * Reads {@code feinschliff.txt} and keeps the options in sync with it.
 *
 * <p>Rules, all of them deliberate:
 * <ul>
 *   <li>The file lives in the instance, never in the world folder.</li>
 *   <li>A missing file is generated once, fully commented.</li>
 *   <li>An existing file is never rewritten. The user's edits and comments stay.</li>
 *   <li>Anything unreadable falls back to the default and logs. The game starts
 *       no matter how broken the file is.</li>
 * </ul>
 */
public final class Config {

	private final Path file;
	private final ConfigSpec spec;
	private final Log log;
	private final String[] header;

	public Config(Path file, ConfigSpec spec, Log log, String... header) {
		this.file = file;
		this.spec = spec;
		this.log = log;
		this.header = header;
	}

	public Path file() {
		return this.file;
	}

	/** Writes the default file when it does not exist yet, then loads it. */
	public void loadOrCreate() {
		if (!Files.exists(this.file)) {
			this.writeDefaultFile();
		}
		this.load();
	}

	public void load() {
		if (!Files.exists(this.file)) {
			this.resetAll();
			this.log.warn("no " + this.file.getFileName() + " found, using defaults");
			return;
		}

		Properties properties = new Properties();
		try (BufferedReader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException e) {
			this.resetAll();
			this.log.error("could not read " + this.file + ", using defaults", e);
			return;
		}

		int missing = 0;
		for (Option<?> option : this.spec.options()) {
			String raw = properties.getProperty(option.key());
			if (raw == null) {
				option.reset();
				missing++;
				continue;
			}
			try {
				option.setFromString(raw.trim());
			} catch (IllegalArgumentException e) {
				option.reset();
				this.log.warn(option.key() + ": " + e.getMessage()
					+ " -- falling back to " + option.defaultAsString());
			}
		}

		for (String key : properties.stringPropertyNames()) {
			if (this.spec.byKey(key) == null) {
				this.log.warn("unknown setting '" + key + "' in " + this.file.getFileName() + ", ignored");
			}
		}

		if (missing > 0) {
			this.log.info(missing + " setting(s) not present in " + this.file.getFileName()
				+ ", using defaults (this is normal after an update)");
		}
	}

	private void resetAll() {
		for (Option<?> option : this.spec.options()) {
			option.reset();
		}
	}

	private void writeDefaultFile() {
		try {
			Path parent = this.file.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			try (BufferedWriter writer = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8)) {
				writer.write(this.spec.renderDefaultFile(this.header));
			}
			this.log.info("wrote a default " + this.file.getFileName() + " to " + this.file.toAbsolutePath());
		} catch (IOException e) {
			this.log.error("could not write " + this.file + ", continuing with defaults", e);
		}
	}
}
