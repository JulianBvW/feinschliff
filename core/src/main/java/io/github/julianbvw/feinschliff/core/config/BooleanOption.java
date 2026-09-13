package io.github.julianbvw.feinschliff.core.config;

public final class BooleanOption extends Option<Boolean> {

	public BooleanOption(String key, boolean defaultValue, String... comment) {
		super(key, defaultValue, comment);
	}

	/** Convenience for the common {@code if (!OPTION.on()) return;} guard. */
	public boolean on() {
		return this.get();
	}

	@Override
	protected Boolean parse(String raw) {
		if ("true".equalsIgnoreCase(raw)) {
			return Boolean.TRUE;
		}
		if ("false".equalsIgnoreCase(raw)) {
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException("expected true or false, got '" + raw + "'");
	}

	@Override
	protected String format(Boolean value) {
		return value.toString();
	}

	@Override
	public String valueHint() {
		return "true or false";
	}
}
