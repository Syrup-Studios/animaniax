# Getting Started

This guide walks you through installing Animaniax, crafting your in-game Field Guide, checking on your animals, and setting up your first farm.

## Installation

1. Install Minecraft `1.21.1` with Fabric (Fabric Loader `>= 0.20.1` and Fabric API `>= 0.116.18+1.21.1`) or NeoForge (`>= 21.1.265`).
2. Download **Syrup Library** (`>= 0.6.0`) and **Animaniax**.
3. Put both `.jar` files in your `mods` folder on both the client and the server.

## Craft the Animaniax Field Guide

The **Animaniax Field Guide** is a 9-page in-game handbook covering animals, blocks, tools, and recipes. Use the left and right arrow keys or buttons to turn pages, and scroll with the mouse wheel or up and down arrow keys.

**Recipe (shapeless):**

- 1 Book
- 1 Crop Seed (Wheat, Pumpkin, Melon, or Beetroot Seeds)
- 1 Straw (craft 1 Wheat into 1 Straw)

## Check an animal's status

Sneak (`Shift`) and right-click any care-enabled animal with **both hands empty** to see its status in chat:

- **Hunger** (`0/100`) and **Thirst** (`0/100`)
- **Mud** enrichment (`0/100`, pigs only)
- **Mood:** `Content` or `Unhappy`, with a short tip if something is missing
- **Breed** or **Morph**
- **Sex:** `Female` or `Male`
- **Milk status:** `Lactating` or `Not lactating` (cows, mooshrooms, goats, and sheep)
- **Pregnancy:** Time remaining in ticks when pregnant
- **Fertility:** `Fertile` or `Sterilized`

## Set up your first farm

1. **Place an Animal Trough:** You need 2 open blocks side by side. Fill it with animal feed, water from a bucket, or slop for pigs.
2. **Lay down Straw:** Put Straw inside barns and pens so livestock and small pets have a bed to sleep on.
3. **Add Mud for pigs:** Craft Mud from a Water Bucket and 2 Dirt and place it in your pig pen so pigs can roll in it and stay Content.
4. **Place Chicken Nests:** Hens and peahens lay eggs inside nests. Keep an adult male nearby if you want eggs to hatch.
5. **Place a Salt Lick:** Injured farm animals will visit a Salt Lick to heal over time.
6. **Set out Pet Bowls:** Cats, dogs, and hamsters can eat and drink from Pet Bowls.

## Common first-day questions

### Why will my cow, goat, or sheep not give milk?

Only **Content, adult females that are lactating** can be milked. Females start lactating after they give birth to a baby. Milking also lowers the animal's thirst, so make sure she drinks water again before you try to collect another bucket or bottle. (Server owners can change a setting in [Configuration](configuration.md) so newly spawned cows start lactating.)

### Why are my chickens not laying eggs on the ground?

By default, hens lay eggs inside **Chicken Nests** instead of dropping loose eggs on the floor. Place a Chicken Nest nearby and right-click it with an empty hand (without sneaking) to collect eggs.

### Why will my animals not breed?

Check all five requirements:

1. You have **one male and one female** of the same species.
2. Both animals are **adults** and **Content** (fed, watered, and muddy for pigs).
3. Neither animal is **sterilized**, pregnant, or still in its post-birth cooldown.
4. Neither animal is **asleep**.
5. There are fewer than 15 animals of that species nearby.

### Why is my pig still unhappy after eating and drinking?

Pigs also need **Mud**. Place Mud (Animaniax Mud or vanilla Mud) where your pig can walk on it until its mud meter reaches 50 or higher.

### Why can I not feed or interact with my animal right now?

Your animal is probably **sleeping**. Livestock, dogs, cats, and ferrets sleep at night, hamsters and hedgehogs sleep during the day, and rabbits sleep in the late afternoon and late night. Wait until it wakes up.
