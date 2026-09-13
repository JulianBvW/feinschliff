package io.github.julianbvw.feinschliff.core.config;

import java.util.Locale;

import io.github.julianbvw.feinschliff.core.platform.KeyCodeResolver;

/**
 * A hotkey, stored as a readable name ({@code F10}, {@code LSHIFT}, {@code C}).
 *
 * <p>The name is resolved to a key code lazily and cached, because the resolver
 * is an LWJGL detail supplied by the adapter and is not necessarily usable at
 * the time the options are constructed.
 */
public final class KeyOption extends Option<String> {

	/** Marks a hotkey the user has switched off. */
	public static final String UNBOUND = "NONE";

	private static final int UNRESOLVED = Integer.MIN_VALUE;

	private static KeyCodeResolver resolver = keyName -> -1;

	private int cachedCode = UNRESOLVED;

	public KeyOption(String key, String defaultValue, String... comment) {
		super(key, defaultValue, comment);
	}

	public static void setResolver(KeyCodeResolver resolver) {
		KeyOption.resolver = resolver;
	}

	/** Key code, or {@code -1} when unbound or unknown. */
	public int code() {
		if (this.cachedCode == UNRESOLVED) {
			String name = this.get();
			this.cachedCode = UNBOUND.equals(name) ? -1 : resolver.resolve(name);
		}
		return this.cachedCode;
	}

	public boolean isBound() {
		return this.code() >= 0;
	}

	@Override
	protected String parse(String raw) {
		String name = raw.trim().toUpperCase(Locale.ROOT);
		if (name.isEmpty()) {
			return UNBOUND;
		}
		return name;
	}

	@Override
	protected String format(String value) {
		return value;
	}

	@Override
	protected void onChanged() {
		// Force a fresh lookup; the name may have changed on reload.
		this.cachedCode = UNRESOLVED;
	}

	@Override
	public String valueHint() {
		return "a key name such as F10, C, LSHIFT -- or NONE to unbind";
	}
}
