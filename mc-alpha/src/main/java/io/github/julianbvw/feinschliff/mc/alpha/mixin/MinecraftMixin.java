package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.mc.alpha.FeinschliffClient;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	/**
	 * Startup. HEAD of init() is the earliest useful point: it runs before
	 * Display.setDisplayMode() and Display.create(), which is what the
	 * borderless-window feature will need, and before GameOptions is built.
	 */
	@Inject(method = "init", at = @At("HEAD"))
	private void feinschliff$bootstrap(CallbackInfo ci) {
		FeinschliffClient.bootstrap();
	}

	/**
	 * The client tick hook, 20 times a second. TAIL rather than HEAD so that
	 * anything reacting to a hotkey sees the state the vanilla tick produced.
	 */
	@Inject(method = "tick", at = @At("TAIL"))
	private void feinschliff$onTick(CallbackInfo ci) {
		Feinschliff.clientTick();
	}
}
