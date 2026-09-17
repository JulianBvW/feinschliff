package io.github.julianbvw.feinschliff.mc.alpha.inventory;

import java.util.List;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.screen.game.inventory.CraftingResultSlot;
import net.minecraft.client.gui.screen.game.inventory.InventoryMenuSlot;
import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import io.github.julianbvw.feinschliff.core.inventory.SlotRole;
import io.github.julianbvw.feinschliff.core.inventory.Slots;
import io.github.julianbvw.feinschliff.mc.alpha.mixin.FurnaceBlockEntityInvoker;

/** The slots of one open menu, answering for the version they belong to. */
public final class MenuSlots implements Slots {

	private static final int HOTBAR_SIZE = 9;

	private static final int FURNACE_INPUT_ID = 0;
	private static final int FURNACE_FUEL_ID = 1;

	private static final int DOES_NOT_SMELT = -1;

	private final List slots;
	private final PlayerInventory player;

	public MenuSlots(List slots, PlayerInventory player) {
		this.slots = slots;
		this.player = player;
	}

	public InventoryMenuSlot slot(int index) {
		return (InventoryMenuSlot)this.slots.get(index);
	}

	/**
	 * The slot under the pointer, or {@code -1} when there is none.
	 *
	 * <p>Asks the same public hit test vanilla's own private lookup asks, and
	 * in the same order, so the two can never disagree about which of two
	 * slots overlapping by a pixel was meant.
	 */
	public int under(int mouseX, int mouseY) {
		for (int index = 0; index < this.slots.size(); index++) {
			if (slot(index).mouseClicked(mouseX, mouseY)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Every slot that looks like the player's really belongs to the player who
	 * is standing here now.
	 *
	 * <p>A screen keeps the inventory it was built with. Dying and respawning
	 * with the inventory open hands the player a new one, and the old one may
	 * even be a different length -- which would move the line between the
	 * armour and the rest, and turn a helmet slot into a storage slot.
	 */
	public boolean belongToThePlayer() {
		for (int index = 0; index < this.slots.size(); index++) {
			Inventory inventory = slot(index).inventory;
			if (inventory instanceof PlayerInventory && inventory != this.player) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int count() {
		return this.slots.size();
	}

	@Override
	public SlotRole role(int index) {
		InventoryMenuSlot slot = slot(index);
		if (slot instanceof CraftingResultSlot) {
			return SlotRole.RESULT;
		}

		Inventory inventory = slot.inventory;
		if (inventory instanceof PlayerInventory) {
			// The same line the inventory itself draws when it decides which
			// of its arrays a slot number means. Its length differs between a
			// player who has been saved once and one who has not, so it is
			// read and never assumed.
			if (slot.id >= ((PlayerInventory)inventory).items.length) {
				return SlotRole.ARMOUR;
			}
			return slot.id < HOTBAR_SIZE ? SlotRole.HOTBAR : SlotRole.MAIN;
		}

		if (inventory instanceof CraftingInventory) {
			return SlotRole.CRAFTING;
		}

		if (inventory instanceof FurnaceBlockEntity) {
			if (slot.id == FURNACE_INPUT_ID) {
				return SlotRole.FURNACE_INPUT;
			}
			return slot.id == FURNACE_FUEL_ID ? SlotRole.FURNACE_FUEL : SlotRole.FURNACE_OUTPUT;
		}

		return SlotRole.OTHER;
	}

	@Override
	public boolean empty(int index) {
		return slot(index).getItem() == null;
	}

	@Override
	public int size(int index) {
		ItemStack stack = slot(index).getItem();
		return stack == null ? 0 : stack.size;
	}

	@Override
	public boolean stackable(int a, int b) {
		ItemStack first = slot(a).getItem();
		ItemStack second = slot(b).getItem();
		if (first == null || second == null) {
			return false;
		}
		// Vanilla's own click compares the id alone, which is safe here only
		// because everything carrying metadata in this version is a damaged
		// tool and tools do not stack. Comparing both is the same answer today
		// and the right one once dyes and coloured wool exist.
		return first.id == second.id && first.metadata == second.metadata;
	}

	@Override
	public int capacity(int to, int from) {
		return capacityFor(to, slot(from).getItem(), false);
	}

	@Override
	public int capacityPointedAt(int to, int from) {
		return capacityFor(to, slot(from).getItem(), true);
	}

	private int capacityFor(int to, ItemStack stack, boolean intoCrafting) {
		// An id with no item behind it comes out of a hand-edited save and
		// would throw in every question asked below.
		if (stack == null || stack.getItem() == null) {
			return 0;
		}

		InventoryMenuSlot target = slot(to);
		if (!takes(to, stack, intoCrafting) || !target.isItemAllowed(stack)) {
			return 0;
		}

		int perStack = stack.getMaxSize();
		int perSlot = target.inventory.getMaxStackSize();
		return perStack < perSlot ? perStack : perSlot;
	}

	@Override
	public void move(int from, int to, int amount) {
		InventoryMenuSlot source = slot(from);
		InventoryMenuSlot target = slot(to);
		ItemStack stack = source.getItem();
		if (stack == null || amount <= 0 || amount > stack.size) {
			throw new IllegalStateException("refusing to move " + amount + " out of a slot holding "
				+ (stack == null ? "nothing" : String.valueOf(stack.size)));
		}

		// removeItem is how vanilla takes items out, and it is what makes a
		// crafting grid work out its result again. It does not always hand
		// back what it was asked for though: a result slot gives up its whole
		// stack whatever the number says. Anything beyond the amount goes
		// straight back where it came from.
		ItemStack taken = source.inventory.removeItem(source.id, amount);
		if (taken == null) {
			return;
		}
		if (taken.size > amount) {
			source.setItem(taken.split(taken.size - amount));
		}
		source.markDirty();

		ItemStack existing = target.getItem();
		if (existing == null) {
			target.setItem(taken);
		} else {
			existing.size += taken.size;
			// Vanilla marks the clicked slot once at the end of every click,
			// and this click never reaches that line. A furnace marks itself
			// nowhere at all, so without this an ore put into a cold one is
			// gone the next time its chunk is unloaded.
			target.markDirty();
		}
	}

	@Override
	public int cursorSize() {
		ItemStack held = this.player.cursorItem;
		return held == null ? 0 : held.size;
	}

	@Override
	public int cursorMax() {
		ItemStack held = this.player.cursorItem;
		// The hand is not part of an inventory, so only the item itself has a
		// say in how much of it can be held at once.
		return held == null || held.getItem() == null ? 0 : held.getMaxSize();
	}

	@Override
	public boolean cursorStackable(int index) {
		ItemStack held = this.player.cursorItem;
		ItemStack stack = slot(index).getItem();
		if (held == null || stack == null) {
			return false;
		}
		return held.id == stack.id && held.metadata == stack.metadata;
	}

	@Override
	public void swap(int a, int b) {
		InventoryMenuSlot first = slot(a);
		InventoryMenuSlot second = slot(b);

		// Both are read before either is written, because one of them is
		// about to be overwritten with the other.
		ItemStack held = first.getItem();
		first.setItem(second.getItem());
		second.setItem(held);
	}

	@Override
	public int cursorCapacity(int index) {
		return capacityFor(index, this.player.cursorItem, true);
	}

	@Override
	public void fromCursor(int to, int amount) {
		ItemStack held = this.player.cursorItem;
		if (held == null || amount <= 0 || amount > held.size) {
			throw new IllegalStateException("refusing to put down " + amount + " out of a hand holding "
				+ (held == null ? "nothing" : String.valueOf(held.size)));
		}

		InventoryMenuSlot target = slot(to);
		ItemStack existing = target.getItem();
		if (existing == null) {
			// split takes the amount off the held stack and hands back a stack
			// of exactly that size, which is what setItem wants.
			target.setItem(held.split(amount));
		} else {
			existing.size += amount;
			held.size -= amount;
			// Vanilla marks the clicked slot once at the end of every click,
			// and a click that ends here never reaches that line.
			target.markDirty();
		}

		// A stack of nothing survives saving and is swallowed without a word
		// the next time something is picked up.
		if (held.size <= 0) {
			this.player.cursorItem = null;
		}
	}

	@Override
	public void toCursor(int from, int amount) {
		ItemStack held = this.player.cursorItem;
		InventoryMenuSlot source = slot(from);
		ItemStack stack = source.getItem();
		if (stack == null || amount <= 0 || amount > stack.size) {
			throw new IllegalStateException("refusing to take " + amount + " out of a slot holding "
				+ (stack == null ? "nothing" : String.valueOf(stack.size)));
		}

		ItemStack taken = source.inventory.removeItem(source.id, amount);
		if (taken == null) {
			return;
		}
		if (taken.size > amount) {
			source.setItem(taken.split(taken.size - amount));
		}
		// Vanilla marks the clicked slot once at the end of every click, and a
		// click that ends here never reaches that line.
		source.markDirty();

		if (held == null) {
			// An empty hand takes the whole stack rather than adding to
			// nothing. That is what makes taking back what was just put down
			// possible at all: by then the hand is usually empty.
			this.player.cursorItem = taken;
		} else {
			held.size += taken.size;
		}
	}

	/** Whether the slot still holds the kind of thing it was handed. */
	public boolean holds(int index, int id, int metadata) {
		ItemStack stack = slot(index).getItem();
		return stack != null && stack.id == id && stack.metadata == metadata;
	}

	/** What the hand is holding, {@code null} when it is empty. */
	public ItemStack cursorItem() {
		return this.player.cursorItem;
	}

	/**
	 * What a slot in this role is willing to be handed, beyond what it says
	 * itself.
	 *
	 * <p>The crafting grid is the one answer that depends on who is asking. A
	 * stack sent away has no business there -- there is no sensible square to
	 * pick. A hand laid on a square has picked it.
	 */
	private boolean takes(int to, ItemStack stack, boolean intoCrafting) {
		SlotRole role = role(to);
		if (role == SlotRole.FURNACE_INPUT) {
			return furnace(to).feinschliff$smeltingResult(stack.getItem().id) != DOES_NOT_SMELT;
		}
		if (role == SlotRole.FURNACE_FUEL) {
			return furnace(to).feinschliff$fuelTime(stack) > 0;
		}
		// The furnace hands its output to the player, never the other way
		// round, and vanilla does not stop anyone: the output slot allows
		// everything, and a wrong item in it stalls the furnace for good.
		// The result and the crafting grid are likewise not places to put
		// things from here.
		if (role == SlotRole.CRAFTING) {
			return intoCrafting;
		}
		return role != SlotRole.FURNACE_OUTPUT && role != SlotRole.RESULT;
	}

	private FurnaceBlockEntityInvoker furnace(int index) {
		return (FurnaceBlockEntityInvoker)slot(index).inventory;
	}
}
