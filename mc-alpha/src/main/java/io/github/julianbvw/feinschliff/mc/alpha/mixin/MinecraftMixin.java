package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.lwjgl.input.Keyboard;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.camera.Freecam;
import io.github.julianbvw.feinschliff.core.config.Settings;
import io.github.julianbvw.feinschliff.core.movement.Fly;
import io.github.julianbvw.feinschliff.core.window.Borderless;
import io.github.julianbvw.feinschliff.core.window.Quit;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;
import io.github.julianbvw.feinschliff.mc.alpha.mob.Pigs;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.Drops;
import io.github.julianbvw.feinschliff.mc.alpha.movement.FlyPhysics;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	/**
	 * Startup. HEAD of init() is the earliest useful point: it runs before
	 * Display.create() and before GameOptions is built, so the mod's own
	 * settings are read and answering before anything can ask for them.
	 */
	@Inject(method = "init", at = @At("HEAD"))
	private void feinschliff$bootstrap(CallbackInfo ci) {
		FeinschliffClient.bootstrap((Minecraft)(Object)this);
	}

	/**
	 * The client tick hook, 20 times a second. TAIL rather than HEAD so that
	 * anything reacting to a hotkey sees the state the vanilla tick produced.
	 */
	@Inject(method = "tick", at = @At("TAIL"))
	private void feinschliff$onTick(CallbackInfo ci) {
		Feinschliff.clientTick();
	}

	/**
	 * The end of the game loop, and the end of the game: vanilla's finally is
	 * empty here, and the applet frame the mod loader wraps the game in does
	 * not exit either. Nothing is left running that would end the process on
	 * its own, so without this it stands around until the loader's watchdog
	 * halts it half a minute later.
	 *
	 * <p>TAIL is the last return. A game that failed to start leaves through
	 * an earlier one and keeps its crash report on screen.
	 */
	@Inject(method = "run", at = @At("TAIL"))
	private void feinschliff$exitWhenTheGameEnds(CallbackInfo ci) {
		if (!Quit.endsTheProcess()) {
			return;
		}
		System.exit(0);
	}

	/**
	 * The fullscreen key, which this version answers badly: it switches the
	 * monitor's display mode and then only tells the game about the new size
	 * when a screen happens to be open, so leaving the pause menu out of it
	 * leaves the picture in a corner at the old size.
	 */
	@Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
	private void feinschliff$borderlessInsteadOfFullscreen(CallbackInfo ci) {
		if (!Borderless.enabled()) {
			return;
		}
		ci.cancel();
		Borderless.toggle();
	}

	/**
	 * The size check at the end of every frame, which follows the awt canvas
	 * the game is normally drawn into. While the window is borderless it is
	 * not drawn into that canvas, and the canvas keeps the size it had, so
	 * this would pull the picture back down to it every frame.
	 *
	 * <p>The ordinal picks the second of the two reads in run(). The first
	 * belongs to a branch that drops out of fullscreen when the window loses
	 * focus, which has nothing to do with this and should keep believing that
	 * no fullscreen is on.
	 */
	@ModifyExpressionValue(
		method = "run",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;fullscreen:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 1))
	private boolean feinschliff$keepBorderlessSize(boolean fullscreen) {
		return fullscreen || Borderless.active();
	}

	/**
	 * The hotbar's share of the mouse wheel. While the free camera is out there
	 * is no hotbar to scroll, so the wheel sets the flying speed instead and the
	 * game is told the wheel did not move.
	 */
	@ModifyExpressionValue(
		method = "tick",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
	private int feinschliff$freecamSpeedFromWheel(int wheelDelta) {
		if (wheelDelta == 0 || !Freecam.active()) {
			return wheelDelta;
		}
		Freecam.changeSpeed(wheelDelta);
		return 0;
	}

	/**
	 * The only key test in run(): F6 draws the profiler chart while it is held.
	 * A camera on that key would make the chart flash on every switch.
	 */
	@ModifyExpressionValue(
		method = "run",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
	private boolean feinschliff$hideProfilerChart(boolean pressed) {
		return pressed && !Freecam.boundTo(Keyboard.KEY_F6);
	}

	/**
	 * Mining, building, attacking and using items all pass through here. The
	 * crosshair stays with the player while the camera is away, so a click
	 * would land on a block the player cannot see.
	 */
	/**
	 * A right click from the back of a pig. TAIL rather than HEAD: by the time
	 * a click gets this far, whatever else it was going to do has been done,
	 * and a click in freecam never arrives at all because that guard cancels
	 * the whole method further up.
	 */
	@Inject(method = "handleMouseClick", at = @At("TAIL"))
	private void feinschliff$feedThePig(int button, CallbackInfo ci) {
		Pigs.rightClicked((Minecraft)(Object)this, button);
	}

	/**
	 * Whether the block under the crosshair took the click for itself. With
	 * wheat in hand that only happens for something that opens, which is
	 * exactly the click that must not cost a piece of it.
	 */
	@ModifyExpressionValue(
		method = "handleMouseClick",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/ClientPlayerInteractionManager;useBlock(Lnet/minecraft/entity/mob/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)Z"))
	private boolean feinschliff$noteTheBlockTookIt(boolean answered) {
		Pigs.blockAnswered(answered);
		return answered;
	}

	@Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
	private void feinschliff$blockClicksInFreecam(int button, CallbackInfo ci) {
		if (Freecam.active()) {
			ci.cancel();
		}
	}

	/** Same for the held mouse button, which is what actually breaks blocks. */
	@ModifyVariable(method = "handleMouseDown", at = @At("HEAD"), argsOnly = true)
	private boolean feinschliff$blockMiningInFreecam(boolean holdingAttack) {
		return holdingAttack && !Freecam.active();
	}

	@Inject(method = "handlePickBlock", at = @At("HEAD"), cancellable = true)
	private void feinschliff$blockPickBlockInFreecam(CallbackInfo ci) {
		if (Freecam.active()) {
			ci.cancel();
		}
	}

	/**
	 * The only write to the selected slot in tick(), and therefore exactly the
	 * number keys. Changing what the body holds belongs to the body.
	 *
	 * <p>The opcode matters: the same field is read a few lines earlier, where
	 * the drop key looks up what to throw.
	 */
	@WrapWithCondition(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/entity/mob/player/PlayerInventory;selectedSlot:I",
			opcode = Opcodes.PUTFIELD))
	private boolean feinschliff$blockHotbarKeysInFreecam(PlayerInventory inventory, int slot) {
		return !Freecam.active();
	}

	/**
	 * The drop key, which takes the item out of the inventory and then hands it
	 * to dropItem. Stopping only the second half would destroy the item, so the
	 * first half is what has to be skipped -- and dropItem ignores a null stack,
	 * which is the whole of the vanilla behaviour that is left to happen.
	 *
	 * <p>Dropping at a body nobody is looking at is how items get lost.
	 */
	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/player/PlayerInventory;removeItem(II)Lnet/minecraft/item/ItemStack;"))
	private ItemStack feinschliff$keepItemInFreecam(PlayerInventory inventory, int slot, int amount,
			Operation<ItemStack> removeItem) {
		return Freecam.active() ? null : removeItem.call(inventory, slot, amount);
	}

	/**
	 * Leaving a world, loading another one, quitting to the title screen. The
	 * camera has nothing to be relative to any more.
	 */
	@Inject(method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;)V", at = @At("HEAD"))
	private void feinschliff$stopFreecam(World world, String message, CallbackInfo ci) {
		Freecam.stop();
		Fly.stop();

		// The player this flight belonged to is about to be replaced, so there
		// is nothing left to hand the movement back to.
		FlyPhysics.forget();
	}

	/**
	 * How many the drop key throws, and nothing else about it.
	 *
	 * <p>The line this sits in is
	 * {@code dropItem(inventory.removeItem(selectedSlot, 1), false)}, and the
	 * amount is the only safe place in it to intervene. Suppressing the throw
	 * would leave removeItem to run anyway -- the stack would be out of the
	 * inventory and nowhere else, which is not a cancelled action but a
	 * destroyed item.
	 *
	 * <p>It is also the one removeItem call in the whole class, so this needs
	 * neither a slice nor an ordinal to stay on target.
	 */
	@ModifyArg(method = "tick",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/player/PlayerInventory;removeItem(II)Lnet/minecraft/item/ItemStack;"),
		index = 1)
	private int feinschliff$dropTheWholeStackWithControl(int amount) {
		return Drops.amount(amount);
	}
}
