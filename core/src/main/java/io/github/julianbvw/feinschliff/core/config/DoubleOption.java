package io.github.julianbvw.feinschliff.core.config;

public final class DoubleOption extends Option<Double> {

	private final double min;
	private final double max;

	public DoubleOption(String key, double defaultValue, double min, double max, String... comment) {
		super(key, defaultValue, comment);
		this.min = min;
		this.max = max;
	}

	public double value() {
		return this.get();
	}

	public float asFloat() {
		return (float)this.get().doubleValue();
	}

	@Override
	protected Double parse(String raw) {
		double parsed;
		try {
			parsed = Double.parseDouble(raw);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("expected a number, got '" + raw + "'");
		}
		if (Double.isNaN(parsed) || parsed < this.min || parsed > this.max) {
			throw new IllegalArgumentException("value " + parsed + " is outside " + this.min + ".." + this.max);
		}
		return parsed;
	}

	@Override
	protected String format(Double value) {
		return value.toString();
	}

	@Override
	public String valueHint() {
		return this.min + " to " + this.max;
	}
}
