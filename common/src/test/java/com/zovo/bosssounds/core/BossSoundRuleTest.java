package com.zovo.bosssounds.core;

import static com.zovo.bosssounds.core.BossSoundRule.Action.MUTE;
import static com.zovo.bosssounds.core.BossSoundRule.Action.PLAY_AT_EVENT;
import static com.zovo.bosssounds.core.BossSoundRule.Action.VANILLA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BossSoundRuleTest {
	@Test void everywhereIsVanillaAtAnyDistance() {
		assertEquals(VANILLA, BossSoundRule.decide(Mode.EVERYWHERE, 5));
		assertEquals(VANILLA, BossSoundRule.decide(Mode.EVERYWHERE, 32));
		assertEquals(VANILLA, BossSoundRule.decide(Mode.EVERYWHERE, 0.4));
	}

	@Test void offAlwaysMutes() {
		assertEquals(MUTE, BossSoundRule.decide(Mode.OFF, 3));
	}

	@Test void nearbyPlaysCloseEventsWhereTheyHappen() {
		assertEquals(PLAY_AT_EVENT, BossSoundRule.decide(Mode.NEARBY, 3));
		assertEquals(PLAY_AT_EVENT, BossSoundRule.decide(Mode.NEARBY, 1.5));
		assertEquals(PLAY_AT_EVENT, BossSoundRule.decide(Mode.NEARBY, 29.9));
	}

	@Test void nearbyMutesEventsTheServerMovedTo32Blocks() {
		assertEquals(MUTE, BossSoundRule.decide(Mode.NEARBY, 30.0));
		assertEquals(MUTE, BossSoundRule.decide(Mode.NEARBY, 32.0));
	}

	@Test void nearbyMutesEventsFromOtherDimensions() {
		assertEquals(MUTE, BossSoundRule.decide(Mode.NEARBY, 0.5));
		assertEquals(MUTE, BossSoundRule.decide(Mode.NEARBY, 1.49));
	}

	@Test void onlyTheThreeGlobalEventsAreTouched() {
		assertTrue(BossSoundRule.isGlobalBossSound(1023));
		assertTrue(BossSoundRule.isGlobalBossSound(1028));
		assertTrue(BossSoundRule.isGlobalBossSound(1038));
		assertFalse(BossSoundRule.isGlobalBossSound(1024));
		assertFalse(BossSoundRule.isGlobalBossSound(1029));
	}
}
