# Configuration

This page is for server owners and players who want to customize world rules. Animaniax stores per-world settings in `<world>/serverconfig/animaniax.json5` using Syrup Library's configuration manager. Restart the world after editing this file.

## Legacy migration

If `<world>/serverconfig/animaniax.json5` does not exist when a world loads, Animaniax automatically reads `<world>/serverconfig/animaniax.properties` (if present) and migrates all values into `animaniax.json5`.

## Care, sleep, and neglect settings

| Key | Type | Default | Range | Description |
| --- | --- | --- | --- | --- |
| `feed_timer` | int | `12000` | `1` to `2147483000` | Base ticks before an animal becomes hungry. |
| `water_timer` | int | `12000` | `1` to `2147483000` | Base ticks before an animal becomes thirsty. |
| `ticks_between_ai_firings` | int | `100` | `1` to `2147483000` | Goal checks between food, water, and mud searches. |
| `ai_block_search_range` | int | `16` | `1` to `64` | Search radius for food, water, and salt lick blocks. |
| `require_animal_interaction_for_ai` | boolean | `true` | `true` / `false` | Require player interaction before care timers activate. |
| `ambiance_mode` | boolean | `false` | `true` / `false` | Animals do not need to eat or drink. |
| `animals_starve` | boolean | `false` | `true` / `false` | Animals take starvation damage when hungry and thirsty. |
| `starvation_timer` | int | `400` | `1` to `2147483000` | Ticks between starvation damage hits. |
| `show_unhappy_particles` | boolean | `true` | `true` / `false` | Emit smoke particles when both hungry and thirsty. |
| `animals_sleep` | boolean | `true` | `true` / `false` | Animals sleep during their scheduled hours on valid beds. |
| `animals_can_attack_others` | boolean | `true` | `true` / `false` | Allows bull retaliation, sparring, and animal hunting goals. |
| `plants_removed_after_eating` | boolean | `true` | `true` / `false` | Remove grazed plants and crops after eating. |
| `water_removed_after_drinking` | boolean | `true` | `true` / `false` | Consume natural water source blocks when drunk. |
| `cows_milkable_at_spawn` | boolean | `false` | `true` / `false` | Newly spawned adult female cows and mooshrooms start lactating. |

## Breeding, growth, and poultry settings

| Key | Type | Default | Range | Description |
| --- | --- | --- | --- | --- |
| `gestation_timer` | int | `20000` | `1` to `2147483000` | Base pregnancy length in ticks. |
| `birth_multiple_chance` | double | `0.1` | `0.0` to `1.0` | Chance for each additional offspring (`1` minimum, up to `64`). |
| `animal_loss_chance` | double | `0.0` | `0.0` to `1.0` | Chance to lose a litter at birth when both hunger and thirst are low. |
| `child_growth_tick` | int | `200` | `1` to `2147483000` | Growth interval in ticks (applies only when cared for and awake). |
| `mating_timer` | int | `100` | `1` to `2147483000` | Ticks between automatic courtship checks. |
| `feed_to_breed` | boolean | `true` | `true` / `false` | Feeding triggers breeding instead of passive courtship alone. |
| `males_mate_multiple_females` | boolean | `false` | `true` / `false` | Allow males to mate outside bonded pairs. |
| `entity_breeding_limit` | int | `15` | `1` to `2147483000` | Maximum nearby same-type animals allowed for breeding and hatching. |
| `animal_cap_search_range` | int | `80` | `1` to `2147483000` | Search radius for breeding and natural spawn caps. |
| `laid_timer` | int | `2000` | `1` to `2147483000` | Ticks between eggs laid in nests. |
| `egg_hatch_chance` | int | `2` | `1` to `2147483000` | Chance of hatching an egg in a nest (`1` in `N`). |
| `allow_egg_throwing` | boolean | `false` | `true` / `false` | Allow throwing chicken eggs. |
| `chickens_drop_eggs` | boolean | `false` | `true` / `false` | Allow hens to drop eggs without a nest. |
| `birds_drop_feathers` | boolean | `true` | `true` / `false` | Chickens and male peafowl shed feathers over time. |
| `feather_timer` | int | `12000` | `1` to `2147482001` | Ticks between feather shedding events. |

## Blocks, production, and machines

| Key | Type | Default | Range | Description |
| --- | --- | --- | --- | --- |
| `allow_seed_dispenser_placement` | boolean | `true` | `true` / `false` | Allow dispensers to place seed piles. |
| `shift_seed_placement` | boolean | `false` | `true` / `false` | Require sneaking to place seed piles with seeds. |
| `allow_trough_automation` | boolean | `true` | `true` / `false` | Allow hoppers and pipes to insert or extract trough feed. |
| `salt_lick_tick` | int | `8000` | `1` to `2147483000` | Ticks between salt lick uses by an animal. |
| `salt_lick_max_uses` | int | `200` | `1` to `1000000` | Maximum uses for a new salt lick. |
| `cheese_maturity_time` | int | `24000` | `1` to `2147483000` | Ticks required to mature cheese or salt in a mold. |
| `salt_creation_amount` | int | `16` | `1` to `64` | Salt items produced from water in a cheese mold. |
| `disable_salt_creation` | boolean | `false` | `true` / `false` | Prevent creating salt in cheese molds with water. |
| `wool_regrowth_timer` | int | `8000` | `1` to `2147483000` | Ticks before sheep or Angora goat wool regrows after shearing. |
| `hamster_wheel_capacity` | int | `200000` | `1` to `2147483000` | RF energy capacity of the hamster wheel. |
| `hamster_wheel_r_f_generation` | int | `20` | `0` to `2147483000` | RF generated per tick by an active hamster wheel. |
| `hamster_wheel_use_time` | int | `2000` | `1` to `2147483000` | Ticks per hamster food item consumed in the wheel. |
| `hive_wild_honey_rate` | int | `700` | `1` to `2147483000` | Base ticks per `25` mB honey cycle in wild hives. |
| `hive_playermade_honey_rate` | int | `450` | `1` to `2147483000` | Base ticks per `25` mB honey cycle in player-made hives. |

## World generation and spawn caps

| Key | Type | Default | Range | Description |
| --- | --- | --- | --- | --- |
| `replace_vanilla_wolves` | boolean | `true` | `true` / `false` | Suppress natural vanilla wolf spawns. |
| `replace_vanilla_ocelots` | boolean | `true` | `true` / `false` | Suppress natural vanilla ocelot spawns. |
| `hive_spawning` | boolean | `true` | `true` / `false` | Enable wild bee hive generation. |
| `hive_spawning_frequency` | int | `3` | `0` to `10` | Wild hive generation chance (`N` out of `200`) per eligible chunk. |
| `natural_spawn_limit_cows` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for cows and mooshrooms. |
| `natural_spawn_limit_pigs` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for pigs. |
| `natural_spawn_limit_chickens` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for chickens. |
| `natural_spawn_limit_sheep` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for sheep. |
| `natural_spawn_limit_goats` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for goats. |
| `natural_spawn_limit_horses` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for draft horses. |
| `natural_spawn_limit_rabbits` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for rabbits. |
| `natural_spawn_limit_amphibians` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for frogs and toads. |
| `natural_spawn_limit_hamsters` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for hamsters. |
| `natural_spawn_limit_ferrets` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for ferrets. |
| `natural_spawn_limit_hedgehogs` | int | `40` | `1` to `2147483000` | Nearby natural spawn cap for hedgehogs. |
| `natural_spawn_limit_cats` | int | `20` | `1` to `2147483000` | Nearby natural spawn cap for cats. |
| `natural_spawn_limit_dogs` | int | `20` | `1` to `2147483000` | Nearby natural spawn cap for dogs. |

## Food settings

| Key | Type | Default | Description |
| --- | --- | --- | --- |
| `foods_give_bonus_effects` | boolean | `true` | Apply status effects when eating Animaniax foods, cheese, and honey. |
| `eat_food_anytime` | boolean | `true` | Allow eating Animaniax foods and honey when hunger is full. |
| `food_value_overrides` | string | `""` | Custom nutrition and saturation multiplier overrides (see below). |

### Food value overrides syntax

`food_value_overrides` uses semicolon-separated entries in the form `item_id(nutrition,saturationMultiplier)`:

```text
animaniax:plain_omelette(3,0.5);animaniax:milk_bottle(6,0.5)
```

- Applies to Animaniax food items and `animaniax:milk_bottle`, as well as tamed wolf/dog healing values.
- Does not apply to `animaniax:honey_bottle`.
- Preserves existing food bonus effects and container remainders. Invalid entries are logged and ignored.
