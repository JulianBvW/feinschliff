package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.game.inventory.InventoryMenuScreen;

/**
 * Lets a gesture hand a press back to the game after holding on to it.
 *
 * <p>A drag cannot know at the press whether it is one, so the press is kept
 * back and played to the game on release when it turns out to be an ordinary
 * click. Calling the game's own method rather than rebuilding what it does is
 * the point: swapping, merging, halving and putting down are a hundred lines
 * of vanilla that nobody should own twice.
 */
@Mixin(InventoryMenuScreen.class)
public interface InventoryMenuScreenInvoker {

	@Invoker("mouseClicked")
	void feinschliff$mouseClicked(int mouseX, int mouseY, int button);
}
