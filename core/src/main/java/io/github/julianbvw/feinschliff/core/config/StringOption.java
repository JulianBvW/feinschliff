package io.github.julianbvw.feinschliff.core.config;

/**
 * A plain line of text, taken as it stands.
 *
 * <p>Empty is a real answer and means "nothing chosen", so this is the one
 * option type that cannot reject what it is given: anything a line of the file
 * can hold is a value, and whether it names something that exists is for the
 * feature reading it to find out.
 */
public final class StringOption extends Option<String> {

	public StringOption(String key, String defaultValue, String... comment) {
		super(key, defaultValue, comment);
	}

	/** Empty when nothing is set, never null. */
	public String text() {
		return this.get();
	}

	/** True when the line carries something other than blanks. */
	public boolean isSet() {
		return !this.get().isEmpty();
	}

	@Override
	protected String parse(String raw) {
		return raw.trim();
	}

	@Override
	protected String format(String value) {
		return value;
	}
}
