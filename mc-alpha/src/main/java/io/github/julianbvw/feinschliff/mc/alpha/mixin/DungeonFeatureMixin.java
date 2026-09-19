package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.DungeonFeature;

import io.github.julianbvw.feinschliff.mc.alpha.world.DungeonChests;

/**
 * A sponge among the saddles.
 *
 * <p>The dungeon itself is not touched: where it sits, how large it is, which
 * mob its spawner names and how many stacks its chest holds are all decided
 * before and after this, out of the generator's own random. This sits on the
 * one call that puts a stack away and changes which stack that is.
 *
 * <p>Wrapping the call rather than the roll it came from is deliberate: the
 * chest is what knows where it stands, and where it stands is what the sponge
 * is decided from.
 */
@Mixin(DungeonFeature.class)
public class DungeonFeatureMixin {

	@WrapOperation(method = "place", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/entity/ChestBlockEntity;setItem(ILnet/minecraft/item/ItemStack;)V"))
	private void feinschliff$slipInASponge(ChestBlockEntity chest, int slot, ItemStack rolled,
			Operation<Void> original) {
		original.call(chest, slot, DungeonChests.loot(chest, slot, rolled));
	}
}
