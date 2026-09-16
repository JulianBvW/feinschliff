package io.github.julianbvw.feinschliff.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The ordered description of {@code feinschliff.txt}: which sections and options
 * exist, in which order, with which comments.
 *
 * <p>This is also the generator for the default file. {@code Properties.store()}
 * would drop both comments and order, so writing is done here by hand and
 * {@link java.util.Properties} is used for reading only.
 */
public final class ConfigSpec {

	private static final int RULE_WIDTH = 74;

	private final List<Node> nodes = new ArrayList<>();
	private final List<Option<?>> options = new ArrayList<>();
	private final Map<String, Option<?>> byKey = new LinkedHashMap<>();

	public void section(String title, String... notes) {
		this.nodes.add(Node.section(title, notes));
	}

	public <O extends Option<?>> O add(O option) {
		if (this.byKey.put(option.key(), option) != null) {
			throw new IllegalStateException("duplicate config key: " + option.key());
		}
		this.nodes.add(Node.option(option));
		this.options.add(option);
		return option;
	}

	public List<Option<?>> options() {
		return Collections.unmodifiableList(this.options);
	}

	public Option<?> byKey(String key) {
		return this.byKey.get(key);
	}

	/** Renders the fully commented default file. */
	public String renderDefaultFile(String... header) {
		StringBuilder sb = new StringBuilder();

		for (String line : header) {
			sb.append(comment(line)).append('\n');
		}

		for (Node node : this.nodes) {
			if (node.isSection()) {
				sb.append('\n').append(rule(node.title)).append('\n');
				for (String note : node.notes) {
					sb.append(comment(note)).append('\n');
				}
				sb.append('\n');
			} else {
				Option<?> option = node.option;
				for (String line : option.comment()) {
					sb.append(comment(line)).append('\n');
				}
				String hint = option.valueHint();
				if (hint != null) {
					sb.append(comment("Values: " + hint)).append('\n');
				}
				sb.append(option.key()).append('=').append(option.defaultAsString()).append('\n');
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	private static String comment(String text) {
		return text.isEmpty() ? "#" : "# " + text;
	}

	private static String rule(String title) {
		StringBuilder sb = new StringBuilder("# --- ").append(title).append(' ');
		while (sb.length() < RULE_WIDTH) {
			sb.append('-');
		}
		return sb.toString();
	}

	private static final class Node {

		final String title;
		final String[] notes;
		final Option<?> option;

		private Node(String title, String[] notes, Option<?> option) {
			this.title = title;
			this.notes = notes;
			this.option = option;
		}

		static Node section(String title, String[] notes) {
			return new Node(title, notes, null);
		}

		static Node option(Option<?> option) {
			return new Node(null, null, option);
		}

		boolean isSection() {
			return this.option == null;
		}
	}
}
