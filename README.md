# CauldronInteract

[![build](https://github.com/VoxCrafterLP/CauldronInteract/actions/workflows/gradle.yml/badge.svg)](https://github.com/VoxCrafterLP/CauldronInteract/actions/workflows/gradle.yml)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/VoxCrafterLP/CauldronInteract/total?label=Downloads)
![GitHub](https://img.shields.io/github/license/VoxCrafterLP/CauldronInteract)
![MC version](https://img.shields.io/badge/Minecraft%20version-1.17.x%20or%20higher-brightgreen)
![bStats Servers](https://img.shields.io/bstats/servers/12031)
![Static Badge](https://img.shields.io/badge/Link-Modrinth-%2300AF5C?logo=modrinth&link=https%3A%2F%2Fmodrinth.com%2Fplugin%2Fcauldroninteract)

CauldronInteract is a simple 1.17+ plugin, which enables dispensers to interact with cauldrons. 
Dispensers can use buckets to fill or empty a cauldron. Filling bottles is also supported.

Tested versions: 1.17, 1.18, 1.19, 1.20, 1.21
## Use case

A possible use for this plugin would be an integration into an automatic lava farm using dripstone blocks. 
This would allow dispensers to empty the cauldrons and fill chests with lava buckets.

<img src="/images/2026-02-04_21.00.58.png" width="800">
<img src="/images/2026-02-04_21.01.07.png" width="800">

## Configurable option: Upgradable dispensers

This feature enables players to individually select dispensers that can interact with cauldrons. For instance, 
this can be useful if the plugin disrupts a redstone circuit. To upgrade a dispenser, players need to hold a hoe and 
shift-right-click the dispenser..

If this feature is toggled off and then back on, all previously upgraded dispensers will continue to function as before.

<img src="/images/2024-12-01_22.25.07.png" width="800">

To enable this option, enable the feature in the plugin's config.
```yaml
# If enabled every dispenser will have to be upgraded first to unlock the ability to interact with a cauldron.
# Dispensers can be upgraded by being shift-right-clicked while holding a hoe.
enable-dispenser-upgrade: true
```

## Configurable option: Smart dispensers

Smart dispensers automatically trigger when the cauldron in front of them fills up naturally (e.g. from rain or dripstone), without needing an external redstone signal. To create a smart dispenser, hold a hoe in your main hand and an observer in your off-hand, then shift-right-click the dispenser. The observer is consumed in the process.

If the upgradable dispensers feature is also enabled, a smart dispenser counts as upgraded and does not need to be separately upgraded.

To enable this option, enable the feature in the plugin's config.
```yaml
# If enabled, a smart dispenser can be created by shift-right-clicking while holding a hoe in the main-hand and an
# observer in the off-hand. A smart dispenser doesn't require an external redstone signal but waits for the cauldron
# in front of it to fill up and then triggers itself automatically. The process of creating a smart dispenser consumes
# an observer. A smart observer only works for naturally filled cauldrons, emptying a bucket into it won't do anything!
enable-smart-dispensers: true
```

## Installation

The installation is fairly straightforward. Download the plugin from the [releases tab](https://github.com/VoxCrafterLP/CauldronInteract/releases) and put the .jar file into 
the `plugins` folder. Make sure that you are using a 1.17+ server.

For a terminal environment, use this:
```bash
cd plugins/
wget https://github.com/VoxCrafterLP/CauldronInteract/releases/download/v1.4.0/CauldronInteract-1.4.0-RELEASE.jar
```

## Bugreports and features

If you have found a bug, or you want to request a feature, feel free to [create an issue](https://github.com/VoxCrafterLP/CauldronInteract/issues/new).

## License
This project is licensed under the GNU GPL v3 and may be used accordingly. Further information can be found [here](https://github.com/VoxCrafterLP/CauldronInteract/blob/master/LICENSE).
