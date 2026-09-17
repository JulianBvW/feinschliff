package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.game.inventory.InventoryMenuScreen;

import io.github.julianbvw.feinschliff.mc.alpha.inventory.Drags;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.Gathers;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.QuickMoves;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.Scrolls;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.Sorts;
import io.github.julianbvw.feinschliff.mc.alpha.inventory.Sweeps;

/**
 * The one place every click in every menu goes through. This class is the base
 * of the player inventory, the chest, the furnace and the crafting table, so a
 * single injection covers all four.
 */
@Mixin(InventoryMenuScreen.class)
public class InventoryMenuScreenMixin {

	@Shadow
	protected List menuSlots;

	/**
	 * A gesture is not a click with something suppressed, it is a different
	 * move, so this cancels rather than conditions. The cost is one
	 * CallbackInfo per click in an open menu -- not per frame, not per tick.
	 *
	 * <p>Whatever is skipped here includes the markDirty() vanilla does at the
	 * end of every click, which is the only thing that ever tells a furnace it
	 * has changed. Every gesture therefore marks every slot it touches.
	 *
	 * <p>The order matters: a shift-click is never a double click, and a fast
	 * second click on the same slot is a double click rather than the start of
	 * anything else.
	 */
	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void feinschliff$readTheClickBeforeTheGameDoes(int mouseX, int mouseY, int button, CallbackInfo ci) {
		// A press handed back to the game is the game's alone.
		if (Drags.replaying()) {
			return;
		}

		if (QuickMoves.handled(this.menuSlots, mouseX, mouseY, button)
				|| Gathers.handled(this.menuSlots, mouseX, mouseY, button)
				|| Sorts.handled(this.menuSlots, mouseX, mouseY, button)
				|| Drags.startedOn(this.menuSlots, mouseX, mouseY, button)) {
			ci.cancel();
		}
	}

	/**
	 * The game calls this for every mouse event that is not a press, which
	 * includes plain pointer movement with the button reading -1. That makes
	 * one empty method both the movement hook and the release hook, and it is
	 * empty: there is nothing here to suppress, so nothing is cancelled.
	 */
	@Inject(method = "mouseReleased", at = @At("HEAD"))
	private void feinschliff$followTheGesture(int mouseX, int mouseY, int button, CallbackInfo ci) {
		Scrolls.released(this.menuSlots, mouseX, mouseY, button);
		Drags.released(this.menuSlots, mouseX, mouseY, button);
		Sweeps.released(this.menuSlots, mouseX, mouseY, button);
	}
}
