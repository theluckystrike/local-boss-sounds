package com.zovo.bosssounds.core;

import java.util.Locale;

/** How one boss sound is played. */
public enum Mode {
	/** Vanilla: heard by everyone in the world, from any distance. */
	EVERYWHERE,
	/** Heard only when the event is near you, from the place where it happened. */
	NEARBY,
	/** Never heard. */
	OFF;

	public static Mode parse(String s) {
		if (s == null) return null;
		try {
			return Mode.valueOf(s.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public String id() {
		return name().toLowerCase(Locale.ROOT);
	}
}
