package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.PrintStream;

import net.minecraft.block.WheatBlock;

import io.github.julianbvw.feinschliff.core.config.Settings;

@Mixin(WheatBlock.class)
public class WheatBlockMixin {

	/**
	 * A line of someone's debugging left behind in the crop: every time wheat
	 * is asked what it drops it writes its growth stage to standard out, which
	 * is a line in the log for every stalk harvested, trampled or washed away.
	 *
	 * <p>Only the printing is skipped. The argument is a string being joined
	 * and has nothing to do but be printed, so nothing else goes missing with
	 * it, and the drop underneath is untouched.
	 */
	@WrapWithCondition(
		method = "getDropItem",
		at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
	private boolean feinschliff$letTheWheatBeQuiet(PrintStream out, String line) {
		return !Settings.GENERAL_QUIET_WHEAT.on();
	}
}
