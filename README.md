[# Cobblemon Spawn Alerts
A highly customizable Cobblemon sidemod to alert you when a certain Pokemon spawns

## IMPORTANT:<br>
#### Some Pokemon info may not display properly if you are on a server!
Servers can optionally install this mod to broadcast this info to clients if they desire, also with a config of its own. If a server does not have this mod installed, then things will not display correctly.<br>
Also, while the serverside mod is optional, all clients must have the mod if the server does.

#### 1.12.0 update:
As of 1.12.0, the Adventure library has been replaced with [Ember's Text API](https://modrinth.com/mod/embers-text-api). This will break existing configs. See the full changelog for more details.

## No more staring at the minimap!
Have you ever been hunting for an ultra-rare, and as you're flying around your eyes are too focused on reading each Pokemon's name that you miss something? Well with this mod, you can simply receive a message in chat when the Pokemon spawns instead! The config is a JSON file that is very easy to edit and add any Pokemon you want.

## Speaking of minimaps...
CSA is fully compatible with Journeymap and can automatically create and remove waypoints when Pokemon spawn/despawn. More details below in the Journeymap Integration tab.

## Complete the Pokedex!
By simply editing the config, you can be alerted whenever an unregistered or uncaught Pokemon spawns near you!

## Customizability!
Each Pokemon can be individually customized exactly to your needs. If you want to shiny hunt for a Ralts while making sure you don't miss out on any beautiful Bidoofs (i love bidoof), you can do that. Messages use Ember's Text API markup to easily color or format messages however you like (see the [ETA docs](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/)). <br>The default message looks like this:<br>
![Default message](https://cdn.modrinth.com/data/cached_images/4b5500d73cb2c2d1a630cc5c1bee5b220bdb9eb7.png)<br>
But can be modified to look like this, or however you want!
![Cool bidoof](https://cdn.modrinth.com/data/cached_images/21ac636baa53001eb530b22ff3b57d1b0b5813d0.png)

## Global Alerts!
Inspired by [Cobblemon Spawn Notification](https://modrinth.com/mod/cobblemon-spawn-notification), all players will be alerted when a rare Pokemon (such as a shiny or legendary) spawns. This can be disabled in the server's `server.json` config for the mod. The server simply sends a packet to all clients, so that each player can individually customize their messages.

## FAQ:
### My alerts don't work when using commands?
This is intended behavior, which I still need to find a better solution for (if one exists). You *can* use `enableSpawnCommandAlerts` in `server.json`, but **use this at your own risk**. This can break spawning of some Pokemon, such as in Cobblemon: Path to Legends.<br>
*Technical details: This mixins to SpawnPokemon#execute and uses the player spawner system to forcibly post a spawn event.*
### Support for Xaero's Minimap waypoints?
Unfortunately, no. While it is technically already possible via modifying templates and using custom MiniMessage scripts in your alerts, I will not be adding direct compatibility to the mod. Xaero's is completely closed source, offering no API, no wiki, and no Discord server. I am not going to go through the effort of trying to figure out how to integrate someone's mod when they provide no resources for doing such. However, if someone wants to PR this, I'd love to make compatibility possible.
### When will you...
Unless it's a genuine issue with the mod, I add features at my own pace. If you get upset with a feature taking too long, feel free to PR it.
### In-game editable config?
See above.

## Known Issues:
- Cobblemon Academy 2.0 makes some changes to the configs, causing shinies not to alert by default. In the CSA `main.json` config (use `/csa openconfig` to open the directory), set `alertAllShinies` to true. Then run `/csa reload`. Shiny alerts should work now.
- Forgot to add `broadcastBucket` to server config in 1.12.0. Oops

## Config:
<details>
  <summary>About/Reloading the Configs</summary>
  
  ### General info:
  - Found in your Minecraft instance's `config` folder. Use `/csa openconfig` to open the directory.
  - Re-entering a world will **NOT** reload the configs. You must use their respective commands to reload them while the game is running, or restart the game.
  - Configs are separated into `client` and `common`. The client configs are `main.json`, `pokemon.json`, and `message_templates.json`. The common configs are `server.json` and `rarities.json`.
  - For a detailed description of each config, see their respective categories.
  ### Client configs:
  - Reloaded via `/csa reload`.
  - These only affect things on **your** own client. They affect what you *see* and what you *hear*. These do nothing on a server and do not at all affect other players.
  ### Common configs:
  - Reloaded via `/csa-common reload`.
  - These affect things on your client, **and** on servers. They affect what clients *know* about a Pokemon.
  - `server.json` is only used if you are the server host (e.g. singleplayer, LAN, dedicated server).
  - `rarities.json` is used by the server and clients. On the server, it only cares about `starters`. On clients, everything is used.
</details>
<details>
<summary>Config - Main (client)</summary>

### Config Settings:<br>
* **debug**: Enables debug messages in chat.<br>
* **multiplayerWarning**: Enables a warning message when joining a server.
* **enableAlerts**: Enables alert messages, excluding despawns.
* **enableDespawnAlerts**: Enables alert messages for when alerted Pokemon despawn. *Note: this is limited to Pokemon which have been globally alerted, since clients don't have a reliable way to track despawns.*
* **alertAllShinies**: Alerts when any shiny spawns, unless `alertShiny` is disabled in its config.<br>
* **alertAllLegendaries**: Alerts when any legendary spawns.<br>
* **alertAllMythicals**: Alerts when any mythical spawns.<br>
* **alertAllUltraBeasts**: Alerts when any ultra beast spawns.<br>
* **alertAllParadox**: Alerts when any paradox spawns.<br>
* **alertAllStarter**: Alerts when any starter spawns.<br>
* **alertAllNotInDex**: Alerts when any Pokemon which is not already registered in your Pokedex spawns.<br>
* **alertAllUncaught**: Alerts when any Pokemon which you have not caught spawns.<br>
* **alertEverything**: Alerts every single spawn near you. Why would you want to do this? Idk.
* **bucketsToAlert**: A list of buckets to alert spawns from. E.g. `"ULTRA_RARE", "RARE"`. *Note: this will only work if the server provides the bucket info.*

**Level filter**: Allows setting min and max levels for triggering alerts.<br>
**Distance filter**: Allows setting min and max distance, in blocks, for triggering alerts.

For IV/EV hunting, see the IV/EV Hunting tab under Config
</details>

<details>
<summary>Config - Pokemon (client)</summary>

### IMPORTANT:
All custom configs must have the Pokemon's name in the language you are playing on! If you are playing Cobblemon on any language other than English, you must ensure that Pokemon names are written in that language, otherwise the configs will not work! This does not apply to the default config, that will work fine.<br>
Alternatively, you can also use the Pokedex number.

`pokemon.json` is where the bulk of the config is at. By default, the config will come with a default set of options that will be applied to any alert for a Pokemon that is not added to the config. **DO NOT DELETE OR RENAME THIS.** You can freely edit its contents, but leave the name as it is *exactly*. You can copy the formatting and change the name of the Pokemon to add a new spawn message for any other Pokemon.<br>
### Config Parameters:<br>
The name of each Pokemon's config can be formatted like "x, y, z" to include multiple Pokemon. E.g. "charmander, charmeleon, charizard" will alert to the whole Charmander line.
* **enabled**: Enables the spawn message for the Pokemon. If set to false, this setting will override every other config setting for the Pokemon and make its spawn message never display.
* **alwaysAlert**: Whether to always alert the Pokemon's spawn message, assuming enabled is set to true. Setting this to false will only display a spawn message given some other condition is true (e.g. alertShiny).
* **alertShiny**: Whether to alert a shiny Pokemon, or if the Pokemon is shiny. If alwaysAlert is set to false, this will ONLY alert that Pokemon's spawn if it is shiny. If alwaysAlert is set to true, then it will simply specify if the spawned Pokemon is shiny.
* **alertHiddenAbility**: Whether to alert a shiny Pokemon, or if the Pokemon has a Hidden Ability. This requires the mod to be installed server side and the server must have abilities being broadcast in the server config.
* **showLegendary**: Shows whether the Pokemon is legendary, mythical, paradox, or ultra beast. This uses the `rarities.json` config.
* **showBucket**: Shows whether the bucket a Pokemon spawned from. This requires the server to broadcast the bucket to clients.
* **customAlertMessage**: Used to create a custom alert message for a Pokemon using [ETA markup](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/). See the Custom Alert Messages tab for more info.
* **customAlertTooltip**: Used to add custom hoverable tooltips to an alert message.
* **customAlertClickEvent**: Used to add custom click events to an alert message. Click events use the following syntax: `event_name:action`. The click events are `open_url`, `open_file`, `run_command`, `suggest_command`, `change_page`, and `copy_to_clipboard`. For example, `run_command:/csa openconfig` would open the config folder when clicked on.
* **autoGlow**: Makes a Pokemon glow when alerted. Click the alert to toggle the glow.
* **glowColor**: Changes the glow color of autoGlow. Formatted as ARGB (#AARRGGBB). Alpha does not actually affect anything.
#### The following are a sub category of the config for displaying certain info about a Pokemon. Note that some info requires the server to broadcast it. These have 3 options -- `"MAIN_MESSAGE"`, `"HOVER"`, and `"DISABLED"`:
* **level**: Shows the Pokemon's level.
* **ivs**: Lists the Pokemon's IVs in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.
* **evs**: Lists the Pokemon's EV Yield in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.
* **nature**: Shows the Pokemon's nature.
* **ability**: Shows the Pokemon's ability.
* **gender**: Shows the Pokemon's gender.
* **coordinates**: Shows the Pokemon's coordinates (x/y/z).
* **biome**: Shows the biome the Pokemon spawned in.
* **nearestPlayer**: Shows the nearest player to the spawned Pokemon.
<br><br>
You will also see sounds listed below the stat displays. See the Custom Sounds tab under Config.

### Examples: <br>

<details>
  <summary>Full template example</summary>

# This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
```json
{
  "configVersion": "1.11.3",
  "pokemonConfigs": {
    "default (You can modify anything BELOW this, but dont delete it!)": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "ability": "HOVER",
        "gender": "HOVER",
        "coordinates": "DISABLED",
        "biome": "MAIN_MESSAGE",
        "nearestPlayer": "MAIN_MESSAGE"
      },
      "customAlertMessage": "",
      "sounds": {
        "shiny": "",
        "legendary": "",
        "mythical": "",
        "ultrabeast": "",
        "paradox": "",
        "starter": "",
        "unregistered": "",
        "uncaught": "",
        "ivs": "",
        "evs": ""
      },
      "customAlertSound": "",
      "autoGlow": false,
      "journeyMap": {
        "enableWaypoint": true,
        "waypointName": "",
        "waypointHexColor": "",
        "persistent": false
      }
    },
    "bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "ability": "HOVER",
        "gender": "DHOVER",
        "coordinates": "DISABLED",
        "biome": "MAIN_MESSAGE",
        "nearestPlayer": "MAIN_MESSAGE"
      },
      "customAlertMessage": "<rainbow>A beautiful <gradient:light_purple:white><b>{shiny_unformatted}</b></gradient><u>{name}</u> spawned in a <u>{biome_unformatted}</u> biome</rainbow><white>{coords}!</white>",
      "sounds": {
        "shiny": "",
        "legendary": "",
        "mythical": "",
        "ultrabeast": "",
        "paradox": "",
        "starter": "",
        "unregistered": "",
        "uncaught": "",
        "ivs": "",
        "evs": ""
      },
      "customAlertSound": "",
      "autoGlow": false,
      "journeyMap": {
        "enableWaypoint": true,
        "waypointName": "",
        "waypointHexColor": "",
        "persistent": false
      }
    }
  }
}
```
</details>

Show all stats in message:<br>

# This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
```json
"bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "MAIN_MESSAGE",
        "evs": "MAIN_MESSAGE",
        "nature": "MAIN_MESSAGE",
        "ability": "MAIN_MESSAGE",
        "gender": "MAIN_MESSAGE",
        "coordinates": "MAIN_MESSAGE",
        "biome": "MAIN_MESSAGE",
        "nearestPlayer": "MAIN_MESSAGE"
      },
      ...other config stuff
},
```
![All stats in main message](https://cdn.modrinth.com/data/cached_images/91a4c7f5079243efff2aba7e84f7044c5f8048c4.png)
Show all stats in hover:<br>

# This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
```json
"bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "HOVER",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "ability": "HOVER",
        "gender": "HOVER",
        "coordinates": "HOVER",
        "biome": "HOVER",
        "nearestPlayer": "HOVER"
      },
      ...other config stuff
},
```
![Hoverable stats](https://cdn.modrinth.com/data/cached_images/b5e4af0678a754053ae022708d1ab867846206a0.png)

</details>

<details>
<summary>Config - Templates (client)</summary>

  The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/csa openconfig`.<br><br>

`message_templates.json` is where you can find and edit the default messages, and message parts. These apply to EVERY spawn, with the exception of `fullSpawnMessage`, which is only used when a custom spawn message is not provided. These templates are what replace the dynamic replacement placeholders.<br>

### Finding the Default Templates:<br>
The default values for the templates are found in your Minecraft instance's language file for the mod.

<details>
  <summary>The defaults for en_us.json (the only currently added language)</summary>

  # This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
```json
  {
  "cobblemon-spawn-alerts.client_config_reloading": "<green>[CSA] </green><white>Client config reloading...</white>",
  "cobblemon-spawn-alerts.client_config_reloaded": "<green>[CSA] </green><white>Client config reloaded!</white>",
  "cobblemon-spawn-alerts.client_config_reload_failed": "<green>[CSA] </green><red>Client config reload failed.</red>",
  "cobblemon-spawn-alerts.config_load_failed": "<green>[CSA] </green><red>Config failed to load properly while loading `%s`.</red>",
  "cobblemon-spawn-alerts.config_save_failed": "<green>[CSA] </green><red>Config failed to save properly while saving `%s`.</red>",
  "cobblemon-spawn-alerts.multiplayer_warning": "<green>[CSA]</green> <yellow>WARNING!</yellow> <white>You are playing on a server. If the server doesn't have the mod installed, or has disabled broadcasting of Pokemon info, certain things, like IVs, EV yield, or Nature, may be displayed incorrectly!",

  "cobblemon-spawn-alerts.default_spawn_message": "<green>A wild {legendary}{shiny}{HA}{gender}<white>{name}</white> {level}{ivs}{evs}{nature}{ability}has appeared{nearest_player}{coords}{biome}!</green>",
  "cobblemon-spawn-alerts.default_despawn_message": "<green>A {legendary}{shiny}{HA}<white>{name}</white> {despawned}.",
  "cobblemon-spawn-alerts.despawn_reason_despawned": "despawned",
  "cobblemon-spawn-alerts.despawn_reason_captured": "was captured by %s",
  "cobblemon-spawn-alerts.despawn_reason_fainted": "was defeated by %s",
  "cobblemon-spawn-alerts.shiny": "<gold>Shiny </gold>",
  "cobblemon-spawn-alerts.shiny_unformatted": "Shiny ",
  "cobblemon-spawn-alerts.hidden_ability": "<aqua>Hidden Ability </aqua>",
  "cobblemon-spawn-alerts.hidden_ability_unformatted": "Hidden Ability ",
  "cobblemon-spawn-alerts.level": "<gray>(Lvl. %s) </gray>",
  "cobblemon-spawn-alerts.level_hover": "Level: <gray>%s</gray>",
  "cobblemon-spawn-alerts.level_unformatted": "%s",
  "cobblemon-spawn-alerts.ivs": "with IVs: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.ivs_hover": "IVs: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.ivs_unformatted": "(%s/%s/%s/%s/%s/%s)",
  "cobblemon-spawn-alerts.evs": "with EV yield: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.evs_hover": "EV Yield: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.evs_unformatted": "(%s/%s/%s/%s/%s/%s)",
  "cobblemon-spawn-alerts.nature": "with Nature: <gray>%s</gray> ",
  "cobblemon-spawn-alerts.nature_hover": "Nature: <gray>%s</gray> ",
  "cobblemon-spawn-alerts.nature_unformatted": "%s",
  "cobblemon-spawn-alerts.ability": "with Ability: <gray>%s</gray> ",
  "cobblemon-spawn-alerts.ability_hover": "Ability: <gray>%s</gray> ",
  "cobblemon-spawn-alerts.ability_unformatted": "%s",
  "cobblemon-spawn-alerts.gender": "%s ",
  "cobblemon-spawn-alerts.gender_hover": "Gender: %s",
  "cobblemon-spawn-alerts.gender_unformatted": "%s",
  "cobblemon-spawn-alerts.male": "<aqua>♂ %s</aqua>",
  "cobblemon-spawn-alerts.female": "<light_purple>♀ %s</light_purple>",
  "cobblemon-spawn-alerts.genderless": "<gray>%s</gray>",
  "cobblemon-spawn-alerts.coords": " at <gray>(%s, %s, %s)</gray>",
  "cobblemon-spawn-alerts.coords_hover": "Coordinates: <gray>(%s, %s, %s)</gray>",
  "cobblemon-spawn-alerts.coords_unformatted": "(%s, %s, %s)",
  "cobblemon-spawn-alerts.biome": " in a <gray>%s</gray> biome",
  "cobblemon-spawn-alerts.biome_hover": "Biome: <gray>%s</gray>",
  "cobblemon-spawn-alerts.biome_unformatted": "%s",
  "cobblemon-spawn-alerts.nearest_player": " near player: <gray>%s</gray>",
  "cobblemon-spawn-alerts.nearest_player_hover": "Nearest Player: <gray>%s</gray>",
  "cobblemon-spawn-alerts.nearest_player_unformatted": "%s",
  "cobblemon-spawn-alerts.legendary": "<light_purple>Legendary </light_purple>",
  "cobblemon-spawn-alerts.legendary_unformatted": "Legendary",
  "cobblemon-spawn-alerts.mythical": "<light_purple>Mythical </light_purple>",
  "cobblemon-spawn-alerts.mythical_unformatted": "Mythical",
  "cobblemon-spawn-alerts.ultrabeast": "<light_purple>Ultra Beast </light_purple>",
  "cobblemon-spawn-alerts.ultrabeast_unformatted": "Ultra Beast",
  "cobblemon-spawn-alerts.paradox": "<light_purple>Paradox </light_purple>",
  "cobblemon-spawn-alerts.paradox_unformatted": "Paradox"
}
```
</details>

### Modifying Templates:<br>
When modifying templates, keep in mind the spacing and parameters. `%s` is used internally to insert values, and the amount of them **MUST** match the default template. For example, if you are modifying the template for IVs, your new template must have EXACTLY 6 `%s` - no more, no less. These also insert values in order, so unfortunately changing the order of IVs or coordinates is currently impossible.<br>**Note:** Some of the default templates have spaces in them to make formatting work properly, so just be aware of that if you are modifying a template.<br>
### Examples:<br>
# This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
Change the shiny color for all Pokemon:<br>
```json
"shiny": "<blue><b>Shiny </b></blue>",
```
![Changing the shiny color](https://cdn.modrinth.com/data/cached_images/1a7e3de779a529774fa0d200feaea67ced201e9e.png)<br><br>
Change the default spawn message for all Pokemon:
```json
"fullSpawnMessage": "<white>A wild {shiny}{level}<blue><b>{name}</b></blue> has appeared{coords}!</white>",
"shiny": "<gold><b>Shiny</b> </gold>",
"level": "<gray>Level %s </gray>",
```
![Changing the default message for all Pokemon](https://cdn.modrinth.com/data/cached_images/f0afb43d8dc2f596a2ea8ccd1f04436745af5747.png)

</details>

<details>
<summary>Config - Server (common)</summary>

### Config Settings:<br>
* **enableSpawnCommandAlerts**: Enables alerts for Pokemon spawned via commands. IMPORTANT - This can break other addons, such as Cobblemon: Path to Legends! Only enable this if you know what you're doing, or if you know the addon is compatible with this option.<br>
* **alertX**: Sends info about the Pokemon belonging to X group to all players on the server, allowing clients to alert them.
* **broadcastX**: Sends X info about the Pokemon to all players on the server.
**Note:** Disabling these on your own client will also stop things from displaying properly in singleplayer... so don't do that?

</details>

<details>
<summary>Config - Rarities (common)</summary>

`rarities.json` is a collection of Pokemon dex numbers used by both the server and client to determine which group(s) a Pokemon belongs to.

The `server.json` config only uses the `starters` list for global alerts via `alertStarters`.

Clients use the entirety of the rarities config, since labels don't exist clientside. As an example, say you love Bidoof so much you think it should be labeled Mythical. If you simply add `399` (it MUST be a dex number - NOT a name), then Bidoof would be alerted **on your client only** as Mythical. Again, this does NOT affect global alerts for servers, with the exception of `starters`.

</details>

<details>
<summary>Custom Alert Messages</summary>

Custom alert messages can utilize dynamic replacement to include info about the Pokemon.
Currently, the available dynamic replacements are:
* **{name} / {name_lower} / {name_upper}**: Inserts the Pokemon's name
* **{legendary} / {legendary_unformatted}**: Inserts the Pokemon's rarity (legendary/mythical/ultra beast/paradox) if `showLegendary` is enabled
* **{shiny} / {shiny_unformatted}**: Inserts a shiny message if the Pokemon is shiny and `alertShiny` is enabled
* **{HA} / {HA_unformatted}**: Inserts a Hidden Ability message if the Pokemon has a Hidden Ability and `alertHiddenAbility` is enabled
* **{bucket} / {bucket_unformatted}**: Inserts the Pokemon's spawn bucket
* **{level} / {level_unformatted}**: Inserts the Pokemon's level
* **{ivs} / {ivs_unformatted}**: Inserts the Pokemon's IVs
* **{evs} / {evs_unformatted}**: Inserts the Pokemon's EV Yield
* **{nature} / {nature_unformatted}**: Inserts the Pokemon's nature
* **{ability} / {ability_unformatted}**: Inserts the Pokemon's ability
* **{gender} / {gender_unformatted}**: Inserts the Pokemon's gender
* **{coords} / {coords_unformatted}**: Inserts the Pokemon's coordinates
* **{biome} / {biome_unformatted}**: Inserts the biome the Pokemon spawned
* **{nearest_player} / {nearest_player_unformatted}**: Inserts the name of the nearest player to the spawned Pokemon
* **{despawned}**: Inserts the Pokemon's despawn message for despawn, captured, fainted, or killed by other causes respectively. This probably shouldn't be used though.

### Examples:<br>
# This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
Creating a custom alert message:<br>
```json
"bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "ability": "HOVER",
        "gender": "DHOVER",
        "coordinates": "DISABLED",
        "biome": "MAIN_MESSAGE",
        "nearestPlayer": "MAIN_MESSAGE"
      },
      "customAlertMessage": "<rainbow>A beautiful <gradient:light_purple:white><b>{shiny_unformatted}</b></gradient><u>{name}</u> spawned in a <u>{biome_unformatted}</u> biome</rainbow><white>{coords}!</white>",
      ...other config stuff
}
```
**Note:** This can also be done by modifying the `fullSpawnMessage` template or the default Pokemon config if you want it to apply to all Pokemon.
![Cool bidoof](https://cdn.modrinth.com/data/cached_images/21ac636baa53001eb530b22ff3b57d1b0b5813d0.png)

</details>

<details>
  <summary>Custom Sounds</summary>

In your `pokemon.json` configs, custom spawn sounds can be added for a bunch of things, like shiny, legendary, or even whenever a Pokemon spawns at all. To get started with adding your own sounds, download the [resource pack template](https://github.com/StainlessStasis/CSA-Pack-Template) and drag it into your resource packs folder (you can open this in the menu in game).<br>

Inside the resource pack, start by adding your sound (**this must be `.ogg`!**) in `assets\minecraft\sounds\cobblemonspawnalerts`. Once you have your sounds added, edit `sounds.json` using whatever text editor -- Notepad++ is a great free option. In the file, you'll find a template for creating your own sounds. When adding a sound, you must begin with the name of the directory where the sounds are located. By default, this will be `cobblemonspawnalerts`. So, if you are adding a sound called `legendary.ogg`, you would add it like so:<br>
```json
"cobblemonspawnalerts.legendary": {
  "sounds": [
    {
    "name": "cobblemonspawnalerts/legendary",
    "attenuation_distance": 0
    }
  ]
}
```
Also, an attenuation distance of 0 is set so that the sound stays a constant volume no matter your distance to the source. For more info, see the [Minecraft wiki page](https://minecraft.wiki/w/Sounds.json).<br>

Next up is adding your sound in the config, which is super simple. Whatever your sound is called in the sounds file -- so `cobblemonspawnalerts.legendary` in this case -- you just put `minecraft:` then the sound. To add this sound for legendary spawns, it would be added like so: `"legendary": "minecraft:cobblemonspawnalerts.legendary",`

<details>
  <summary>Full template using the default sounds provided in the pack</summary>

  # This is outdated as of 1.12.0. If you're reading this, I'm either still working on it, or I forgot to edit this. Please let me know.
  This plays the Pokemon: Legends Arceus shiny sound when any shiny spawns
  ```json
{
  "configVersion": "1.11.3",
  "pokemonConfigs": {
    "default (You can modify anything BELOW this, but dont delete it!)": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "alertHiddenAbility": true,
      "alertDespawned": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "ability": "HOVER",
        "gender": "HOVER",
        "coordinates": "DISABLED",
        "biome": "MAIN_MESSAGE",
        "nearestPlayer": "MAIN_MESSAGE"
      },
      "customAlertMessage": "",
      "sounds": {
        "shiny": "minecraft:cobblemonspawnalerts.your_sound_here",
        "legendary": "",
        "mythical": "",
        "ultrabeast": "",
        "paradox": "",
        "starter": "",
        "unregistered": "",
        "uncaught": "",
        "ivs": "",
        "evs": ""
      },
      "customAlertSound": "",
      "autoGlow": false,
      "journeyMap": {
        "enableWaypoint": true,
        "waypointName": "",
        "waypointHexColor": "",
        "persistent": false
      }
    }
  }
}
```
</details>

Also, you can use sounds from vanilla Minecraft or any other mod! E.g. `minecraft:entity.warden.sonic_boom` or `cobblemon:pokemon.charmander.cry`.<br>
</details>

<details>
  <summary>IV/EV Hunting</summary>

IV and EV hunting can be found in your `main.json` config. To prevent bloating of the Pokemon-specific configs, these apply to all spawns. This may be changed in the future if deemed necessary or is heavily requested.<br>

Let's start with IV hunting, as it is the more complicated of the two:
* **requireAllMinimumsMet**: Requires all of the specified minimum stats to be met for an alert to trigger. For example, say `minHp` and `minAtk` are both set to 20, and a Pokemon spawns with 25 hp but only 10 atk. If this is enabled, then an alert will NOT trigger, since both conditions aren't met. If this is disabled, however, an alert will trigger since at least one of the conditions is met. (basically, if it's true it functions as an AND gate, and false, an OR gate)
* **minPerfectIVs**: Requires at least x perfect IVs to trigger an alert

For EV hunting, there is no equivalent of `requireAllMinimumsMet`. An alert will be triggered if any of the minimum conditions are true. So, if `minHp` and `minAtk` are both set to 1, then any Pokemon with an EV yield of at least 1 hp OR attack will trigger an alert.<br>

Lastly, for both IV and EV hunting, anything set to 0 will be ignored when determining whether to alert. This means you can safely leave any stats you don't care about at 0 and it will not falsely alert.

</details>

<details>
  <summary>Journeymap Integration</summary>

In `pokemon.json`, there are a few options for Journeymap waypoints:
* **enableWaypoint**: Enables waypoints for the config. <br>
* **waypointName**: Sets the name of the waypoint. Default's to the Pokemon's name if left blank.<br>
* **waypointHexColor**: Sets the color of the waypoint. Formatted using hexadecimal (e.g. #FFFFFF for white). You can use an [online tool](https://htmlcolorcodes.com/color-picker/) to help with creating the color code.<br>
* **persistent**: Determines whether the waypoint should persist when leaving/rejoining the world.<br>
</details>

## More to come!
I currently plan to add the following:
* In-game editable config using ForgeConfigAPIPort
* Maybe individual IV hunting?

If you have any other ideas, feel free to share them with me!

## Feedback | Contact me
You can contact me on Discord; my username is `stasis_the_shattered`. You can find me in the [Cobblemon discord](https://discord.com/invite/cobblemon) in #content-zone-help and search for Cobblemon Spawn Alerts. Feel free to ping so I see your message.<br>
*Note: Please do not send a friend request or DM, I will ignore it and redirect you to the Cobblemon server. Only reason I'm choosing to do this now is so future problems can be solved by searching in that channel.*
](https://github.com/StainlessStasis/CobblemonSpawnAlerts)
