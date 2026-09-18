package io.github.julianbvw.feinschliff.mc.alpha.mining;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

import io.github.julianbvw.feinschliff.core.mining.ToolKind;
import io.github.julianbvw.feinschliff.core.mining.Tools;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.ToolItemAccessor;

/**
 * Names the tool in hand for the core, which deals only in kinds, tiers and
 * block ids.
 */
public final class Mining {

	private Mining() {
	}

	/**
	 * How fast that stack gets through that block.
	 *
	 * @param vanilla what the item answered, handed straight back for anything
	 *                that is not a tool
	 */
	public static float speed(ItemStack stack, Block block, float vanilla) {
		if (!Tools.anyOn()) {
			return vanilla;
		}

		Item item = Item.BY_ID[stack.id];
		ToolKind kind = kindOf(item);
		if (kind == null) {
			return vanilla;
		}

		return Tools.speed(kind, tierOf(item, kind), golden(item), block.id, vanilla);
	}

	/** Whether the block gives anything up for the tool in hand. */
	public static boolean drops(Block block, boolean vanilla) {
		return Tools.drops(block.id, vanilla);
	}

	private static ToolKind kindOf(Item item) {
		if (item instanceof PickaxeItem) {
			return ToolKind.PICKAXE;
		}
		if (item instanceof AxeItem) {
			return ToolKind.AXE;
		}
		if (item instanceof ShovelItem) {
			return ToolKind.SHOVEL;
		}
		if (item instanceof HoeItem) {
			return ToolKind.HOE;
		}
		if (item instanceof SwordItem) {
			return ToolKind.SWORD;
		}
		return null;
	}

	/**
	 * A sword's tier is never asked for, because a blade cuts wool at the same
	 * rate whatever it is made of, the way shears do.
	 */
	private static int tierOf(Item item, ToolKind kind) {
		if (item instanceof ToolItem) {
			return ((ToolItemAccessor)item).feinschliff$getTier();
		}
		return kind == ToolKind.HOE ? hoeTier(item) : 0;
	}

	/**
	 * A hoe keeps no tier of its own: it was never meant to dig, so the number
	 * it was built with is spent on its durability and then dropped. The golden
	 * hoe is the odd one -- the game gives it stone's durability -- and it
	 * belongs with wood here, where gold belongs.
	 */
	private static int hoeTier(Item item) {
		if (item == Item.STONE_HOE) {
			return 1;
		}
		if (item == Item.IRON_HOE) {
			return 2;
		}
		if (item == Item.DIAMOND_HOE) {
			return 3;
		}
		return 0;
	}

	/** Gold sits at wood's tier, so the tier alone cannot tell the two apart. */
	private static boolean golden(Item item) {
		return item == Item.GOLDEN_PICKAXE
			|| item == Item.GOLDEN_AXE
			|| item == Item.GOLDEN_SHOVEL
			|| item == Item.GOLDEN_HOE
			|| item == Item.GOLDEN_SWORD;
	}
}
