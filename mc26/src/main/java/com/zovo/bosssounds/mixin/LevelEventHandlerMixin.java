package com.zovo.bosssounds.mixin;

import com.zovo.bosssounds.LocalBossSoundsClient;
import com.zovo.bosssounds.core.BossSoundRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelEventHandler.class)
public abstract class LevelEventHandlerMixin {
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private ClientLevel level;

	@Inject(method = "globalLevelEvent", at = @At("HEAD"), cancellable = true)
	private void localbosssounds$route(int type, BlockPos pos, int data, CallbackInfo ci) {
		if (!BossSoundRule.isGlobalBossSound(type)) return;
		LocalPlayer player = this.minecraft.player;
		if (player == null) return;
		Vec3 at = Vec3.atCenterOf(pos);
		BossSoundRule.Action action = BossSoundRule.decide(LocalBossSoundsClient.CONFIG.modeFor(type), player.position().distanceTo(at));
		if (action == BossSoundRule.Action.VANILLA) return;
		ci.cancel();
		if (action == BossSoundRule.Action.MUTE) return;
		// Same sound, category and volume as vanilla, but played from where the event happened, so it fades with distance.
		SoundEvent sound;
		float volume = 1.0F;
		if (type == BossSoundRule.WITHER_SPAWN) {
			sound = SoundEvents.WITHER_SPAWN;
		} else if (type == BossSoundRule.END_PORTAL_OPEN) {
			sound = SoundEvents.END_PORTAL_SPAWN;
		} else {
			sound = SoundEvents.ENDER_DRAGON_DEATH;
			volume = 5.0F;
		}
		this.level.playLocalSound(at.x, at.y, at.z, sound, SoundSource.HOSTILE, volume, 1.0F, false);
	}
}
