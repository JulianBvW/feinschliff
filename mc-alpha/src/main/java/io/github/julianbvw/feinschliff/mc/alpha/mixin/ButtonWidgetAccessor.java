package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * Reaches a button's width, which it keeps to itself.
 *
 * <p>Buttons are drawn as two halves of the same texture and hit-tested
 * against the same number, so changing this is all it takes to make a narrow
 * button that looks and behaves like a wide one.
 */
@Mixin(ButtonWidget.class)
public interface ButtonWidgetAccessor {

	@Accessor("width")
	int feinschliff$width();

	@Accessor("width")
	void feinschliff$setWidth(int width);
}
