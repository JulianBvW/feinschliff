package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.github.julianbvw.feinschliff.core.gameplay.Eating;
import io.github.julianbvw.feinschliff.mc.alpha.mining.Mining;

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

	/**
	 * Every question of how fast a block comes apart passes through here: the
	 * player's inventory asks the stack in the selected slot, the stack asks its
	 * item, and each item answers for itself. Answering one level above the
	 * items reaches all five tool classes at once, and the sword and the hoe,
	 * which are not tools at all as far as the game's own class tree goes.
	 */
	@ModifyReturnValue(method = "getMiningSpeed", at = @At("RETURN"))
	private float feinschliff$giveTheBlockItsTool(float vanilla, Block block) {
		return Mining.speed((ItemStack) (Object) this, block, vanilla);
	}
}
