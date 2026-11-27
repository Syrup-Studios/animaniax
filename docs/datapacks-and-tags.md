# Datapacks and Tags

Animaniax uses standard Minecraft, common (`c:`), and mod (`animaniax:`) tags so modpacks and datapacks can adjust animal diets, grazing targets, spawns, and recipes.

## Animal food and recipe item tags

| Tag | Default entries | Purpose |
| --- | --- | --- |
| `animaniax:cow_foods` | `minecraft:wheat`, optional mod grains | Cow and mooshroom feed (`#minecraft:cow_food` references this) |
| `animaniax:goat_foods` | `minecraft:wheat`, `minecraft:string`, `minecraft:stick`, `minecraft:apple`, optional mod grains | Goat feed (`#minecraft:goat_food` references this) |
| `animaniax:sheep_foods` | `minecraft:wheat`, optional mod grains | Sheep feed (`#minecraft:sheep_food` references this) |
| `animaniax:pig_slop_foods` | `minecraft:carrot`, `minecraft:beetroot`, `minecraft:potato`, `minecraft:poisonous_potato`, `minecraft:bread` | Pig feed (`#minecraft:pig_food` references this) and Slop Bucket ingredients |
| `animaniax:horse_foods` | `minecraft:wheat`, `minecraft:apple`, `minecraft:carrot`, optional mod grains | Draft horse feed (`#minecraft:horse_food` references this) |
| `animaniax:rabbit_foods` | `minecraft:wheat`, `minecraft:carrot`, `minecraft:beetroot`, `minecraft:apple` | Rabbit feed (`#minecraft:rabbit_food` references this) |
| `animaniax:poultry_foods` | `#minecraft:chicken_food` | Chicken and peafowl trough feed |
| `animaniax:cat_foods` | `#c:foods/raw_fish` (optional), `cod`, `salmon`, `tropical_fish`, `pufferfish` | Cat feed and Pet Bowl items |
| `animaniax:dog_foods` | `#c:foods/raw_beef` (optional), `minecraft:beef` | Dog care/breeding feed and Pet Bowl items |
| `animaniax:additional_wolf_foods` | Animaniax meats, omelettes, cheese wedges, truffles, `milk_bottle` | Added to `#minecraft:wolf_food` (plus `raw_horse` and `cooked_horse`) for healing tamed dogs and wolves |
| `animaniax:ferret_foods` | `minecraft:mutton`, `#animaniax:small_pet_eggs`, `#animaniax:small_pet_meats` | Ferret feed |
| `animaniax:hedgehog_foods` | `#animaniax:small_pet_eggs`, `#animaniax:small_pet_meats`, `carrot`, `beetroot`, `apple` | Hedgehog feed |
| `animaniax:hamster_foods` | `animaniax:hamster_food`, `apple`, `wheat_seeds`, `melon_seeds`, `beetroot_seeds`, `pumpkin_seeds`, optional mod seeds | Hamster feed |
| `animaniax:hamster_seeds` | `wheat_seeds`, `melon_seeds`, `pumpkin_seeds`, `beetroot_seeds` | Ingredients for crafting `animaniax:hamster_food` |
| `animaniax:small_pet_eggs` | `minecraft:egg`, `animaniax:brown_egg`, `peacock_egg_blue`, `peacock_egg_white` | Shared egg tag for ferrets and hedgehogs |
| `animaniax:small_pet_meats` | `minecraft:rabbit`, `minecraft:chicken`, `raw_prime_mutton`, `raw_prime_rabbit`, `raw_prime_chicken` | Shared meat tag for ferrets and hedgehogs |
| `animaniax:flower_breeding_foods` | `dandelion`, `poppy`, `blue_orchid`, `allium`, `azure_bluet`, `red_tulip`, `orange_tulip`, `white_tulip`, `pink_tulip`, `oxeye_daisy` | Flowers that cows, mooshrooms, and sheep accept for breeding |
| `animaniax:manual_seeds` | Vanilla crop seeds plus optional `#c:seeds`, `#forge:seeds` | Seeds accepted for Field Guide crafting |
| `animaniax:milk_buckets` | `minecraft:milk_bucket` and all 5 Animaniax breed milk buckets | Milk bucket tag used in Slop and Milk Bottle recipes |
| `animaniax:omelette_cheeses` | All 5 `*_cheese_wedge` items | Cheese wedges accepted for Cheese and Ultimate Omelettes |
| `animaniax:cooked_bacon` | `animaniax:cooked_prime_bacon` | Bacon accepted for Bacon and Ultimate Omelettes |
| `animaniax:planks` | `#minecraft:planks` and optional `#c:planks` | Planks used for Trough, Bee Hive, and Cat Tower recipes |
| `animaniax:wooden_sticks` | `minecraft:stick` and optional `#c:rods/wooden` | Sticks used for Trough and Nest recipes |
| `c:dusts/salt` | `animaniax:salt` | Common salt tag |
| `c:sugars` | `minecraft:sugar` | Common sugar tag used in Chocolate Truffle recipe |
| `c:wools` / `minecraft:wool` | All 7 breed wool blocks (and `#minecraft:wool` in `c:wools`) | Breed wool integration |

## Block and fluid tags

| Tag | Default entries | Purpose |
| --- | --- | --- |
| `animaniax:forage_plants` | `#minecraft:crops`, `#minecraft:flowers`, `#minecraft:saplings`, mushrooms, `dead_bush`, `sweet_berry_bush`, `nether_wart`, `fern`, `large_fern` | Plants grazed by hungry livestock |
| `animaniax:pig_mud` | `animaniax:mud`, `minecraft:mud` | Mud blocks recognized by pigs for enrichment |
| `minecraft:goats_spawnable_on` | `minecraft:sand`, `minecraft:red_sand` | Allows goats to spawn in desert and badlands |
| `c:wools` / `minecraft:wool` (block) | All 7 breed wool blocks | Breed wool block integration |
| `c:milk` (fluid) | `animaniax:milk_generic`, `porting_lib:milk`, `milk:still_milk`, `minecraft:milk` (all optional) | Common milk fluid compatibility |

## Entity type, POI, and biome tags

### Entity type tags

- `animaniax:amphibians`: `common_frog`, `dart_frog`, `toad`
- `animaniax:amphibian_predators`: `minecraft:chicken`, `animaniax:peafowl`
- `animaniax:birth_breed_inheritance`: `cow`, `pig`, `sheep`, `goat`, `rabbit`
- `animaniax:natural_family_generators`: `cow`, `mooshroom`, `pig`, `sheep`, `goat`, `rabbit`, `animaniax:draft_horse`
- `animaniax:natural_spawn/*`: 13 spawn-cap group tags (`amphibians`, `cats`, `chickens`, `cows`, `dogs`, `ferrets`, `goats`, `hamsters`, `hedgehogs`, `horses`, `pigs`, `rabbits`, `sheep`)

### Point of interest tags

- `minecraft:acquirable_job_site`: includes `animaniax:pet_seller`

### Worldgen biome tags

| Tag | Biomes |
| --- | --- |
| `animaniax:cow_spawns` | `badlands`, `eroded_badlands`, `wooded_badlands`, `mangrove_swamp`, `frozen_peaks`, `jagged_peaks`, `stony_peaks` |
| `animaniax:goat_spawns` | `plains`, `sunflower_plains`, `savanna`, `savanna_plateau`, `windswept_savanna`, `badlands`, `eroded_badlands`, `wooded_badlands`, `desert` |
| `animaniax:draft_horse_spawns` | `plains`, `sunflower_plains`, `savanna`, `savanna_plateau`, `windswept_savanna`, `badlands`, `eroded_badlands`, `wooded_badlands` |
| `animaniax:peafowl_spawns` | `swamp`, `mangrove_swamp`, `jungle`, `sparse_jungle`, `bamboo_jungle` |
| `animaniax:cat_spawns` | `#minecraft:is_jungle`, `#minecraft:is_savanna` |
| `animaniax:dog_forest_spawns` | `#minecraft:is_forest` |
| `animaniax:dog_mountain_spawns` | `#minecraft:is_mountain` |
| `animaniax:dog_snow_spawns` | `snowy_plains`, `ice_spikes`, `snowy_taiga`, `grove`, `snowy_slopes`, `frozen_peaks`, `jagged_peaks`, `frozen_river`, `frozen_ocean`, `deep_frozen_ocean` |
| `animaniax:hamster_spawns` | `#minecraft:is_beach`, `desert`, `badlands`, `eroded_badlands`, `wooded_badlands` |
| `animaniax:ferret_spawns` | `#minecraft:is_savanna` |
| `animaniax:hedgehog_spawns` | `#minecraft:is_forest` |
| `animaniax:hedgehog_albino_spawns` | `swamp`, `mangrove_swamp` |
| `animaniax:common_frog_spawns` | `swamp`, `mangrove_swamp`, `river` |
| `animaniax:dart_frog_spawns` | `jungle`, `bamboo_jungle`, `sparse_jungle`, `forest`, `birch_forest`, `old_growth_birch_forest` |
| `animaniax:toad_spawns` | `swamp`, `mangrove_swamp`, `#minecraft:is_forest` |
| `animaniax:hive_spawns` | `#minecraft:is_jungle`, `#minecraft:is_forest`, `#minecraft:is_taiga`, `swamp`, `mangrove_swamp`, `plains`, `sunflower_plains` |
