package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import java.io.InputStream;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.menu.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

import io.github.julianbvw.feinschliff.core.window.Quit;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;
import io.github.julianbvw.feinschliff.mc.alpha.texture.TexturePacks;

/** The title screen, which in this version has no way out of the game on it. */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	/** The screen hands out 0 to 3, and every branch it has tests for those. */
	private static final int FEINSCHLIFF_QUIT_ID = 100;

	/** Vanilla's id for Options..., the button this one moves in beside. */
	private static final int FEINSCHLIFF_OPTIONS_ID = 0;

	/** Between the two halves, so together they still cover one button's width. */
	private static final int FEINSCHLIFF_GAP = 4;

	/**
	 * TAIL, because the screen builds its buttons there and reaches for two of
	 * them by position on the way. By the time this runs it has finished doing
	 * that, so a longer list is nothing to it.
	 */
	@Inject(method = "init", at = @At("TAIL"))
	private void feinschliff$addQuitButton(CallbackInfo ci) {
		if (!Quit.enabled()) {
			return;
		}

		List buttons = ((ScreenAccessor)this).feinschliff$buttons();
		ButtonWidget options = feinschliff$button(buttons, FEINSCHLIFF_OPTIONS_ID);
		if (options == null) {
			return;
		}

		int whole = ((ButtonWidgetAccessor)options).feinschliff$width();
		int half = (whole - FEINSCHLIFF_GAP) / 2;

		ButtonWidget quit = new ButtonWidget(FEINSCHLIFF_QUIT_ID,
			options.x + half + FEINSCHLIFF_GAP, options.y, "Quit Game");
		((ButtonWidgetAccessor)options).feinschliff$setWidth(half);
		((ButtonWidgetAccessor)quit).feinschliff$setWidth(half);
		buttons.add(quit);
	}

	/**
	 * The screen tests the id of every button it is handed, so one it does not
	 * know falls through untouched. Cancelling anyway says which of the two is
	 * meant to happen.
	 */
	@Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
	private void feinschliff$quitWhenAsked(ButtonWidget button, CallbackInfo ci) {
		if (button.id != FEINSCHLIFF_QUIT_ID) {
			return;
		}
		ci.cancel();

		Minecraft minecraft = FeinschliffClient.minecraft();
		if (minecraft == null) {
			return;
		}

		// Ends the game loop rather than the process. Leaving through the
		// bottom of run() is the one way out the mod already has, and this
		// screen has no world open for it to leave behind.
		Quit.request();
		minecraft.stop();
	}

	private static ButtonWidget feinschliff$button(List buttons, int id) {
		for (int index = 0; index < buttons.size(); index++) {
			ButtonWidget button = (ButtonWidget)buttons.get(index);
			if (button.id == id) {
				return button;
			}
		}
		return null;
	}

	/**
	 * The splash line over the logo is picked from a list the screen reads in
	 * its constructor. It is the only thing a pack can replace here that is
	 * not an image, and the one file of the forty this version asks for that
	 * is text.
	 *
	 * <p>The game wraps this read in a catch of its own and falls back to a
	 * placeholder, so nothing here can cost more than a line of nonsense over
	 * the logo.
	 */
	@WrapOperation(
		method = "<init>",
		at = @At(value = "INVOKE",
			target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"),
		require = 1)
	private InputStream feinschliff$splashesFromThePack(Class<?> owner, String path,
			Operation<InputStream> original) {
		InputStream fromPack = TexturePacks.open(path);
		return fromPack != null ? fromPack : original.call(owner, path);
	}
}
