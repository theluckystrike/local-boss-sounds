package com.zovo.bosssounds.core;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** config/local-boss-sounds.json. Each sound is "everywhere", "nearby" or "off". */
public final class Config {
	public Mode witherSpawn = Mode.NEARBY;
	public Mode dragonDeath = Mode.NEARBY;
	public Mode endPortalOpen = Mode.NEARBY;
	public final List<String> warnings = new ArrayList<>();

	public static Config defaults() {
		return new Config();
	}

	public Mode modeFor(int type) {
		return switch (type) {
			case BossSoundRule.WITHER_SPAWN -> witherSpawn;
			case BossSoundRule.DRAGON_DEATH -> dragonDeath;
			case BossSoundRule.END_PORTAL_OPEN -> endPortalOpen;
			default -> Mode.EVERYWHERE;
		};
	}

	public static Config load(Path file) {
		Config c = new Config();
		if (!Files.exists(file)) {
			c.save(file);
			return c;
		}
		JsonObject o;
		try {
			JsonElement e = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
			if (!e.isJsonObject()) throw new IllegalStateException("not a JSON object");
			o = e.getAsJsonObject();
		} catch (Exception ex) {
			c.warnings.add("could not read the file (" + ex.getMessage() + "), using defaults");
			return c;
		}
		c.witherSpawn = mode(o, "witherSpawn", c.witherSpawn, c);
		c.dragonDeath = mode(o, "dragonDeath", c.dragonDeath, c);
		c.endPortalOpen = mode(o, "endPortalOpen", c.endPortalOpen, c);
		return c;
	}

	public void save(Path file) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put("witherSpawn", witherSpawn.id());
		m.put("dragonDeath", dragonDeath.id());
		m.put("endPortalOpen", endPortalOpen.id());
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(m) + "\n", StandardCharsets.UTF_8);
		} catch (IOException ex) {
			warnings.add("could not write " + file + ": " + ex.getMessage());
		}
	}

	private static Mode mode(JsonObject o, String k, Mode def, Config c) {
		if (!o.has(k)) return def;
		JsonElement e = o.get(k);
		Mode m = e.isJsonPrimitive() && e.getAsJsonPrimitive().isString() ? Mode.parse(e.getAsString()) : null;
		if (m == null) {
			c.warnings.add(k + " must be \"everywhere\", \"nearby\" or \"off\", using \"" + def.id() + "\"");
			return def;
		}
		return m;
	}
}
