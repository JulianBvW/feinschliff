package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.client.SurvivalInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.mc.alpha.mining.SilkTouches;

@Mixin(SurvivalInteractionManager.class)
public class SurvivalInteractionManagerMixin {

	/**
	 * The one place in the whole client where a block a player has broken gives
	 * anything up, and the only one where the tool that broke it is still in
	 * reach. The block has already been taken out of the world by the time this
	 * runs, so all that is left to decide is what lands on the floor.
	 *
	 * <p>The call is wrapped rather than the drop itself, because the game
	 * reaches the floor by several routes -- the snow layer writes its own
	 * snowball, everything else goes through a count and a roll -- and only the
	 * call above them all is a single answer.
	 */
	@WrapOperation(
		method = "finishMiningBlock(IIII)Z",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/block/Block;afterMinedByPlayer(Lnet/minecraft/world/World;IIII)V"))
	private void feinschliff$takeTheBlockWhole(Block block, World world, int x, int y, int z, int metadata,
			Operation<Void> original, @Local(ordinal = 0) ItemStack tool) {
		if (!SilkTouches.handled(world, x, y, z, block, tool)) {
			original.call(block, world, x, y, z, metadata);
		}
	}
}
