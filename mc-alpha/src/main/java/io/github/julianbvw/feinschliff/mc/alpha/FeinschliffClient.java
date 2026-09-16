package io.github.julianbvw.feinschliff.mc.alpha;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.client.Minecraft;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.KeyOption;
import io.github.julianbvw.feinschliff.core.input.Keys;
import io.github.julianbvw.feinschliff.core.platform.Game;
import io.github.julianbvw.feinschliff.core.platform.WindowMode;
import io.github.julianbvw.feinschliff.core.window.Borderless;
import io.github.julianbvw.feinschliff.core.window.VSync;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.MinecraftGameHost;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.Log4jLog;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.LwjglDisplay;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.LwjglKeys;
import io.github.julianbvw.feinschliff.mc.alpha.window.BorderlessWindow;

/**
 * Alpha-era adapter: wires the platform pieces into the core.
 *
 * <p>Startup deliberately does not go through OSL's entrypoints module. It
 * would fire -- the loader starts this version through its own applet wrapper,
 * so the {@code MinecraftApplet} that module hooks really is there -- but it
 * drags in osl-core, which then has to be bundled or installed alongside.
 * Hooking {@code Minecraft.init()} ourselves costs no runtime dependency at
 * all.
 */
public final class FeinschliffClient {

	private static boolean started;
	private static Minecraft minecraft;

	private FeinschliffClient() {
	}

	/** The running game, for the one place that has to recognise the player. */
	public static Minecraft minecraft() {
		return minecraft;
	}

	public static void bootstrap(Minecraft minecraft) {
		if (started) {
			return;
		}
		started = true;
		FeinschliffClient.minecraft = minecraft;

		// Nothing here may keep the game from starting. A mod that fails to
		// initialise has to stay out of the way, not take Minecraft with it.
		try {
			KeyOption.setResolver(LwjglKeys::resolve);
			Keys.setKeyState(LwjglKeys::isDown);
			VSync.setVideo(LwjglDisplay::setVSync);
			Borderless.setWindow(new WindowMode() {

				@Override
				public void setBorderless(boolean borderless) {
					BorderlessWindow.set(minecraft, borderless);
				}

				@Override
				public void takePointer() {
					BorderlessWindow.takePointer(minecraft);
				}
			});
			Game.setHost(new MinecraftGameHost(minecraft));

			// The instance directory, i.e. where feinschliff.txt belongs.
			// Minecraft.getWorkingDirectory() is not usable here: it is only
			// filled in further down init(), after Display.create().
			Path gameDir = Paths.get(System.getProperty("user.dir", "."));

			Feinschliff.init(gameDir, new Log4jLog());
		} catch (Throwable t) {
			new Log4jLog().error("failed to start up; the mod stays inactive"
				+ " for this session", t);
		}
	}
}
