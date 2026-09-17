package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.Screen;

/**
 * Reaches the list of buttons a screen draws and hands clicks to.
 *
 * <p>A shadow would not do, and not only because the field belongs to a class
 * above the one being changed: the name in a shadow is remapped by looking it
 * up in the targeted class, so an inherited one is left as written and then
 * found nowhere at all.
 */
@Mixin(Screen.class)
public interface ScreenAccessor {

	@Accessor("buttons")
	List feinschliff$buttons();
}
