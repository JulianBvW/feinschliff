package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.item.ItemStack;

/**
 * Reaches the two questions a furnace already answers for itself, so that
 * sorting something into it does not need a second copy of the answer.
 *
 * <p>Both are tables of hardcoded ids in this version and both grow with every
 * release. Asking the furnace keeps them right for free; a table of our own
 * would be wrong by the next version.
 */
@Mixin(FurnaceBlockEntity.class)
public interface FurnaceBlockEntityInvoker {

	/** Burn time in ticks, zero for anything that is not fuel. */
	@Invoker("getFuelTime")
	int feinschliff$fuelTime(ItemStack item);

	/** What that item smelts into, or -1 when it does not smelt. */
	@Invoker("getResult")
	int feinschliff$smeltingResult(int itemId);
}
