# Housing and Production

This page covers every block and workstation you need to feed, house, and collect products from your animals.

## Animal Trough

An Animal Trough needs 2 open blocks side by side when placed.

**Crafting (Shaped):**

```text
[Planks] [Empty]      [Planks]
[Planks] [Iron Ingot] [Planks]
[Stick]  [Empty]      [Stick]
```

- **Capacity:** Holds up to 3 stacks/items of feed, 1 bucket of water (10 drinks for large livestock, or 20 drinks for poultry and small pets), or 1 bucket of slop at a time.
- **How to use:** Right-click to add animal feed, a Water Bucket, a compatible fluid container, or a Slop Bucket. Right-click with an empty hand to take feed back out. Supports Hoppers when automation is enabled in [Configuration](configuration.md).

## Chicken Nest

**Crafting (Shapeless):** `1 Leaves + 1 Wool + 1 Stick`

- **Capacity:** Holds up to 3 eggs of the same chicken breed or peafowl morph.
- **How to use:**
  - Hens and peahens lay eggs inside automatically about every 1.5 minutes.
  - Right-click with an empty hand (without sneaking) to pick up 1 egg.
  - Eggs hatch on random ticks if an **adult, unsterilized male** of the same bird type is within 3 blocks and the area is not overcrowded.

## Cheese Mold

**Crafting (Shaped):**

```text
[Empty]  [Empty]      [Empty]
[Planks] [Iron Ingot] [Planks]
[Empty]  [Planks]     [Empty]
```

- **Making Cheese:** Right-click an empty Cheese Mold with a **Friesian, Holstein, Jersey, Goat, or Sheep Milk Bucket** (or compatible milk container). Wait 1 full Minecraft day (**20 minutes**) for it to mature, then right-click with an empty hand to collect the finished Cheese Wheel.
- **Making Salt:** Right-click an empty Cheese Mold with a **Water Bucket** (unless salt creation is turned off in [Configuration](configuration.md)). After 1 Minecraft day, collect **16 Salt** (adjustable in config).
- **Checking progress:** Sneak and right-click with an empty hand to see the curing percentage (`0%` to `100%`).

## Bee Hives

- **Wild Bee Hive:** Spawns naturally attached to tree logs in Forests, Jungles, Taigas, Swamps, and Plains. Produces honey slowly over time in valid hive biomes, and can sting nearby players for 1.25 hearts of damage.
- **Player Bee Hive:** Safer and faster, and never stings players.

**Crafting (Shaped):**

```text
[Planks] [Planks]        [Planks]
[Planks] [Wild Bee Hive] [Planks]
[Planks] [Planks]        [Planks]
```

- **Capacity and harvesting:** Both hives hold up to **5 buckets** of honey (produced while inside valid hive biomes).
  - Right-click with a **Glass Bottle** when at least 1 bucket (`1,000` mB) is stored to collect a **Honey Bottle**.
  - Right-click with an empty **Bucket** to collect a **Honey Bucket** (or use another compatible fluid container).
  - Sneak and right-click with an empty hand to check how much honey is stored.

## Salt Lick

**Crafting (Shapeless):** `8 Salt + 1 Water Bucket`

- **Uses:** Has `200` uses by default (keeps remaining durability if broken and picked up).
- **How it works:** Injured farm livestock within 16 blocks visit the Salt Lick to heal **1 heart** per use.

## Straw, Mud, and Seed Piles

- **Straw:** Sleep block for livestock, dogs, hamsters, ferrets, and hedgehogs.
  - **Craft (Shapeless):** `1 Wheat` -> `1 Straw` (or `1 Wheat + 1 Carving Knife` -> `1 Straw`, keeping the knife).
- **Mud:** Slows walking mobs and lets pigs stay Content for breeding.
  - **Craft (Shapeless):** `1 Water Bucket + 2 Dirt` -> `4 Mud`.
- **Seed Piles:** Right-click a solid non-farmland block with **Wheat, Pumpkin, Melon, or Beetroot Seeds** to place a Seed Pile on the ground for chickens and peafowl (Dispensers can also place piles).

## Pet Bowl

**Crafting (Shaped):**

```text
[Empty]       [Empty]      [Empty]
[Iron Ingot]  [Empty]      [Iron Ingot]
[Iron Nugget] [Iron Ingot] [Iron Nugget]
```

- **Capacity:** Holds up to 3 pet foods (Raw Fish, Raw Beef, or Hamster Food) or 1 bucket of water. Dropped pet foods also land directly inside the bowl.

## Hamster Wheel

**Crafting (Shaped):**

```text
[Empty]      [Iron Ingot] [Empty]
[Iron Ingot] [Empty]      [Iron Ingot]
[Stone Slab] [Iron Ingot] [Stone Slab]
```

- **How to run it:**
  1. Right-click the wheel with **Hamster Food** to load up to 16 pieces.
  2. Insert a **fed, tamed hamster** (not in a ball) either from a Carried Hamster item or from your shoulder (feed it so it has stored food first).
  3. While spinning, the wheel generates **20 RF per tick** (up to `200,000` RF internal buffer) and sends energy to adjacent tech mod cables or machines.
  4. Every ~1.5 minutes (`2,000` ticks), it uses 1 Hamster Food. When food runs out, the wheel releases the hamster safely nearby (or drops a Carried Hamster item if blocked).
  5. Right-click with an empty hand to take your hamster out early.

## Pet Furniture

| Item | Crafting (Shaped) | Notes |
| --- | --- | --- |
| **Cat Bed (variant 1)** | 5 Wool in a U-shape (bottom 3 + 2 sides) | Sleep bed for cats; job site for Pet Seller villagers |
| **Cat Bed (variant 2)** | 8 Wool in a ring (empty center) | Sleep bed for cats; job site for Pet Seller villagers |
| **Dog Pillow** | 6 Wool in bottom 2 rows | Sleep bed for dogs |
| **Cat Tower** | String, Wooden Planks, and Red Wool | Decorative cat furniture |
| **Dog House** | 1 White Terracotta (top center) + 5 Red Terracotta (arch) | Decorative dog furniture |
| **Litter Box** | 1 Sand (center) + 5 Iron Ingots (U-shape around bottom) | Decorative cat furniture |

*Redstone tip:* Animal Troughs, Chicken Nests, Pet Bowls, and Hamster Wheels all emit **Redstone Comparator** signals based on their contents or occupancy.
