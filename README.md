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
format, and leaves the terrain a seed produces exactly as it is. Anything it
changes in a world is expressed purely with means vanilla already has.

There is one narrow exception, and it is deliberate: a dungeon chest can hold a
sponge in place of one of the items it rolled. The same dungeon, in the same
place, with the same number of stacks in the same chest — and not one number is
drawn from the generator to decide it, so every ore vein, tree and spring in
that chunk still lies where the seed put it.

It is also not a bugfix pack. Old-version quirks are part of the appeal and are
left alone unless they are genuinely in the way.

## Features

Every feature can be switched on and off individually in `config/feinschliff.txt`,
and every one of them is **on out of the box** — installing the mod is meant to be
enough. Set a key to `false` to get the vanilla behaviour back for that one feature.

Several of them have further keys of their own in that file: flying and camera
speed, how a boat handles, how fast a pig is and how often it stops listening,
which lines the debug overlay draws, whether the window fills the screen from
the start. `general.debugLogging` is the one setting that is off by
default — it adds diagnostic output to the log and belongs in a bug report
rather than in everyday play.

| ID | Feature | Category | Default | Config key |
|----|---------|----------|---------|------------|
| B1 | **Borderless fullscreen.** Puts `F11` on a window that fills the screen at the desktop resolution with nothing drawn around it, one press each way. The fullscreen this version ships with changes the monitor's display mode and then only tells the game about the new size when a menu happens to be open. | Window | on | `window.borderless` |
| B2 | **Vertical sync.** Waits for the monitor before showing a frame, instead of rendering hundreds nobody sees. A driver forcing vsync on or off still overrides it. | Window | on | `window.vsync` |
| B3 | **Clean exit.** Ends the process when you close the window, instead of leaving it on screen for half a minute. Nothing is saved on the way out — leave through *Save and Quit* as you would without the mod. | Window | on | `window.exitOnClose` |
| B4 | **Quit Game button.** Puts one on the title screen beside *Options...*, both of them half as wide. This version has no way out of the game on it, and a window filling the screen shows no close button to reach for. | Window | on | `window.quitButton` |
| F6 | **Pigs go where you look.** Sit on a saddled pig, hold wheat, and it walks wherever you are looking, a shade faster than you walk and without a movement key held down. Every half minute or so it grunts and spends a few seconds doing as it pleases. Right-click to feed it a piece of wheat: it hops, and then runs at sprinting speed for a quarter of a minute. A saddled pig also gives the saddle back when it dies. | Pigs | on | `pig.steer` |
| F3 | **No eating at full health.** Keeps food in your hand instead of using it up for nothing. This version has no hunger bar, so a meal at full health heals nothing. | Gameplay | on | `gameplay.noEatingAtFullHealth` |
| F5 | **Boat handling.** Takes the glide out of a boat. It gets up to speed in under a second and a half instead of eleven, comes to a stop in one instead of five, turns where you are already looking rather than where you were looking five seconds ago, and goes a little faster while it is at it. It also stops pulling your view around. How a boat breaks and what it leaves behind are untouched. | Boats | on | `boat.handling` |
| F2 | **Every block gets its tool.** Twenty-one blocks in this version belong to no tool at all: bricks, obsidian, redstone ore, furnaces, spawners, stone stairs, pressure plates, the iron door, buttons and rails are pickaxe work; stairs, crafting tables, doors, signs, fences, jukeboxes and ladders are an axe's; farmland a shovel's; and leaves and sponge the hoe's, which until now could not mine anything at all. Gold digs like gold rather than like wood, and a sword cuts wool, leaves and cactus. | Mining | on | `mining.toolAssignments` |
| G1 | **Sponges soak up water.** Put a sponge down and the water two blocks in every direction goes away; take it up again and the water comes back. It keeps drinking for as long as it lies there, so water pressing in from outside gets no further than the block beside it. The sponge is not used up — there is no wet sponge in this version — so a single one lasts for ever. | Sponges | on | `sponge.soaksUpWater` |
| G2 | **Sponges in dungeon chests.** About every second one holds a sponge. Without this there is no way to a sponge at all in this version — it is in no chest, on no mob and in no recipe. It takes the place of something the chest had already rolled, never a saddle, a golden apple or a record, and only in dungeons made from here on. | Sponges | on | `sponge.inDungeons` |
| C2 | **Free camera.** Sends the camera off on its own while your body stays where it is. Steered with your usual movement keys, jump and sneak, speed on the mouse wheel. Nothing you do with it touches the world — the game keeps drawing everything from where you actually are, so no chunk is ever loaded or generated for the camera. | Camera | on | `camera.freecam` |
| C1 | **Flight.** Takes off with `L` and flies with the usual movement keys, jump and sneak. You keep colliding with the world. Unlike the free camera this moves you, so it loads and generates terrain wherever you go — which is the point of it. | Movement | on | `movement.fly` |
| A1 | **Debug overlay.** Replaces the F3 screen with one that also shows position, chunk, facing, light level, world time, the block under the crosshair and the seed — colour-coded, on a translucent panel. | HUD | on | `hud.debugOverlay` |
| A5 | **Hide the hud.** `F1` clears the screen of everything drawn on top of the world — hotbar, crosshair, health, both debug screens and your own hand. The water and fire tints stay, so you can still tell that you are drowning. | HUD | on | `hud.hide` |
| D1 | **Shift-click.** Hold shift and click a stack to send it across instead of picking it up: between you and a chest, into a furnace as fuel or as something to smelt, out of a furnace again, onto your armour, and otherwise between your hotbar and the rest of your inventory. On a crafting result it makes as many as the grid and your free space allow. | Inventory | on | `inventory.shiftClick` |
| D2 | **Double-click fills a stack.** Click a stack twice in quick succession to pull everything of the same kind in the menu into your hand, up to a full stack. It takes the part-used stacks first, so what stays behind is whole ones. | Inventory | on | `inventory.doubleClick` |
| D3 | **Drag across slots.** Hold a stack, press and drag over several slots to lay it out over them: the left button shares it out evenly, the right button puts one in each. What does not divide stays in your hand. | Inventory | on | `inventory.drag` |
| D4 | **Sort a chest.** The middle mouse button over a chest tidies it: everything of one kind together, stacks filled up, empty slots at the end. Over the three rows above your hotbar it tidies those instead. Plain sorts by item id, with shift held by how much of each you have. | Inventory | on | `inventory.sort` |
| D5 | **Shift-drag.** Keep shift and the button held after a shift-click and draw across more slots to send each of them across as well — a whole row of your inventory into a chest, or five stacks of cobble back out, in one movement. | Inventory | on | `inventory.shiftDrag` |
| D6 | **Wheel moves single items.** Turn the wheel over a stack to move it one item at a time: down sends one across, up brings one back. Across means the same place a shift-click would send it. | Inventory | on | `inventory.scroll` |
| D7 | **Quick stack into a chest.** Control and the middle mouse button put away everything the chest already has some of. What it has never held stays with you, and so does your hotbar. | Inventory | on | `inventory.quickStack` |
| D8 | **Number keys.** Point at a stack in a menu and press 1 to 9 to put it on that place of your hotbar, trading places with whatever was there. It works on a crafting result too, but only onto a free place, and pointing at an empty slot fetches that place to you. | Inventory | on | `inventory.hotbarKeys` |
| E2 | **Pick block reaches further.** The middle mouse button already puts a block you are looking at into your hand when you have it on the hotbar. Now it fetches it from the rest of your inventory too, onto the nearest free place or in exchange for what you are holding. | Inventory | on | `inventory.pickBlock` |
| D9 | **Drop out of a menu.** Point at a stack in a menu and press the drop key to throw it out of there, without taking it into your hand first. With control held the whole stack goes. | Inventory | on | `inventory.dropFromMenu` |
| E3d | **Drop a whole stack.** Hold control while pressing the drop key to throw all of it instead of one. The key itself stays yours to bind in the game's own controls screen. | Inventory | on | `inventory.dropStack` |
| E3c | **Screenshots.** `F2` saves a picture to `screenshots/`, named after the moment it was taken. It holds exactly what is on the monitor — hud, debug screen and your own hand included — at the size of the window. | Screenshots | on | `screenshot.enabled` |

The free camera is on `hotkey.freecam`, `F6` by default, and it toggles: one
press sends it off, the next brings it back. While it is out you cannot mine,
build, attack, drop or change items, and it stops at the edge of the world the
game has drawn — there is nothing to see beyond it, since only the chunks
around your body are rendered. Leaving the world puts the camera away.

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

*Quit Game* ends the game the way closing the window does, and there is no
world open on the title screen for it to leave behind. Out of a world the way
out is still *Save and quit to title* first — that is what writes your world to
disk, and nothing else does.

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

A double click gathers from the whole menu, both sides of it at once: your
inventory, the chest and the crafting grid you are looking at. It never touches
a crafting result, because taking one of those spends the ingredients and a
gesture meant to tidy up should not craft. What the hand cannot hold stays
where it is.

Dragging keeps the press back until a second slot is touched, because until
then there is no telling a drag from a click. Letting go on the slot you
started on is therefore an ordinary click and behaves exactly as it would
without the mod — it is the game's own click, handed back to it. From the
second slot on the stack is really laid out as you go, and every further slot
takes it back and shares it out again, so what you see while dragging is the
outcome rather than a picture of it. Slots that cannot take what you are
holding are not part of the line, and what does not fit stays in your hand.

Shift-dragging puts nothing off, because a shift-click has already acted by the
time the pointer moves on: every slot the line reaches is simply another
shift-click, and each is visited once. The crafting result is left out of it,
so a line passing over it does not craft the grid empty on its way — clicking
it is how that is asked for.

Sorting leaves your hotbar alone, however you sort: it is arranged by hand, and
tidying it up every time would undo that. Armour, furnace and crafting grid are
left alone too, where the order means something. This version has nothing to
sort alphabetically by — an item here is a number and a count, with no name
anywhere in the game — so those two are what there is.

The wheel walks the same road as a shift-click, in both directions: down sends
one item where the whole stack would have gone, up takes one back from exactly
those slots. Turning it sixty-four times therefore ends where one shift-click
ends. One turn is one item however far the wheel is pushed, and part-used
stacks are emptied before whole ones are broken open. A crafting result is left
out — that is made rather than moved, and it comes out whole or not at all.

Quick stack is the one gesture you can press without looking, and the rule that
makes it so is that the chest decides: only what it has already started on goes
in. It uses the same route a shift-click uses, so part-used stacks in the chest
are filled up before an empty slot is taken, and a chest that runs out of room
simply keeps the rest with you.

A number key means one slot and no other, so it reaches the crafting grid as
well — pointing at a square is how a square gets picked. It trades places, and
an exchange only happens when both sides would hold what the other has. That is
what stops a diamond going into a crafting result or a furnace output: those
hand things out and take nothing back, so on them the key works onto a free
place and does nothing at all onto a taken one. Whole stacks only — half a
stack on a numbered key is nobody's idea of one.

Dropping a whole stack changes one number and nothing else: how many the drop
key takes out of your hand. It is the only safe place in that line to
intervene, because the throwing and the taking out are one statement — stop
the throw and the stack is out of your inventory and nowhere else.

In a menu the drop key throws whatever you are pointing at, and what your hand
happens to be holding stays in it — the same rule a shift-click follows. On a
crafting result one press is one craft, thrown whole, because the inventory
behind that slot hands over everything whatever amount is asked of it.

Pick block was always half there: the game searches your whole inventory for
the block you are looking at and then only acts if what it found happens to be
on the hotbar. The other half is a trade — the stack comes down, whatever was
in the way goes up — so it still creates nothing, which is the point of that
button.

A tool in this version is three lists and a number: thirteen blocks the pickaxe
knows about, four the axe, seven the shovel, none at all the hoe, and twice the
tier plus two on anything that is on the list. The lists were written before
most of the blocks that need them, which is why an iron pickaxe takes six times
longer on bricks than on the cobblestone they are made of.
`mining.toolAssignments` hands those twenty-one blocks to the tool every later
version gives them. Nothing ever gets slower for it: every answer is the larger
of the game's and the mod's, so a block the modern game left toolless keeps
whatever this version gave it.

The wooden pressure plate is the one place where that changes a drop rather
than a duration. Its constructor is handed no material and takes the default,
which is stone, so the game asks for a pickaxe before it will let go of one and
gives you nothing at all for a bare hand. It is made of planks, sounds like
planks, and now breaks like planks.

`mining.goldIsFast` is about the one tool that is upside down. A golden pickaxe
is the fastest in the game from Beta onwards and the slowest here, level with
wood, because the single number that sets a tool's speed also sets what it is
allowed to harvest — and gold has to sit at the bottom of it for the second
reason. Separating the two gives gold what it is for: quick, and quickly gone.
What it may harvest is untouched, so a golden pickaxe still brings up no iron.

`mining.swordCuts` is the one thing here that no version of the game does.
Shears arrive in Beta 1.7; until then a sword is a flat one and a half on every
block there is, which is the state of things before shears rather than a
mistake. This makes it a machete on the three things one would otherwise reach
for shears for — wool, leaves and cactus — at the rate shears would manage,
whatever the blade is made of, since shears have no material either.

A boat's whole character is one number: how much of its speed it keeps from one
tick to the next. Vanilla keeps 99% of it, and that single figure is why an
alpha boat needs eleven seconds to get going, five to become harmless again,
and half a lake to turn around in — acceleration, braking and turning are the
same number seen from three sides. `boat.drag` is that number, 0.92 here, and
it is the one to reach for if the boat feels wrong in either direction.

`boat.topSpeed` is in blocks per second, 9 against vanilla's 8. There is no
setting for the push, because there is nothing for one to decide: a boat
settles where its push and its drag cancel out, so the push follows from the
two numbers above and a boat therefore always reaches the speed it was
promised. Everything else about sailing is as it was — the boat goes where you
look, it still shatters on the first thing it touches at speed, and it still
leaves behind exactly what it always did.

`boat.freeView` is the other half, and it is a separate setting because it is a
separate annoyance: vanilla turns a boat towards whatever direction it happens
to be drifting in and then drags your view along after it, so keeping your eyes
on one spot while sailing past it is a fight. Off, the tug comes back.

A pig is not a vehicle, and the riding is built so that it never quite becomes
one. Wheat in your hand is the whole of the steering: the pig turns towards
where you are looking and walks, at `pig.speed`, which is 4.5 blocks a second
against the 4.3 you manage on foot and the 3.0 a pig manages on its own. Put
the wheat away and it goes back to wandering. Every `pig.mind.every` seconds of
being steered it grunts and takes `pig.mind.for` seconds for itself, and there
is nothing to be done about that but wait.

Feeding is the one thing that buys obedience. A right-click hands over one
piece of wheat, the pig hops with it and then runs at `pig.feed.speed` for
`pig.feed.seconds`, and for that time it has no opinions at all. Feeding again
mid-run adds to what is left rather than replacing it, up to the longest a
single piece could ever have lasted — a second piece is always worth something,
a sackful is not worth more than one. A click that opens a chest or a door is
that click and costs no wheat, and pointing at the pig itself is still how you
get off it.

Every stretch of time up there is rolled fresh within a third either way, so
the pig keeps no schedule. All of it is worked out while you sit on it and
written down nowhere: what a pig remembers between sessions is its saddle, and
that tag is vanilla's own.

A sponge in this version is a block whose one job was left half done. The
half that lets go is finished — take a sponge up and the game wakes the cube
around it so the water flows back — and the half that drinks is an empty pair
of braces inside three loops that already count to five. `sponge.soaksUpWater`
fills them in, at the radius of two those loops already name.
`sponge.soaksUpWater.keepsDry` is what makes one worth carrying: the sponge
drinks again every time a block beside it changes, so a hole in a lake stays a
hole. Off, it dries its cube once, the moment you place it, and the water finds
its way back.

The water goes without a word to its neighbours, and that is what keeps a sponge
quiet. Settled water has no tick of its own — the only thing that ever sets it
moving is being told that a block beside it changed — so announcing each removal
would wake the whole pool, have it press at the cube, drink it, and wake the pool
again. Taking the water out silently leaves everything outside the cube asleep.
The one thing it costs is that water which lived off a source the sponge drank,
a fall passing through the cube for instance, is not told either and hangs where
it is until something else disturbs it; breaking the sponge does that, and so
does breaking any block near it. Nothing else about the block changes — it is as
hard as it was, it drops itself, and it dams water like any other solid block.

`sponge.inDungeons` is the other half of the same idea, because a block that
drinks is worth nothing if there is no way to one — and there is none here.
A dungeon chest is the only generated chest this version has, and its eight
rolls are the whole of its loot system.

The sponge takes a place rather than adding one, and that is not a matter of
taste. The slot a rolled item goes into is drawn from the generator's own
random, and that draw only happens when the roll produced something, so turning
one of the empty rolls into a sponge would take one number more out of that
stream than the game did. Everything the chunk makes after the dungeon hangs off
that same stream: the other dungeon tries, clay, dirt, gravel, coal, iron, gold,
redstone, diamond, how many trees and of which kind, flowers, mushrooms, sugar
cane, cactus, and seventy water and lava springs. One extra draw moves all of
it. So the sponge draws nothing at all: it decides from a random of its own,
seeded from the world and the chest, and hands back a different stack for a slot
the game had already picked.

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
2. Drop `feinschliff-<version>+mca1.1.2_01.jar` into the instance's `mods`
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
