package io.github.julianbvw.feinschliff.core.config;

/**
 * One of a fixed set of names.
 *
 * <p>The accepted values are listed in the generated file, so the reader does
 * not have to guess, and anything else is refused by name rather than quietly
 * taken as "none of them".
 */
public final class ChoiceOption extends Option<String> {

	private final String[] choices;

	public ChoiceOption(String key, String defaultValue, String[] choices, String... comment) {
		super(key, defaultValue, comment);
		this.choices = choices;
	}

	public String text() {
		return this.get();
	}

	/** True when the value is that one, whatever case the file spells it in. */
	public boolean is(String choice) {
		return this.get().equalsIgnoreCase(choice);
	}

	@Override
	protected String parse(String raw) {
		for (String choice : this.choices) {
			if (choice.equalsIgnoreCase(raw)) {
				return choice;
			}
		}
		throw new IllegalArgumentException("expected " + this.valueHint() + ", got '" + raw + "'");
	}

	@Override
	protected String format(String value) {
		return value;
	}

	@Override
	public String valueHint() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.choices.length; i++) {
			if (i > 0) {
				sb.append(i == this.choices.length - 1 ? " or " : ", ");
			}
			sb.append(this.choices[i]);
		}
		return sb.toString();
	}
}
