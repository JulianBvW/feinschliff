package io.github.julianbvw.feinschliff.core.config;

/**
 * A single {@code key=value} line in {@code settings.txt}.
 *
 * <p>An option always holds a usable value: it starts at its default and falls
 * back to the default whenever the file is missing, unreadable or malformed.
 * A broken config must never keep the game from starting.
 */
public abstract class Option<T> {

	private final String key;
	private final T defaultValue;
	private final String[] comment;

	private T value;

	protected Option(String key, T defaultValue, String... comment) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.comment = comment;
		this.value = defaultValue;
	}

	public final String key() {
		return this.key;
	}

	public final T get() {
		return this.value;
	}

	public final T defaultValue() {
		return this.defaultValue;
	}

	public final String[] comment() {
		return this.comment;
	}

	/** The default rendered the way it should appear in the generated file. */
	public final String defaultAsString() {
		return this.format(this.defaultValue);
	}

	/** Extra explanation appended to the comment block, e.g. a value range. */
	public String valueHint() {
		return null;
	}

	final void reset() {
		this.value = this.defaultValue;
		this.onChanged();
	}

	final void setFromString(String raw) {
		T parsed = this.parse(raw);
		if (parsed == null) {
			throw new IllegalArgumentException("cannot parse '" + raw + "'");
		}
		this.value = parsed;
		this.onChanged();
	}

	/**
	 * Parse a raw value from the file. Return {@code null} or throw
	 * {@link IllegalArgumentException} when the value is not acceptable.
	 */
	protected abstract T parse(String raw);

	protected abstract String format(T value);

	/** Hook for options that cache derived state. See {@link KeyOption}. */
	protected void onChanged() {
	}
}
