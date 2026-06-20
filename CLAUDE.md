# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

This is a plain IntelliJ IDEA project (no Maven/Gradle). Compile and run from the IDE, or manually:

```bash
# Compile (from project root)
javac -d out/production/GoblinHunters -sourcepath src $(find src -name "*.java")

# Run
java -cp out/production/GoblinHunters goblinhunters.controller.Main
```

Entry point: `goblinhunters.controller.Main`. No test suite exists.

## Architecture Overview

Standard MVC with strict layer separation enforced through interfaces.

### Packages
- `goblinhunters.model` — game state and logic
- `goblinhunters.controller` — game loop and input routing
- `goblinhunters.view` — Swing rendering
- `goblinhunters.utils` — shared constants and enums

### Model
`Model` is a singleton facade implementing `IModel`. It never exposes internals directly; all cross-layer access goes through `IModel`. Internal work is delegated to package-private managers:

| Manager | Responsibility |
|---|---|
| `MapManager` | procedural map generation, tile state, boss cracks |
| `CollisionManager` | walkability, hitbox overlap, bomb blocking |
| `SpawnManager` | initial spawn, portal spawn, boss portal |
| `ScoreManager` | score accumulation, enemy death rewards |
| `LevelManager` | zone progression, portal/gate lifecycle, transitions |

Managers receive `Model` as a constructor arg but operate exclusively on data passed as parameters — they hold no state that references the singleton, which keeps them testable.

**Critical constraint**: `IModel` is the public contract between Model and Controller. Do not add, remove, or rename methods in `IModel` without updating `ControllerForView` (which mirrors every method as a delegate).

All live game object lists (`activeBombs`, `projectiles`, `activeFire`, `enemies`, etc.) use `CopyOnWriteArrayList` so the EDT can read a snapshot during `paintComponent` without racing against the game thread.

### Game Loop
`ControllerForModel` runs a fixed-timestep loop at 60 FPS on a dedicated `Thread`. Each tick:
1. Calls `Model.updateGameLogic()` if `PLAYING` and not paused.
2. Checks `isGameOverPending()` / `isLevelCompletedFlag()` to trigger state transitions.
3. Calls `ControllerForView.requestRepaint()` to schedule a Swing repaint.

Level transitions are animated: the controller counts down `transitionTimer` ticks; at the midpoint (screen fully black) it calls `Model.prepareNextLevel()`.

### Controller
Two singletons with distinct roles:
- `ControllerForModel` (implements `IControllerForModel`): owns the game thread, game state enum (`MENU`/`PLAYING`/`GAME_OVER`), and pause flag.
- `ControllerForView` (implements `IControllerForView`): thin delegation layer — almost every method is a one-liner forwarding to `Model.getInstance()` or `ControllerForModel.getInstance()`. Key exception: `isStaffUsable()` contains view-specific business logic (zone 2 + preparation phase only).

### View
`ConcreteDrawer` is the single rendering orchestrator. It delegates to standalone singleton drawers:
- `EnemyDrawer`, `PlayerDrawer`, `HudDrawer` — extracted from ConcreteDrawer, same package
- `MenuDrawer`, `GameOverDrawer`, `PauseMenuDrawer` — overlay screens

**Y-sorting**: all renderable entities are gathered into `List<DrawableEntity>` (a package-private record of `int y` + `Runnable drawAction`), sorted by foot Y coordinate, then drawn in order. This produces correct depth layering without a Z-buffer.

`SpriteManager` is the global sprite cache. Sprite keys follow the pattern `PREFIX_STATE_DIRECTION` (e.g. `"BOSS_RUN_DOWN"`, `"COMMON_DYING"`). DYING animations omit the direction suffix. Grayscale variants for HUD icons are pre-built at startup via `buildGrayscale()`.

`GamePanel` owns all Swing `InputMap`/`ActionMap` bindings. Key rebinding at runtime is handled by passing a `BiConsumer<Integer, String>` callback to the controller, which stores it and calls it from `PauseController` — so the pause system can update bindings without depending on `GamePanel`.

## Game Zones and Flow

| Zone | Theme | Notes |
|---|---|---|
| 0 | VILLAGE | Classic Bomberman-style |
| 1 | FOREST | Bush break animation instead of crate break |
| 2 | CAVE | Preparation phase → global explosion spawns boss |

Zone 2 flow: `isPreparationPhase()` is true until `tickBossPreparation()` returns true, at which point `triggerGlobalExplosion()` destroys all crates and spawns `BossGoblin`. The boss fight runs until all enemies (boss + portal goblins) are dead, then the exit gate activates.

## Key Config Locations

- `LogicConfig` / `ViewConfig` — all game constants (grid size, speeds, frame counts, HUD positions). `Config` is a façade that re-exports both.
- `Config.GRID_WIDTH` / `GRID_HEIGHT` = 12 × 11 logical tiles.
- `Config.FPS` = 60; `ENTITY_LOGICAL_SPEED` drives all movement calculations.
