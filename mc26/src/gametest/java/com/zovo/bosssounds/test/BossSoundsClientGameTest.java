package com.zovo.bosssounds.test;

import com.zovo.bosssounds.LocalBossSoundsClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Real game run: a dedicated server and a real client in one test. The server fires the three global boss events at
 * known distances from the player, in the same dimension and in the Nether. The client records every sound it starts.
 * Each mode is checked against what it promises. Results go to run/boss-sounds-result.csv.
 */
public final class BossSoundsClientGameTest implements FabricClientGameTest {
	record Heard(String id, double x, double y, double z, float range) {}

	record Case(String name, int type, String sound, double dx, boolean nether) {}

	static final List<Case> CASES = List.of(
		new Case("wither 10 blocks", 1023, "minecraft:entity.wither.spawn", 10, false),
		new Case("wither 120 blocks", 1023, "minecraft:entity.wither.spawn", 120, false),
		new Case("wither in nether", 1023, "minecraft:entity.wither.spawn", 0, true),
		new Case("dragon 20 blocks", 1028, "minecraft:entity.ender_dragon.death", 20, false),
		new Case("dragon 300 blocks", 1028, "minecraft:entity.ender_dragon.death", 300, false),
		new Case("portal 6 blocks", 1038, "minecraft:block.end_portal.spawn", 6, false),
		new Case("portal 60 blocks", 1038, "minecraft:block.end_portal.spawn", 60, false));

	@Override
	public void runTest(ClientGameTestContext context) {
		List<Heard> heard = new CopyOnWriteArrayList<>();
		SoundEventListener listener = (SoundInstance s, WeighedSoundEvents e, float range) ->
			heard.add(new Heard(s.getIdentifier().toString(), s.getX(), s.getY(), s.getZ(), range));
		context.runOnClient(mc -> mc.getSoundManager().addListener(listener));
		List<String> rows = new ArrayList<>();
		rows.add("mode,case,distance_sent,heard,sound_x,sound_z,offset_from_event,offset_from_player");
		List<String> failures = new ArrayList<>();
		Path cfg = FabricLoader.getInstance().getConfigDir().resolve("local-boss-sounds.json");
		try (TestDedicatedServerContext server = context.worldBuilder().createServer();
			 TestDedicatedServerConnection conn = server.connect()) {
			conn.waitForChunksRender();
			server.runCommand("gamemode creative @a");
			server.runCommand("forceload add -1 -1 1 1");
			server.runCommand("setblock 0 119 0 minecraft:stone");
			server.runCommand("tp @a 0.5 120 0.5");
			context.waitTicks(40);
			for (String mode : List.of("everywhere", "nearby", "off")) {
				writeConfig(cfg, mode);
				LocalBossSoundsClient.reload();
				context.waitTicks(5);
				for (Case c : CASES) {
					heard.clear();
					BlockPos[] eventPos = new BlockPos[1];
					server.runOnServer(srv -> {
						ServerLevel overworld = srv.overworld();
						Vec3 p = srv.getPlayerList().getPlayers().getFirst().position();
						BlockPos at = BlockPos.containing(p.x + c.dx, p.y, p.z);
						eventPos[0] = at;
						ServerLevel where = c.nether ? srv.getLevel(Level.NETHER) : overworld;
						where.globalLevelEvent(c.type, at, 0);
					});
					conn.waitForClientboundPackets();
					context.waitTicks(10);
					Vec3[] pp = new Vec3[1];
					context.runOnClient(mc -> pp[0] = mc.player.position());
					Vec3 player = pp[0];
					Heard h = heard.stream().filter(x -> x.id.equals(c.sound)).findFirst().orElse(null);
					Vec3 ev = Vec3.atCenterOf(eventPos[0]);
					String row = String.format(Locale.ROOT, "%s,%s,%.0f,%s,%s,%s,%s,%s", mode, c.name, c.nether ? 0 : c.dx, h != null,
						h == null ? "" : fmt(h.x), h == null ? "" : fmt(h.z),
						h == null ? "" : fmt(new Vec3(h.x, h.y, h.z).distanceTo(ev)),
						h == null ? "" : fmt(new Vec3(h.x, h.y, h.z).distanceTo(player)));
					rows.add(row);
					String bad = check(mode, c, h, ev);
					if (bad != null) failures.add(mode + " / " + c.name + ": " + bad);
				}
			}
		} finally {
			context.runOnClient(mc -> mc.getSoundManager().removeListener(listener));
		}
		writeConfig(cfg, "nearby");
		try {
			Files.write(Path.of("boss-sounds-result.csv"), rows, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		rows.forEach(r -> LocalBossSoundsClient.LOG.info("RESULT {}", r));
		if (!failures.isEmpty()) throw new AssertionError("Local Boss Sounds game test failed: " + failures);
		LocalBossSoundsClient.LOG.info("RESULT all {} checks passed", rows.size() - 1);
	}

	/** null if the case behaves as the mode promises, else the reason. */
	static String check(String mode, Case c, Heard h, Vec3 event) {
		boolean near = !c.nether && c.dx < 30;
		switch (mode) {
			case "everywhere":
				// vanilla: every case is heard, right next to the player (2 blocks from the camera)
				return h == null ? "vanilla sound was not played" : null;
			case "off":
				return h != null ? "sound played while off" : null;
			default:
				if (!near) return h != null ? "far or other-dimension sound was played" : null;
				if (h == null) return "near sound was not played";
				double off = new Vec3(h.x, h.y, h.z).distanceTo(event);
				return off > 0.01 ? "near sound was not played at the event (" + fmt(off) + " blocks off)" : null;
		}
	}

	static void writeConfig(Path cfg, String mode) {
		String json = "{\n  \"witherSpawn\": \"" + mode + "\",\n  \"dragonDeath\": \"" + mode + "\",\n  \"endPortalOpen\": \"" + mode + "\"\n}\n";
		try {
			Files.createDirectories(cfg.getParent());
			Files.writeString(cfg, json, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static String fmt(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}
}
