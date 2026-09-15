package io.github.julianbvw.feinschliff.mc.alpha;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.client.Minecraft;

import io.github.julianbvw.feinschliff.core.Feinschliff;
import io.github.julianbvw.feinschliff.core.config.KeyOption;
import io.github.julianbvw.feinschliff.core.input.Keys;
import io.github.julianbvw.feinschliff.core.platform.Game;
import io.github.julianbvw.feinschliff.core.window.VSync;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.MinecraftGameHost;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.Log4jLog;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.LwjglDisplay;
import io.github.julianbvw.feinschliff.mc.alpha.adapter.LwjglKeys;

/**
 * Alpha-era adapter: wires the platform pieces into the core.
 *
 * <p>Startup deliberately does not go through OSL's entrypoints module. That
 * module hooks {@code MinecraftApplet}, but an Ornithe PrismLauncher instance
 * starts the game without the applet wrapper, so the entrypoint would not
 * reliably fire. Hooking {@code Minecraft.init()} ourselves works in both
 * launch modes, costs no runtime dependency, and is the same place the
 * borderless-window feature will need later -- it runs before
 * {@code Display.create()}.
 */
public final class FeinschliffClient {

	private static boolean started;

	private FeinschliffClient() {
	}

	public static void bootstrap(Minecraft minecraft) {
		if (started) {
			return;
		}
		started = true;

		// Nothing here may keep the game from starting. A mod that fails to
		// initialise has to stay out of the way, not take Minecraft with it.
		try {
			KeyOption.setResolver(LwjglKeys::resolve);
			Keys.setKeyState(LwjglKeys::isDown);
			VSync.setVideo(LwjglDisplay::setVSync);
			Game.setHost(new MinecraftGameHost(minecraft));

			// The instance directory, i.e. where settings.txt belongs.
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
