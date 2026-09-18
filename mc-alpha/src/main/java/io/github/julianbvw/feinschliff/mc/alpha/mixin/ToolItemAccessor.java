package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ToolItem;

/**
 * What a pickaxe, axe or shovel is made of, as the number the game keeps it as:
 * wood 0, stone 1, iron 2, diamond 3, and gold, which shares wood's.
 *
 * <p>A protected field, and the mining code lives outside the item package.
 */
@Mixin(ToolItem.class)
public interface ToolItemAccessor {

	@Accessor("tier")
	int feinschliff$getTier();
}
