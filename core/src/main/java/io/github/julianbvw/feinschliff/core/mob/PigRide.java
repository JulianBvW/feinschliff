package io.github.julianbvw.feinschliff.core.mob;

import java.util.Random;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * What a pig under a rider is doing, in timers.
 *
 * <p>Three states, and the pig is in exactly one of them: it walks where the
 * rider looks, it is still chewing a piece of wheat and therefore running, or
 * it has decided for a moment that it would rather not. The last one is the
 * point of the whole thing -- a pig is not a vehicle, and a mount that always
 * obeys is a vehicle with legs.
 *
 * <p>Speeds are given in blocks per second and turned into the one thing the
 * game understands, a factor on the push a step gives. A walking player settles
 * at {@link #WALK} blocks a second, so a pig asked for half that gets half the
 * push, and the surface it walks on keeps deciding the rest: ice is still ice.
 *
 * <p>Every stretch of time here is rolled fresh, a third either way, so nothing
 * about the pig arrives on a schedule.
 */
public final class PigRide {

	/**
	 * Where a walking player settles, in blocks a second.
	 *
	 * <p>{@code 0.98 * 0.1 / (1 - 0.546)} blocks a tick: full input, damped by
	 * the tick before the step, against the drag of ordinary ground. Sneaking
	 * is three tenths of the input and lands at 1.3, a pig left alone at 3.0.
	 */
	public static final double WALK = 4.317;

	private static final int TICKS_PER_SECOND = 20;

	/** How far a rolled time may stray from the one that was asked for. */
	private static final double SPREAD = 1.0 / 3.0;

	/** The leap out of a feed, as a share of the speed it buys. */
	private static final double LEAP = 1.2;

	private static final Random RANDOM = new Random();

	private static int chewing;
	private static int ownBusiness;
	private static int untilOwnBusiness;

	private static boolean leapOwed;
	private static boolean grumbled;

	private PigRide() {
	}

	public static boolean enabled() {
		return Settings.PIG_STEER.on();
	}

	/** A rider has just got on, or got on a different pig. */
	public static void begin() {
		chewing = 0;
		ownBusiness = 0;
		leapOwed = false;
		grumbled = false;
		untilOwnBusiness = roll(Settings.PIG_MIND_EVERY.value());
	}

	/**
	 * Moves every timer on by a tick and says whether the pig is taking
	 * directions right now.
	 *
	 * @param wheat whether the rider is holding out wheat. Without it the pig
	 *              goes its own way, but a piece it has already eaten keeps
	 *              working -- putting the wheat away for a moment to place a
	 *              block does not throw the rest of it away
	 */
	public static boolean tick(boolean wheat) {
		if (chewing > 0) {
			chewing--;
		}

		if (!enabled() || !wheat) {
			return false;
		}

		if (ownBusiness > 0) {
			ownBusiness--;
			return false;
		}

		// Nothing interrupts a piece of wheat: what was paid for is delivered.
		if (chewing > 0) {
			return true;
		}

		if (!Settings.PIG_MIND.on()) {
			return true;
		}

		if (untilOwnBusiness > 0) {
			untilOwnBusiness--;
			return true;
		}

		ownBusiness = roll(Settings.PIG_MIND_FOR.value());
		untilOwnBusiness = roll(Settings.PIG_MIND_EVERY.value());
		grumbled = true;
		return false;
	}

	/** True once, on the tick the pig loses interest, so it can grunt about it. */
	public static boolean tookOffence() {
		boolean answer = grumbled;
		grumbled = false;
		return answer;
	}

	/** True while a piece of wheat is still being worked through. */
	public static boolean running() {
		return chewing > 0;
	}

	/**
	 * Takes a piece of wheat, whether or not one is already being chewed.
	 *
	 * <p>A second piece is added to what is left of the first, so feeding again
	 * always buys something -- but never past what a single piece could have
	 * bought at its luckiest. A sack of wheat cannot be turned into a morning
	 * of running.
	 */
	public static boolean feed() {
		if (!enabled() || !Settings.PIG_FEED.on()) {
			return false;
		}

		int most = ticks(Settings.PIG_FEED_SECONDS.value() * (1.0 + SPREAD));
		chewing += roll(Settings.PIG_FEED_SECONDS.value());
		if (chewing > most) {
			chewing = most;
		}

		leapOwed = true;
		return true;
	}

	/** True once, for the hop a pig makes when it gets something nice. */
	public static boolean takeLeap() {
		boolean answer = leapOwed;
		leapOwed = false;
		return answer;
	}

	/** Blocks per second the pig is good for at this moment. */
	public static double pace() {
		return running() ? Settings.PIG_FEED_SPEED.value() : Settings.PIG_SPEED.value();
	}

	/**
	 * The factor on a step's push that produces {@link #pace()}.
	 *
	 * @param steering false while the pig is walking for itself, which it does
	 *                 at its own speed and nobody else's
	 */
	public static float push(boolean steering) {
		return steering ? (float)(pace() / WALK) : 1.0F;
	}

	/** How hard the feeding hop carries forward, in blocks per tick. */
	public static double leapPush() {
		return Settings.PIG_FEED_SPEED.value() / TICKS_PER_SECOND * LEAP;
	}

	/** Seconds into ticks, a third either way. */
	private static int roll(double seconds) {
		return ticks(seconds * (1.0 - SPREAD + RANDOM.nextDouble() * 2.0 * SPREAD));
	}

	private static int ticks(double seconds) {
		int ticks = (int)(seconds * TICKS_PER_SECOND);
		return ticks < 1 ? 1 : ticks;
	}
}
