# Food and Recipes

Animaniax adds prime meats, breed cheeses, omelettes, truffles, milk bottles, honey, and farm recipes.

*Note on hunger values:* Every **2 food points equal 1 full hunger shank** on your hotbar. By default, Animaniax foods grant bonus status effects and can be eaten even when your hunger bar is full. Server owners can adjust food settings and nutrition values in [Configuration](configuration.md).

## Meats

Raw prime meats inflict **10 seconds of Nausea IV** if eaten raw. Cook them in a **Furnace** or **Smoker** first.

| Food item | Hunger restored | Saturation | Bonus effect when cooked |
| --- | --- | --- | --- |
| Raw Prime Meats / Horse / Frog Legs | `0.5 shanks` | `1.0` | Nausea IV (`10s`) when eaten raw |
| **Cooked Prime Beef** | `6 shanks` | `0.5` | Instant Health I |
| **Cooked Prime Steak** | `4 shanks` | `0.5` | Instant Health I |
| **Cooked Prime Pork** | `6 shanks` | `0.5` | Absorption I (`2.5 min`) |
| **Cooked Prime Bacon** | `4 shanks` | `0.5` | Absorption I (`1.5 min`) |
| **Cooked Prime Chicken** | `4 shanks` | `0.5` | Haste I (`2.5 min`) |
| **Cooked Prime Mutton** | `6 shanks` | `0.5` | Instant Health I |
| **Cooked Chevon** | `2.5 shanks` | `0.5` | Resistance I (`30s`) |
| **Cooked Prime Chevon** | `5 shanks` | `0.5` | Resistance II (`1 min`) |
| **Cooked Prime Rabbit** | `4 shanks` | `0.5` | Jump Boost IV (`30s`) |
| **Cooked Peacock** | `3 shanks` | `0.5` | Luck I (`30s`) |
| **Cooked Prime Peacock** | `4.5 shanks` | `0.5` | Luck II (`1 min`) |
| **Cooked Horse** | `6 shanks` | `0.5` | Strength I (`30s`) |
| **Cooked Frog Legs** | `3.5 shanks` | `0.5` | Jump Boost III (`1 min`) |

## Dairy and cheese

- **Bottle of Milk:** Restores `2 shanks` (`1.0` saturation), can always be drunk, clears all active status effects, and returns an empty Glass Bottle.
- **Placed Cheese Wheels (`4` bites each):** Place a Cheese Wheel on the ground and right-click to eat a slice (`1 shank`, `1.2` saturation):
  - **Friesian, Holstein, and Jersey Cheese:** Instant Health III
  - **Goat Cheese:** Resistance I (`1 min`)
  - **Sheep Cheese:** Instant Health I
- **Cheese Wedges:** Cut any Cheese Wheel with a Carving Knife to get `4` wedges. Each wedge restores `1.5 shanks` (`0.9` saturation):
  - **Friesian, Holstein, and Jersey Cheese Wedge:** Instant Health II
  - **Goat Cheese Wedge:** Resistance I (`1 min`)
  - **Sheep Cheese Wedge:** Instant Health I

## Omelettes, truffles, and honey

| Food item | Hunger restored | Saturation | Bonus effect | How to make |
| --- | --- | --- | --- | --- |
| **Plain Omelette** | `2.5 shanks` | `0.6` | None | Smelt any Egg (Chicken, Brown, or Peafowl) |
| **Cheese Omelette** | `2.5 shanks` | `0.7` | Instant Health II | Plain Omelette + any Cheese Wedge |
| **Bacon Omelette** | `2.5 shanks` | `0.7` | Strength I (`30s`) | Plain Omelette + Cooked Prime Bacon |
| **Truffle Omelette** | `2.5 shanks` | `0.8` | Regeneration II (`30s`) | Plain Omelette + Truffle |
| **Ultimate Omelette** | `2.5 shanks` | `0.9` | Regeneration II (`30s`), Strength I (`30s`), Resistance II (`30s`) | Plain Omelette + Cooked Prime Bacon + Truffle + Cheese Wedge |
| **Truffle** | `1 shank` | `0.7` | None | Dug up by hungry leashed pigs in Forests |
| **Truffle Soup** | `5 shanks` | `0.6` | Regeneration II (`1 min`) | `2 Truffles + 1 Bowl` (does not stack) |
| **Chocolate Truffle** | `3 shanks` | `0.7` | Speed IV (`1 min`) | `1 Truffle + 1 Cocoa Beans + 1 Sugar` |
| **Honey Bottle** | `5 shanks` | `1.5` | Regeneration II (`5s`) (does not cure Poison) | `1 Honey Bucket + 1 Glass Bottle`, or collect from a Bee Hive |

## Kitchen and farm crafting

### Carving Knife recipes (shapeless)

The Carving Knife stays in the crafting grid and loses `1` durability per craft:

- **Any Cheese Wheel + Carving Knife** -> `4` matching Cheese Wedges
- **Raw Prime Beef + Carving Knife** -> `4` Raw Prime Steak
- **Raw Prime Pork + Carving Knife** -> `4` Raw Prime Bacon
- **Wheat + Carving Knife** -> `1` Straw

### Dairy, slop, honey, and pet food

- **Slop Bucket:** Any `2` pig foods (Carrot, Potato, Poisonous Potato, Beetroot, or Bread) + any `1` Milk Bucket (shapeless; consumes the bucket).
- **Vanilla Milk Bucket:** Place any Animaniax breed Milk Bucket alone in the crafting grid to convert it into a vanilla Milk Bucket.
- **4 Bottles of Milk:** Any `1` Milk Bucket + `4` Glass Bottles (returns an empty Bucket).
- **8 Sugar:** `1` Honey Bottle (returns an empty Glass Bottle).
- **3 Hamster Food:** Any `3` crop seeds (Wheat, Melon, Pumpkin, or Beetroot Seeds).

### Breed wool crafting

- `1` Breed Wool -> `1` matching vanilla Wool (Brown, Black, or White)
- `2` Breed Wool -> `3` matching vanilla Carpets
- `6` Breed Wool + `1` Stick -> `1` matching vanilla Banner
- `3` Breed or vanilla Wool + `3` Wooden Planks -> `1` White Bed

### Extra vanilla recipes

- **Lead:** `2` Leather + `2` String (shapeless)
- **Name Tag:** `1` String + `1` Gold Nugget (shapeless)
- **Saddle:** `3` Leather across the top row, then `Iron Ingot`, `Wool`, `Iron Ingot` in the middle row
