# Feinschliff

A quality-of-life mod for **Minecraft Alpha v1.1.2_01**, built on
[OrnitheMC](https://ornithemc.net/) (Fabric Loader + Mixin for legacy versions).

Feinschliff adds HUD information, window and performance options, camera helpers
and inventory comfort — the things that make an old version pleasant to play
without making it a different game.

## What it will never do

This is the core promise of the project, and every feature is checked against it:

> A world played with Feinschliff must work completely and without errors in
> unmodified Alpha v1.1.2_01 after the mod is removed.

Concretely, Feinschliff adds **no** blocks, items, entities, recipes, biomes or
dimensions, writes **no** new NBT tags, changes **nothing** about the save
format, and never touches world generation. Anything it changes in a world is
expressed purely with means vanilla already has.

It is also not a bugfix pack. Old-version quirks are part of the appeal and are
left alone unless they are genuinely in the way.

## Features

Every feature can be switched on and off individually in `config/feinschliff.txt`,
and every one of them is **on out of the box** — installing the mod is meant to be
enough. Set a key to `false` to get the vanilla behaviour back for that one feature.

| ID | Feature | Category | Default | Config key |
|----|---------|----------|---------|------------|
| B2 | **Vertical sync.** Waits for the monitor before showing a frame, instead of rendering hundreds nobody sees. A driver forcing vsync on or off still overrides it. | Window | on | `window.vsync` |
| B3 | **Clean exit.** Ends the process when you close the window, instead of leaving it on screen for half a minute. Nothing is saved on the way out — leave through *Save and Quit* as you would without the mod. | Window | on | `window.exitOnClose` |
| F3 | **No eating at full health.** Keeps food in your hand instead of using it up for nothing. This version has no hunger bar, so a meal at full health heals nothing. | Gameplay | on | `gameplay.noEatingAtFullHealth` |
| C2 | **Free camera.** Sends the camera off on its own while your body stays where it is. Steered with your usual movement keys, jump and sneak, speed on the mouse wheel. Nothing you do with it touches the world — the game keeps drawing everything from where you actually are, so no chunk is ever loaded or generated for the camera. | Camera | on | `camera.freecam` |
| A1 | **Debug overlay.** Replaces the F3 screen with one that also shows position, chunk, facing, light level, world time, the block under the crosshair and the seed — colour-coded, on a translucent panel. | HUD | on | `hud.debugOverlay` |

The free camera is on `hotkey.freecam`, `F6` by default, and it toggles the same
way. While it is out you cannot mine, build, attack, drop or change items, and
it stops at the edge of the world the game has drawn — there is nothing to see
beyond it, since only the chunks around your body are rendered. Leaving the
world puts the camera away.

The debug key is `hotkey.debugOverlay`, `F3` by default, and it toggles:
press once to show the overlay, press again to hide it. Switching
`hud.debugOverlay` off restores the vanilla F3 screen unchanged.

## Requirements

- Minecraft **Alpha v1.1.2_01**
- An **Ornithe** instance (Fabric Loader)
- Java 8 or newer

## Installation

1. Create an Ornithe instance for `a1.1.2_01` with the
   [Ornithe installer](https://ornithemc.net/download), which can generate a
   ready-made PrismLauncher/MultiMC instance.
2. Drop `feinschliff-<version>+mc<a1.1.2_01>.jar` into the instance's `mods`
   folder.
3. Start the game once. Feinschliff writes a commented
   `config/feinschliff.txt` into the instance. Edit it, then press the reload
   hotkey (`F10` by default) or restart.

The config lives in the instance, never inside a world folder, so removing the
mod leaves your worlds untouched. Existing files are never overwritten — delete
the file to get a fresh one with all defaults and comments back.

## Building

```bash
./gradlew build        # -> mc-alpha/build/libs/
```

The build needs a **JDK**, not just a JRE. If Gradle reports
`does not provide the required capabilities: [JAVA_COMPILER]`, point `JAVA_HOME`
at a real JDK first.

To copy the result straight into a launcher instance, add the target to your
personal `~/.gradle/gradle.properties` (not to the repository):

```properties
prism_instance_dir = /path/to/PrismLauncher/instances/<name>/minecraft
```

```bash
./gradlew deploy       # build + copy into that instance
./gradlew runClient    # development launch, uses ./run as the game directory
./gradlew genSources   # decompile named Minecraft sources
./gradlew vscode       # generate a VS Code launch configuration
```

## Project layout

| Module | Contents |
|---|---|
| `core` | Feature logic, config system, data tables. Has **no** Minecraft on its classpath, by design. |
| `mc-alpha` | The alpha-era adapter: mixins and the thin glue that binds `core` to the game. |

Keeping the game out of `core` is what makes supporting more than one Minecraft
version affordable: a new feature is written once in `core`, and each supported
version only needs its (usually tiny) injection points.

## Credits

- [OrnitheMC](https://ornithemc.net/) for the mappings, toolchain and for making
  legacy modding possible at all
- [FabricMC](https://fabricmc.net/) for the loader, Loom and Mixin tooling

## License

MIT — see [LICENSE](LICENSE).
