# CauldronInteract

[![build](https://github.com/VoxCrafterLP/CauldronInteract/actions/workflows/maven.yml/badge.svg)](https://github.com/VoxCrafterLP/CauldronInteract/actions/workflows/maven.yml)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/VoxCrafterLP/CauldronInteract/total?label=Downloads)
![GitHub](https://img.shields.io/github/license/VoxCrafterLP/CauldronInteract)
![MC version](https://img.shields.io/badge/Minecraft%20version-1.17.x%20or%20higher-brightgreen)
![bStats Servers](https://img.shields.io/bstats/servers/12031)

CauldronInteract is a simple 1.17+ plugin, which enables dispensers to interact with cauldrons. Dispensers can use buckets to fill or empty a cauldron. Filling bottles is also supported.

Tested versions: 1.17, 1.18, 1.19, 1.20, 1.21
## Use case

A possible use for this plugin would be an integration into an automatic lava farm using dripstone blocks. This would allow dispensers to empty the cauldrons and fill chests with lava buckets.

<img src="/images/2024-04-07_21.48.31.png" width="800">

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

## Installation

The installation is fairly straightforward. Download the plugin from the [releases tab](https://github.com/VoxCrafterLP/CauldronInteract/releases) and put the .jar file into the `plugins` folder. Make sure that you are using a 1.17+ server.

For a terminal environment, use this:
```bash
cd plugins/
wget https://github.com/VoxCrafterLP/CauldronInteract/releases/download/v1.2.3/CauldronInteract-1.2.3-RELEASE.jar
```

## Bugreports and features

If you have found a bug, or you want to request a feature, feel free to [create an issue](https://github.com/VoxCrafterLP/CauldronInteract/issues/new).

## License
This project is licensed under the GNU GPL v3 and may be used accordingly. Further information can be found [here](https://github.com/VoxCrafterLP/CauldronInteract/blob/master/LICENSE).
