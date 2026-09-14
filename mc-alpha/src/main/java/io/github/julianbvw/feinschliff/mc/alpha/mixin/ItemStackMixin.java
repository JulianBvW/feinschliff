package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.gameplay.Eating;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	/** MobEntity.heal() caps health here, and that literal is the only statement of it. */
	private static final int FEINSCHLIFF_MAX_HEALTH = 20;

	@Shadow
	public abstract Item getItem();

	/**
	 * Right clicking any item goes through here, which is what makes this the
	 * right place: StewItem hands back a bowl no matter what its super call did,
	 * so cancelling FoodItem alone would empty the stew while sparing the food.
	 * Testing the item one level above catches every FoodItem subclass instead.
	 *
	 * <p>Returning the stack unchanged is what the caller reads as "nothing
	 * happened": Minecraft only consumes the item and plays the use animation
	 * when it gets back a different stack or a different stack size.
	 */
	@Inject(method = "startUsing", at = @At("HEAD"), cancellable = true)
	private void feinschliff$keepFoodAtFullHealth(World world, PlayerEntity player,
			CallbackInfoReturnable<ItemStack> cir) {
		if (this.getItem() instanceof FoodItem
				&& Eating.blockedAtFullHealth(player.health, FEINSCHLIFF_MAX_HEALTH)) {
			cir.setReturnValue((ItemStack) (Object) this);
		}
	}
}
