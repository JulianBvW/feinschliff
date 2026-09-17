package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.game.inventory.InventoryMenuScreen;

import io.github.julianbvw.feinschliff.mc.alpha.inventory.QuickMoves;

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
	 * A shift-click is not a click with something suppressed, it is a
	 * different move, so this cancels rather than conditions. The cost is one
	 * CallbackInfo per click in an open menu -- not per frame, not per tick.
	 *
	 * <p>Whatever is skipped here includes the markDirty() vanilla does at the
	 * end of every click, which is the only thing that ever tells a furnace it
	 * has changed. Quick moves therefore mark every slot they touch.
	 */
	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void feinschliff$sendStackAwayOnShiftClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
		if (QuickMoves.handled(this.menuSlots, mouseX, mouseY, button)) {
			ci.cancel();
		}
	}
}
