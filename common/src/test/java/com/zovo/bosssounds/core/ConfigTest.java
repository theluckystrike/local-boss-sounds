package com.zovo.bosssounds.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigTest {
	@TempDir Path dir;

	@Test void missingFileWritesDefaults() throws Exception {
		Path f = dir.resolve("config/local-boss-sounds.json");
		Config c = Config.load(f);
		assertEquals(Mode.NEARBY, c.witherSpawn);
		String s = Files.readString(f);
		assertTrue(s.contains("\"witherSpawn\": \"nearby\""), s);
		assertTrue(s.contains("\"endPortalOpen\": \"nearby\""), s);
	}

	@Test void readsModesInAnyCase() throws Exception {
		Path f = dir.resolve("c.json");
		Files.writeString(f, "{\"witherSpawn\":\"OFF\",\"dragonDeath\":\"everywhere\"}");
		Config c = Config.load(f);
		assertEquals(Mode.OFF, c.modeFor(BossSoundRule.WITHER_SPAWN));
		assertEquals(Mode.EVERYWHERE, c.modeFor(BossSoundRule.DRAGON_DEATH));
		assertEquals(Mode.NEARBY, c.modeFor(BossSoundRule.END_PORTAL_OPEN));
		assertTrue(c.warnings.isEmpty());
	}

	@Test void badValueKeepsDefaultAndWarns() throws Exception {
		Path f = dir.resolve("c.json");
		Files.writeString(f, "{\"witherSpawn\":\"quiet\",\"dragonDeath\":3}");
		Config c = Config.load(f);
		assertEquals(Mode.NEARBY, c.witherSpawn);
		assertEquals(Mode.NEARBY, c.dragonDeath);
		assertEquals(2, c.warnings.size());
	}

	@Test void brokenJsonUsesDefaults() throws Exception {
		Path f = dir.resolve("c.json");
		Files.writeString(f, "[1,2");
		assertEquals(1, Config.load(f).warnings.size());
	}

	@Test void otherEventsStayVanilla() {
		assertEquals(Mode.EVERYWHERE, Config.defaults().modeFor(1000));
	}
}
