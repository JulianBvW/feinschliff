package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.mob.player.PlayerInventory;

import io.github.julianbvw.feinschliff.mc.alpha.inventory.PickBlocks;

/**
 * Pick block already searches the whole inventory and then only acts when
 * what it found is on the hotbar. This fills in the other half.
 *
 * <p>The method has exactly one caller in the game, the middle mouse button,
 * so nothing else can be caught by this.
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	@Inject(method = "selectSlot", at = @At("HEAD"), cancellable = true)
	private void feinschliff$fetchItOutOfTheInventory(int block, boolean creative, CallbackInfo ci) {
		if (PickBlocks.handled((PlayerInventory)(Object)this, block)) {
			ci.cancel();
		}
	}
}
