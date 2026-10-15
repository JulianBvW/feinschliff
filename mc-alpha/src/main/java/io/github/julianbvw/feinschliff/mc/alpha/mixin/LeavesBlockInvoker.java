package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.LeavesBlock;
import net.minecraft.world.World;

/**
 * Reaches the one step the block already knows how to take, so that letting a
 * leaf go does not need a second copy of it.
 *
 * <p>It matters that this is the game's own method and not two lines rebuilt
 * here: those two lines are a drop and a removal, in that order, and a rebuild
 * that forgot the drop would quietly cost every sapling a decaying wood gives.
 */
@Mixin(LeavesBlock.class)
public interface LeavesBlockInvoker {

	/** Throw what the leaf drops, then turn it into air. */
	@Invoker("breakLeaves")
	void feinschliff$letGo(World world, int x, int y, int z);
}
