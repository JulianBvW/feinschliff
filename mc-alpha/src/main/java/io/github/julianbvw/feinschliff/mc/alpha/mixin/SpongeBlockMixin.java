package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.SpongeBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.mc.alpha.world.Sponges;

/**
 * The sponge, finished.
 *
 * <p>Everything else about the block is left alone: it is as hard as it was,
 * sounds the same, drops itself, blocks water like any solid block, and
 * releasing the cube when the sponge is taken up is the game's own work and
 * already right.
 */
@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin extends Block {

	/**
	 * Mixin does not merge constructors. This one is here so the override below
	 * has a superclass to override from.
	 */
	protected SpongeBlockMixin(int id, Material material) {
		super(id, material);
	}

	/**
	 * The method as it stands walks the cube, asks each of the hundred and
	 * twenty-five positions whether it holds water, and has nothing in the body
	 * of that question. It calls no super and the base is empty, so cancelling
	 * it takes nothing away -- including the hundred and twenty-five unguarded
	 * reads, which is why the replacement is the safer of the two.
	 */
	@Inject(method = "onAdded", at = @At("HEAD"), cancellable = true)
	private void feinschliff$soakUpTheWater(World world, int x, int y, int z, CallbackInfo ci) {
		if (Sponges.soak(world, x, y, z)) {
			ci.cancel();
		}
	}

	/**
	 * Added rather than injected: a sponge inherits this empty from Block and
	 * declares nothing of its own, so there is no method here to inject into.
	 */
	@Override
	public void neighborChanged(World world, int x, int y, int z, int neighborBlock) {
		Sponges.soakAgain(world, x, y, z);
	}
}
