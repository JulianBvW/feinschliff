package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.player.PlayerInventory;

import io.github.julianbvw.feinschliff.mc.alpha.inventory.PickBlocks;
import io.github.julianbvw.feinschliff.mc.alpha.mining.Mining;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	/**
	 * Pick block already searches the whole inventory and then only acts when
	 * what it found is on the hotbar. This fills in the other half.
	 *
	 * <p>The method has exactly one caller in the game, the middle mouse button,
	 * so nothing else can be caught by this.
	 */
	@Inject(method = "selectSlot", at = @At("HEAD"), cancellable = true)
	private void feinschliff$fetchItOutOfTheInventory(int block, boolean creative, CallbackInfo ci) {
		if (PickBlocks.handled((PlayerInventory)(Object)this, block)) {
			ci.cancel();
		}
	}

	/**
	 * Whether what is held is allowed to bring the block home. The game only
	 * asks for stone, iron and snow and waves everything else through, so this
	 * can turn a no into a yes and never the other way round.
	 *
	 * <p>The same answer also decides the speed: a block being mined without
	 * the tool it demands goes at a hundredth of the rate.
	 */
	@ModifyReturnValue(method = "canMineBlock", at = @At("RETURN"))
	private boolean feinschliff$plankIsNotStone(boolean vanilla, Block block) {
		return Mining.drops(block, vanilla);
	}
}
