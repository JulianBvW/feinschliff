package io.github.julianbvw.feinschliff.mc.alpha.mixin;

import java.util.Random;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.VeinFeature;

import io.github.julianbvw.feinschliff.mc.alpha.world.Veins;

/**
 * Gives a vein its full size wherever it lies.
 *
 * <p>The mistake is a cast, and a cast is not a call -- there is nothing in the
 * method for an injector to take hold of. So the vein is moved instead: it is
 * laid down where the cast rounds the way it was meant to, and the two calls
 * that touch the world carry the blocks back. Everything between them is the
 * game's own arithmetic, untouched.
 *
 * <p>It draws no number from the generator either. {@code place} takes exactly
 * the same values from the random in the same order whatever its coordinates
 * are, so the terrain, the caves, the trees, the springs and the dungeons of a
 * chunk are the ones the seed asked for. The only difference is which blocks of
 * stone turn out to be ore.
 *
 * <p>All seven things that generate as veins are covered, because they are one
 * class: dirt, gravel, coal, iron, gold, redstone and diamond.
 */
@Mixin(VeinFeature.class)
public class VeinFeatureMixin {

	@WrapMethod(method = "place")
	private boolean feinschliff$layTheVeinWhereItRounds(World world, Random random,
			int x, int y, int z, Operation<Boolean> original) {
		int outerX = Veins.shiftX();
		int outerZ = Veins.shiftZ();
		Veins.moveTo(x, z);
		try {
			return original.call(world, random, x + Veins.shiftX(), y, z + Veins.shiftZ());
		} finally {
			Veins.restore(outerX, outerZ);
		}
	}

	/**
	 * The one read in the method, and it sits behind the sphere test, so it only
	 * ever asks about blocks the vein really wants.
	 */
	@WrapOperation(method = "place", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;getBlock(III)I"))
	private int feinschliff$askWhereItReallyIs(World world, int x, int y, int z,
			Operation<Integer> original) {
		return original.call(world, x - Veins.shiftX(), y, z - Veins.shiftZ());
	}

	@WrapOperation(method = "place", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;setBlockQuietly(IIII)Z"))
	private boolean feinschliff$writeWhereItReallyBelongs(World world, int x, int y, int z, int block,
			Operation<Boolean> original) {
		return original.call(world, x - Veins.shiftX(), y, z - Veins.shiftZ(), block);
	}
}
