package io.github.julianbvw.feinschliff.mc.alpha.mob;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.animal.PigEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.mob.PigRide;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.MobEntityAccessor;

/**
 * The half of the pig that needs the game: who is sitting on what, where they
 * are looking, and the noises a pig makes about it.
 *
 * <p>Nothing here is written down anywhere. A pig's mood lives for as long as
 * somebody is sitting on it, and a world that has never seen this mod cannot
 * tell that one did: the only thing a pig remembers is its saddle, which is
 * vanilla's own tag.
 */
public final class Pigs {

	/** How fast the pig comes round to where the rider is looking, per tick. */
	private static final float TURN = 30.0F;

	/** Full input. What it is worth in blocks per second is decided elsewhere. */
	private static final float FULL = 1.0F;

	private static final int RIGHT_CLICK = 1;

	private static PigEntity ridden;

	private Pigs() {
	}

	/**
	 * Takes the pig's next step for it, and says so. False leaves the animal to
	 * the game, which is every pig but the one being ridden with wheat out --
	 * and that one too, while it is having a moment.
	 */
	public static boolean listens(MobEntity mob) {
		if (!(mob instanceof PigEntity)) {
			return false;
		}

		Minecraft minecraft = FeinschliffClient.minecraft();
		PlayerEntity rider = minecraft == null ? null : minecraft.player;
		if (rider == null || rider.vehicle != mob) {
			if (ridden == mob) {
				ridden = null;
			}
			return false;
		}

		PigEntity pig = (PigEntity)mob;
		if (ridden != pig) {
			ridden = pig;
			PigRide.begin();
		}

		if (!PigRide.tick(holdingWheat(rider))) {
			if (PigRide.tookOffence()) {
				say(pig, "mob.pig", 0.7F, 0.75F);
			}
			return false;
		}

		walk(pig, rider);
		return true;
	}

	/** Whether the click that is being handled has already done something. */
	private static boolean clickWasSpent;

	/**
	 * Remembers that the block under the crosshair answered the click. Opening
	 * a chest from pig-back is opening a chest, not feeding.
	 */
	public static void blockAnswered(boolean answered) {
		clickWasSpent = answered;
	}

	/** A right click while riding: the pig gets the wheat rather than the world. */
	public static void rightClicked(Minecraft minecraft, int button) {
		boolean spent = clickWasSpent;
		clickWasSpent = false;

		if (button != RIGHT_CLICK || spent) {
			return;
		}

		PlayerEntity rider = minecraft.player;
		if (rider == null || !(rider.vehicle instanceof PigEntity) || rider.vehicle != ridden) {
			return;
		}
		// Pointing at the pig itself is how anyone gets off it in this version,
		// and that has to keep working.
		if (minecraft.crosshairTarget != null && minecraft.crosshairTarget.entity == rider.vehicle) {
			return;
		}

		if (!holdingWheat(rider) || !PigRide.feed()) {
			return;
		}

		rider.inventory.removeItem(rider.inventory.selectedSlot, 1);

		PigEntity pig = (PigEntity)rider.vehicle;
		say(pig, "random.eat", 0.8F, 1.0F);
		say(pig, "mob.pig", 0.8F, 1.3F);
	}

	/** True while the pig is being steered, so it must not turn the rider's head. */
	public static boolean steering(Entity vehicle) {
		return vehicle != null && vehicle == ridden && PigRide.enabled();
	}

	/** The push a step gives this mob, as a share of what the game would give it. */
	public static float push(Object mob) {
		return mob == ridden ? PigRide.push(true) : 1.0F;
	}

	/** A saddled pig gives the saddle back when it dies. */
	public static boolean dropsTheSaddle(MobEntity mob) {
		return Settings.PIG_SADDLE_DROP.on() && mob instanceof PigEntity && ((PigEntity)mob).saddled;
	}

	public static int saddleId() {
		return Item.SADDLE.id;
	}

	private static boolean holdingWheat(PlayerEntity rider) {
		ItemStack held = rider.inventory.getSelectedItem();
		return held != null && held.id == Item.WHEAT.id;
	}

	/**
	 * One step in the direction the rider is looking.
	 *
	 * <p>The pig is turned rather than pointed: it comes round at the rate the
	 * game turns its own walkers at, which is what keeps a look over the
	 * shoulder from spinning the animal on the spot.
	 */
	private static void walk(PigEntity pig, PlayerEntity rider) {
		turn(pig, rider.yaw);

		MobEntityAccessor reins = (MobEntityAccessor)pig;
		reins.feinschliff$setForwardSpeed(FULL);
		reins.feinschliff$setSidewaysSpeed(0.0F);

		// The game's own rule for anything walking a route: what you bump into,
		// you try to get over. One block is exactly what a hop clears.
		reins.feinschliff$setJumping(pig.collidingHorizontally);

		if (PigRide.takeLeap()) {
			leap(pig, reins);
		}
	}

	/** The hop a pig makes when it gets something it likes. */
	private static void leap(PigEntity pig, MobEntityAccessor reins) {
		reins.feinschliff$setJumping(true);

		double radians = pig.yaw * Math.PI / 180.0;
		double push = PigRide.leapPush();
		pig.velocityX -= Math.sin(radians) * push;
		pig.velocityZ += Math.cos(radians) * push;
	}

	private static void turn(PigEntity pig, float wanted) {
		float by = wanted - pig.yaw;
		while (by < -180.0F) {
			by += 360.0F;
		}
		while (by >= 180.0F) {
			by -= 360.0F;
		}
		if (by > TURN) {
			by = TURN;
		}
		if (by < -TURN) {
			by = -TURN;
		}
		pig.yaw += by;
	}

	private static void say(PigEntity pig, String sound, float volume, float pitch) {
		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft != null && minecraft.world != null) {
			minecraft.world.playSound(pig, sound, volume, pitch);
		}
	}
}
