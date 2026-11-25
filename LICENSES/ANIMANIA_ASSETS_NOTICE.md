# Third-Party Placeholder Assets Notice (GNU LGPL-3.0)

This repository and its built artifacts contain placeholder textures, models, and sounds from the original Animania mod, used strictly as temporary placeholders alongside the Responsive Source License (RSL) v1.0 codebase.

## License Separation and Exact Scope

### 1. Covered by GNU LGPL-3.0 (Placeholder Assets, Models, and Sounds Only)

The **GNU Lesser General Public License v3.0** (`LICENSES/LGPL-3.0.txt`, incorporating `LICENSES/GPL-3.0.txt`) applies **only** to the placeholder asset, model, and sound files listed below under `src/main/resources/assets/animaniax/` (packaged under `assets/animaniax/` in built JARs):

* `textures/**`: Original Animania texture `.png` and `.png.mcmeta` files.
* `models/block/**`: Placeholder block model `.json` files adapted from original Animania block models for Minecraft 1.21.1 paths.
* `models/item/**`: Placeholder item model `.json` files adapted from original Animania item models for Minecraft 1.21.1 paths.
* `models/placeholder/craftstudio/*.csjsmodel`: Original Animania CraftStudio model files.
* `models/placeholder/entity/*.json`: JSON geometry and pose data extracted from original Animania Java entity model and pose definitions.
* `models/placeholder/blockentity/*.json`: JSON geometry data extracted from original Animania Java block entity model definitions.
* `sounds/**`: Original Animania non-Easter-egg animal and vehicle `.ogg` sound files.
* `sounds.json`: Sound event definitions referencing those `.ogg` files, adapted for `animaniax` and Minecraft 1.21.1.

### 2. Covered by Responsive Source License (RSL) v1.0 (Everything Else)

Everything else in this repository and its built artifacts remains licensed exclusively under **Responsive Source License (RSL) v1.0** (`LICENSE.md`), including without limitation:

* All Java source code and compiled class files under `src/main/java/**` (including custom model loaders, renderers, animations, sound registrations, mixins, entities, block entities, items, fluids, mechanics, and loader entrypoints).
* All datapack files under `src/main/resources/data/**` (recipes, loot tables, tags, worldgen, and biome modifiers).
* Language files (`assets/animaniax/lang/**`), blockstate definitions (`assets/animaniax/blockstates/**`), resource metadata, mod manifests (`fabric.mod.json`, `META-INF/neoforge.mods.toml`, `animaniax.mixins.json`), Gradle build scripts, and project documentation.

Nothing in this notice grants permission to redistribute, mirror, or relicense the RSL-licensed code, data, or documentation under LGPL-3.0 or any license other than RSL v1.0.

## Upstream Attribution

* **Source Project:** Animania (`https://github.com/capnkirok/animaniamod`)
* **Branch and Commit:** `1.12` (`32ae2b4c56cb84284e865dae0d3b78770992ba1d`)
* **Upstream Authors and Credits:** RazzleberryFox, Purplicious_Cow, cy4n, Tschipp, ZeAmateis, Timmypote, Raptorfarian, vroulas, and TheJurassicAlien (per upstream `mcmod.info`)
* **Upstream License:** GNU Lesser General Public License v3.0 (`LGPL-3.0`)

## Notice of Rights Under LGPL-3.0 Section 4

The LGPL-3.0 placeholder textures, models, and sounds are kept cleanly separated as data and asset files under `assets/animaniax/` rather than embedded as hardcoded Java geometry or audio data. Users and modifiers can inspect, extract, modify, replace, or override those LGPL-3.0 placeholder textures, models, and sounds directly via Minecraft resource packs or asset replacement under the terms of LGPL-3.0, independently of the RSL-licensed code that loads, plays, and renders them.

Complete license texts are included in `LICENSES/LGPL-3.0.txt` and `LICENSES/GPL-3.0.txt`.
