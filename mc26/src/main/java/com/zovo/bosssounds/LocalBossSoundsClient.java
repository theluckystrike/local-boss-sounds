package com.zovo.bosssounds;

import com.zovo.bosssounds.core.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalBossSoundsClient implements ClientModInitializer {
	public static final Logger LOG = LoggerFactory.getLogger("local-boss-sounds");
	public static volatile Config CONFIG = Config.defaults();

	@Override
	public void onInitializeClient() {
		reload();
	}

	/** Reads config/local-boss-sounds.json again. Also used by the game test. */
	public static void reload() {
		Config c = Config.load(FabricLoader.getInstance().getConfigDir().resolve("local-boss-sounds.json"));
		for (String w : c.warnings) LOG.warn("local-boss-sounds.json: {}", w);
		CONFIG = c;
		LOG.info("Local Boss Sounds ready: witherSpawn={}, dragonDeath={}, endPortalOpen={}",
			c.witherSpawn.id(), c.dragonDeath.id(), c.endPortalOpen.id());
	}
}
