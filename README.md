# Quantum Boots

A NeoForge mod for **Minecraft 26.2** that overhauls how armour works. Instead of one generic armour number, every piece protects against specific kinds of damage, each material has its own specialty, and an in-inventory overlay shows exactly what you're protected against. It also adds **Quantum Boots**: fall-proof boots that boost your jump while you sprint.
The idea and values for stats (excluding copper) is based upon Better Than Adventure's implementation which can be read about in further detail here <https://bta.miraheze.org/wiki/Armor>

## Features

### Damage-type protection

Worn armour adds up protection percentages across five damage categories:

- **Projectile**
- **Blast**
- **Fall**
- **Fire**
- **Lightning** (currently broken)

Each material has a specialty. Full-set protection:

| Material | Projectile | Blast | Fall | Fire | Lightning |
|---|---|---|---|---|---|
| Leather | 20% | 20% | **99%** | 20% | 0% |
| Copper | 33% | 33% | 33% | 33% | **99%** |
| Iron | 45% | 45% | 45% | 45% | 0% |
| Gold | 70% | 70% | 70% | 70% | 0% |
| Chainmail | **99%** | 35% | 35% | 35% | 0% |
| Diamond | 66% | 66% | 66% | **99%** | 0% |
| Netherite | 55% | **99%** | 55% | 55% | 0% |

### Quantum Boots

- **Total fall damage immunity**
- **Jump Boost while sprinting**, refreshed continuously while you run
- Protection stats matching diamond boots, whilst the vanilla stats match with Netherrite

### Other armour changes

- **Leather boots** stop you from trampling farmland.
- **A full copper set** attracts lightning during thunderstorms when you can see the sky, but gives strong lightning protection to compensate.
- **Chainmail armour** can be crafted from iron chains.

## Credits

- Armour protection values are adapted from [Better Than Adventure](https://bta.miraheze.org/wiki/Armor). This mod is not affiliated with or endorsed by Better Than Adventure.
- Built on the [NeoForge](https://neoforged.net/) MDK.
