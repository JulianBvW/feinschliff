package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import net.minecraft.block.LeavesBlock;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.mc.alpha.block.Leaves;

/**
 * A leaf with no trunk anywhere near it goes.
 *
 * <p>One more reason to go, put in front of the three the block already has.
 * When the leaf stays, nothing happens here and the whole of the version's own
 * tick runs after it, which is the point: the numbers it keeps in a leaf's
 * metadata are Mojang's, and later versions read those same numbers as the kind
 * of tree a leaf came from. Writing them ourselves would cost that.
 *
 * <p>Cancelling once the leaf is gone is not tidiness. The tick's first branch
 * reads the metadata of the position, finds the zero that air has, and writes a
 * one back into it -- metadata on a block of air.
 */
@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

	@Inject(method = "tick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
		at = @At("HEAD"), cancellable = true)
	private void feinschliff$goWithoutATrunkNear(World world, int x, int y, int z,
			Random random, CallbackInfo ci) {
		if (Leaves.letGo(world, x, y, z)) {
			ci.cancel();
		}
	}
}
