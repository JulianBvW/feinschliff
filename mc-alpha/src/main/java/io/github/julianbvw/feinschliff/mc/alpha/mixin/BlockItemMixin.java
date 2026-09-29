package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.mc.alpha.block.Placements;

/**
 * The one place where a block and the player who placed it are both in hand.
 *
 * <p>The game's own hook for this, {@code updateMetadataOnPlaced}, is given the
 * face that was clicked and no more, which is enough for a torch or a ladder
 * and not enough for anything that should look at you. Here the player is an
 * argument, and the block has just been set, so this wraps that hook rather
 * than replacing it: the ladders and torches get their answer first, and the
 * facing goes on top.
 *
 * <p>Every block the player places comes through here, including all the ones
 * with no facing at all, so what follows has to be cheap and has to let those
 * pass untouched.
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {

	@WrapOperation(
		method = "useOn",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/block/Block;updateMetadataOnPlaced(Lnet/minecraft/world/World;IIII)V"))
	private void feinschliff$turnItTowardsYou(Block block, World world, int x, int y, int z, int face,
			Operation<Void> original, @Local(argsOnly = true) PlayerEntity player) {
		original.call(block, world, x, y, z, face);
		Placements.orient(world, x, y, z, block, player);
	}
}
