# Animal Care and Breeding

Animaniax gives farm animals and pets daily hunger, thirst, and sleep routines, along with pregnancy and baby growth.

## Hunger, thirst, and mood

Every care-enabled animal tracks **Hunger** and **Thirst** from `0` to `100`. Pigs also track **Mud** enrichment from `0` to `100`.

- Animals start at `100` hunger and `100` thirst.
- About every **10 minutes** (half a Minecraft day), each care meter drops to `49`.
- An animal is **Content** when both Hunger and Thirst are `50` or higher (and Mud is `50` or higher for pigs).

### Wild animals and neglect

- **Wild animals stay healthy on their own:** By default, wild animals do not lose hunger or thirst until a player first interacts with them (by feeding, watering, milking, or similar).
- **Hungry or thirsty (1 meter low):** The animal gets **Weakness I**.
- **Both hungry and thirsty:** The animal gets **Weakness II** and puffs dark smoke particles.
- **Starvation:** Starvation damage is **off by default**. If enabled in [Configuration](configuration.md), animals that are both hungry and thirsty take 2 hearts of damage every 20 seconds.
- **Ambiance Mode:** Server owners can enable Ambiance Mode in [Configuration](configuration.md) if they want animals that never need food or water.

## How to feed and water animals

- **By hand:** Right-click an awake animal with its favorite food or a Water Bucket (or compatible fluid container). Watering by hand refills thirst to `100`.
- **Animal Troughs:** Animals search up to 16 blocks away for a Trough with suitable food, water (1 bucket gives 10 drinks for large livestock, or 20 drinks for birds and small pets), or slop for pigs.
- **Pet Bowls:** Cats, dogs, and hamsters eat and drink from Pet Bowls.
- **Seed Piles and dropped seeds:** Hungry chickens and peafowl eat from placed Seed Piles and dropped seeds on the ground.
- **Natural grazing:** When hungry, cows, mooshrooms, pigs, sheep, goats, rabbits, and draft horses graze on grass blocks, crops, flowers, mushrooms, ferns, saplings, and berry bushes. By default, grazed crops and plants are eaten and removed.
- **Natural water:** Farm livestock can drink from nearby water source blocks when thirsty (removing the water block by default).
- **Salt Licks:** Injured farm livestock seek nearby Salt Licks to heal 1 heart per visit.

## Sleep schedules and beds

Animals sleep during their active rest hours when a valid bed block is nearby. While sleeping, animals stay still and ignore most feeding, watering, and breeding interactions until they wake up.

| Animal | When they sleep | Where they sleep |
| --- | --- | --- |
| Cows, Mooshrooms, Pigs, Sheep, Goats | Night (sunset to dawn) | Straw or Grass Block |
| Chickens, Peafowl, Draft Horses, Ferrets | Night (sunset to dawn) | Straw or Grass Block |
| Rabbits | Late afternoon and late night | Straw or Grass Block |
| Dogs | Night (sunset to dawn) | Dog Pillow or Straw |
| Cats | Night (sunset to dawn) | Cat Bed |
| Hamsters | Daytime (dawn to sunset) | Straw |
| Hedgehogs (standard and albino) | Daytime (dawn to sunset) | Straw or Grass Block |

## Breeding, pregnancy, and baby growth

### Requirements to breed

- Both animals must be **adults** of the same species and **opposite sexes** (one male and one female).
- Both must be **Content** and **Fertile** (not sterilized).
- Neither can be sleeping, pregnant, or in its post-birth cooldown (about 2 minutes after giving birth by default).
- By default, bonded pairs stick together unless multiple-female mating is turned on in [Configuration](configuration.md).
- Fewer than `15` animals of the same type can be within `80` blocks.

### Starting courtship

- By default, hand-feeding or watering an eligible adult puts it in love mode. Cows, mooshrooms, and sheep also accept common flowers (such as Dandelions, Poppies, Tulips, Blue Orchids, Alliums, Azure Bluets, and Oxeye Daisies) for breeding.
- If `Feed to Breed` is turned off in [Configuration](configuration.md), well-cared-for pairs breed on their own over time.

### Pregnancy, litters, and breeds

1. After mating, the mother becomes pregnant for about **17 minutes** (`20,000` ticks by default). Chickens and peafowl lay eggs in nests instead; see [Poultry and Draft Horses](animals/poultry-and-horses.md).
2. Litters always produce at least `1` baby, with a small default chance (`10%`) for extra twins or larger litters.
3. For cows, pigs, sheep, goats, and rabbits, babies inherit the father's breed if a matching father is within `30` blocks, or the mother's breed otherwise.
4. Once she gives birth, a female cow, mooshroom, goat, or sheep starts **lactating** so you can milk her.

### Baby growth

Babies grow in stages over time, but **growth pauses if they are hungry, thirsty, or asleep**. Keep young animals fed and watered so they reach adulthood smoothly.

### Sterilizing males

If you want to stop a male animal from breeding or fertilizing nest eggs, right-click an **adult male** with a **Carving Knife**. This uses 1 durability on the knife and permanently marks that male as sterilized.

## Advancements

Hand-feeding each breed or morph unlocks **Breed Feeder** advancements for your world.
