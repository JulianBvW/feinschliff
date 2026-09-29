package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.StairsBlock;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.block.Placement;

/**
 * A staircase keeps the way it was laid.
 *
 * <p>The method cancelled here is the whole of the version's idea of which way
 * a staircase points: it looks for a staircase above, then for a solid block
 * on one side and none on the other, then for a staircase below, and writes
 * whichever answer it reaches first. It runs for the stair itself and for the
 * eight around it on every neighbour change, which is why a staircase turns
 * while you build beside it.
 *
 * <p>Nothing else of the block is touched. A staircase still turns back into
 * the block it is made of when something solid is set on top of it, and still
 * passes everything else on to that block.
 */
@Mixin(StairsBlock.class)
public class StairsBlockMixin {

	@Inject(method = "updateShape(Lnet/minecraft/world/World;III)V", at = @At("HEAD"), cancellable = true)
	private void feinschliff$leaveItAsItWasLaid(World world, int x, int y, int z, CallbackInfo ci) {
		if (Placement.facesYou()) {
			ci.cancel();
		}
	}
}
