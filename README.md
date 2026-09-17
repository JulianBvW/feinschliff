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
| B1 | **Borderless fullscreen.** Puts `F11` on a window that fills the screen at the desktop resolution with nothing drawn around it, one press each way. The fullscreen this version ships with changes the monitor's display mode and then only tells the game about the new size when a menu happens to be open. | Window | on | `window.borderless` |
| B2 | **Vertical sync.** Waits for the monitor before showing a frame, instead of rendering hundreds nobody sees. A driver forcing vsync on or off still overrides it. | Window | on | `window.vsync` |
| B3 | **Clean exit.** Ends the process when you close the window, instead of leaving it on screen for half a minute. Nothing is saved on the way out — leave through *Save and Quit* as you would without the mod. | Window | on | `window.exitOnClose` |
| F3 | **No eating at full health.** Keeps food in your hand instead of using it up for nothing. This version has no hunger bar, so a meal at full health heals nothing. | Gameplay | on | `gameplay.noEatingAtFullHealth` |
| C2 | **Free camera.** Sends the camera off on its own while your body stays where it is. Steered with your usual movement keys, jump and sneak, speed on the mouse wheel. Nothing you do with it touches the world — the game keeps drawing everything from where you actually are, so no chunk is ever loaded or generated for the camera. | Camera | on | `camera.freecam` |
| C1 | **Flight.** Takes off with `L` and flies with the usual movement keys, jump and sneak. You keep colliding with the world. Unlike the free camera this moves you, so it loads and generates terrain wherever you go — which is the point of it. | Movement | on | `movement.fly` |
| A1 | **Debug overlay.** Replaces the F3 screen with one that also shows position, chunk, facing, light level, world time, the block under the crosshair and the seed — colour-coded, on a translucent panel. | HUD | on | `hud.debugOverlay` |
| A5 | **Hide the hud.** `F1` clears the screen of everything drawn on top of the world — hotbar, crosshair, health, both debug screens and your own hand. The water and fire tints stay, so you can still tell that you are drowning. | HUD | on | `hud.hide` |
| D1 | **Shift-click.** Hold shift and click a stack to send it across instead of picking it up: between you and a chest, into a furnace as fuel or as something to smelt, out of a furnace again, onto your armour, and otherwise between your hotbar and the rest of your inventory. On a crafting result it makes as many as the grid and your free space allow. | Inventory | on | `inventory.shiftClick` |
| E3c | **Screenshots.** `F2` saves a picture to `screenshots/`, named after the moment it was taken. It holds exactly what is on the monitor — hud, debug screen and your own hand included — at the size of the window. | Screenshots | on | `screenshot.enabled` |

The free camera is on `hotkey.freecam`, `F6` by default, and it toggles the same
way. While it is out you cannot mine, build, attack, drop or change items, and
it stops at the edge of the world the game has drawn — there is nothing to see
beyond it, since only the chunks around your body are rendered. Leaving the
world puts the camera away.

Flying is on `hotkey.fly`, `L` by default. Letting go in mid-air means falling,
with everything that comes with it, and flying is no kind of shield: lava still
burns and deep water still drowns.

The hud key is `hotkey.hideHud`, `F1` by default, and it toggles. Any open
menu — the pause screen, your inventory, a chest — brings the hud back for as
long as it is open, so nothing is ever invisible while you are trying to use
it. The screen also comes back on its own when the game is restarted: the
setting is never written to the config.

The screenshot key is `hotkey.screenshot`, `F2` by default. The game pauses
for a moment while the file is written, and a line above the hotbar says where
it went; the log says the same. Two pictures taken in the same second get a
`_1`, `_2` suffix, so nothing is ever overwritten.

Borderless fullscreen sits on `F11`, the key the game already uses for
fullscreen, so there is no hotkey of its own. Set `window.borderless.onStart` to
fill the screen from the moment the game starts. Switching `window.borderless`
off hands `F11` back to the game's own fullscreen, which in this version leaves
the picture in a corner at the old size unless a menu is open when you press it.

Shift-click works in all four menus — your inventory, a chest, a furnace and a
crafting table — because in this version they are all the same screen
underneath. Where a stack goes depends on where it came from, and it only ever
goes one way: out of your inventory it goes to whatever the menu brought along,
and out of that it comes back to you, hotbar first. It fills part-used stacks
before empty slots, never puts anything into a furnace's output, and leaves a
stack where it is when there is no room rather than shuffling it somewhere
else. Whatever you happen to be holding stays in your hand throughout. Crafting
stops the moment the next result would not fit whole, so nothing is ever made
that cannot be put down.

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
