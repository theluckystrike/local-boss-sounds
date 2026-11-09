package com.zovo.bosssounds.core;

/**
 * Decides what to do with a global boss sound that reached the client.
 *
 * The server never sends the real position of a far event. For a player in the same dimension it sends the real
 * position when the event is closer than 32 blocks, and otherwise a point 32 blocks away in the direction of the event.
 * For a player in another dimension it sends the player's own position. So the client can tell "near" from "far" by
 * the distance of the position it got.
 */
public final class BossSoundRule {
	/** Vanilla level event ids for the three global sounds. */
	public static final int WITHER_SPAWN = 1023;
	public static final int DRAGON_DEATH = 1028;
	public static final int END_PORTAL_OPEN = 1038;

	/**
	 * A position this far away or more means the server moved it, so the real event is 32 blocks away or more.
	 * The server rounds to a block and the player moves while the packet travels, so the mark is a bit under 32.
	 */
	public static final double FAR_MARK = 30.0;

	/**
	 * A position closer than this is the server's mark for "this happened in another dimension": it sends the
	 * player's own position then. A wither, a dying dragon or an end portal is never this close to the player.
	 */
	public static final double ELSEWHERE_MARK = 1.5;

	public enum Action { VANILLA, PLAY_AT_EVENT, MUTE }

	private BossSoundRule() {}

	public static boolean isGlobalBossSound(int type) {
		return type == WITHER_SPAWN || type == DRAGON_DEATH || type == END_PORTAL_OPEN;
	}

	/**
	 * @param mode     the mode for this sound
	 * @param distance distance from the player's feet to the center of the block in the packet
	 */
	public static Action decide(Mode mode, double distance) {
		return switch (mode) {
			case EVERYWHERE -> Action.VANILLA;
			case OFF -> Action.MUTE;
			case NEARBY -> (distance < ELSEWHERE_MARK || distance >= FAR_MARK) ? Action.MUTE : Action.PLAY_AT_EVENT;
		};
	}
}
