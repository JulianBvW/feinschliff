package io.github.julianbvw.feinschliff.core.config;

public final class IntOption extends Option<Integer> {

	private final int min;
	private final int max;

	public IntOption(String key, int defaultValue, int min, int max, String... comment) {
		super(key, defaultValue, comment);
		this.min = min;
		this.max = max;
	}

	public int value() {
		return this.get();
	}

	@Override
	protected Integer parse(String raw) {
		int parsed;
		try {
			parsed = Integer.parseInt(raw);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("expected a whole number, got '" + raw + "'");
		}
		if (parsed < this.min || parsed > this.max) {
			throw new IllegalArgumentException("value " + parsed + " is outside " + this.min + ".." + this.max);
		}
		return parsed;
	}

	@Override
	protected String format(Integer value) {
		return value.toString();
	}

	@Override
	public String valueHint() {
		return this.min + " to " + this.max;
	}
}
