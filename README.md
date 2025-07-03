# Cobblemon Spawn Alerts
A highly customizable Cobblemon sidemod to alert you when a certain Pokemon spawns

## IMPORTANT:<br>
**Some Pokemon info, like IVs or Nature, may not display properly if you are on a server!** Servers can optionally install this mod to broadcast this info to clients if they desire, also with a config of its own. If a server does not have this mod installed, then things will not display correctly.<br>
Also, while the serverside mod is optional, all clients must have the mod if the server does.

## No more staring at the minimap!
Have you ever been hunting for an ultra-rare, and as you're flying around your eyes are too focused on reading each Pokemon's name that you miss something? Well with this mod, you can simply receive a message in chat when the Pokemon spawns instead! The config is a JSON file that is very easy to edit and add any Pokemon you want.

## Complete the Pokedex!
By simply editing the config, you can be alerted whenever an unregistered or uncaught Pokemon spawns near you!

## Customizability!
Each Pokemon can be individually customized exactly to your needs. If you want to shiny hunt for a Ralts while making sure you don't miss out on any beautiful Bidoofs (i love bidoof), you can do that. Messages use MiniMessage formatting to easily color or format messages however you like (see the [MiniMessage docs](https://docs.advntr.dev/minimessage/format.html)). <br>The default message looks like this:<br>
![Default message](https://cdn.modrinth.com/data/cached_images/4b5500d73cb2c2d1a630cc5c1bee5b220bdb9eb7.png)<br>
But can be modified to look like this, or however you want!
![Custom spawn message](https://cdn.modrinth.com/data/cached_images/71ff33f8e14b2520cc97c897754ce8579037d4b5.png)

## Global Alerts!
Inspired by [Cobblemon Spawn Notification](https://modrinth.com/mod/cobblemon-spawn-notification), all players will be alerted when a rare Pokemon (such as a shiny or legendary) spawns. This can be disabled in the server's `server.json` config for the mod. The server simply sends a packet to all clients, so that each player can individually customize their messages.

## Config - Main (client)
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig`.<br><br>
This file is called `main.json`

### Config Settings:<br>
* **alertAllShinies**: Alerts you when any shiny spawns, unless `alertShiny` is disabled in its config, or its config is disabled.<br>
* **alertAllLegendaries**: Alerts you when any legendary spawns, unless its config is disabled.<br>
* **alertAllMythicals**: Alerts you when any mythical spawns, unless its config is disabled.<br>
* **alertAllUltraBeasts**: Alerts you when any ultra beast spawns, unless its config is disabled.<br>
* **alertAllParadox**: Alerts you when any paradox spawns, unless its config is disabled.<br>
* **alertAllNotInDex**: Alerts you when any Pokemon which is not already registered in your Pokedex spawns, unless its config is disabled.<br>
* **alertAllUncaught**: Alerts you when any Pokemon which you have not caught spawns, unless its config is disabled.<br>

### Reloading the Config:<br>
You can edit the config while the game is running, and simply use the command `/cobblemonspawnalerts reload` to reload it. Leaving/entering a world will **NOT** reload the config. The command must be run to take effect.<br>

## Config - Server
The config is found in your server folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig` in singleplayer.<br><br>
This file is called `server.json`

### Config Settings:<br>
* **alertShinies**: Alerts all players on the server when a shiny spawns.<br>
* **alertLegendaries**: Alerts all players on the server when a legendary spawns.<br>
* **alertMythicals**: Alerts all players on the server when a mythical spawns.<br>
* **alertUltraBeasts**: Alerts all players on the server when a ultra beast spawns.<br>
* **alertParadox**: Alerts all players on the server when a paradox spawns.<br>
* **broadcastIVs**: Tells clients what IVs a spawned Pokemon has.<br>
* **broadcastNature**: Tells clients what Nature a spawned Pokemon has.<br>
**Note:** Disabling these on your client instance will also stop things from displaying properly in singleplayer... so don't do that (unless you want to)

### Reloading the Config:<br>
You can edit the config while the game is running, and simply use the command `/cobblemonspawnalerts-server reload` to reload it. You must have permission level 3 (OP) or higher to use this command.<br>

## Config - Pokemon (client)
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig`.<br><br>

`pokemon.json` is where the bulk of the config is at. By default, the config will come with a default set of options that will be applied to any alert for a Pokemon that is not added to the config. **DO NOT DELETE OR RENAME THIS.** You can freely edit its contents, but leave the name as it is *exactly*. You can copy the formatting and change the name of the Pokemon to add a new spawn message for any other Pokemon.<br>
### Config Parameters:<br>
The name of each Pokemon's config can be formatted like "x, y, z" to include multiple Pokemon. E.g. "charmander, charmeleon, charizard" will alert to the whole Charmander line.
* **enabled**: Enables the spawn message for the Pokemon. If set to false, this setting will override every other config setting for the Pokemon and make its spawn message never display.
* **alwaysAlert**: Whether to always alert the Pokemon's spawn message, assuming enabled is set to true. Setting this to false will only display a spawn message given some other condition is true (e.g. alertShiny).
* **alertShiny**: Whether to alert a shiny Pokemon, or if the Pokemon is shiny. If alwaysAlert is set to false, this will ONLY alert that Pokemon's spawn if it is shiny. If alwaysAlert is set to true, then it will simply specify if the spawned Pokemon is shiny.
* **showLegendary**: Shows whether the Pokemon is legendary, mythical, paradox, or ultra beast.
* **customAlertMessage**: Used to create a custom alert message for a Pokemon using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format.
<br><br>
The following are a sub category of the config for displaying certain info about a Pokemon. These have 3 options -- `"MAIN_MESSAGE"`, `"HOVER"`, and `"DISABLED"`:
* **level**: Shows the Pokemon's level.
* **ivs**: Lists the Pokemon's IVs in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.
* **evs**: Lists the Pokemon's EV Yield in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.
* **nature**: Shows the Pokemon's nature.
* **gender**: Shows the Pokemon's gender.
* **coordinates**: Shows the Pokemon's coordinates (x/y/z).<br>
* **biome**: Shows the biome the Pokemon spawned in.

### Examples: <br>
Show all stats in message:<br>
```json
"bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "MAIN_MESSAGE",
        "evs": "MAIN_MESSAGE",
        "nature": "MAIN_MESSAGE",
        "gender": "MAIN_MESSAGE",
        "coordinates": "MAIN_MESSAGE",
        "biome": "MAIN_MESSAGE"
      },
      "customAlertMessage": ""
},
```
![All stats in message](https://cdn.modrinth.com/data/cached_images/aecca0f8f36fa8718ed82002d02c09784d5f70f2.png)
Show all stats in hover:<br>
```json
"bidoof": {
      "enabled": true,
      "alwaysAlert": true,
      "alertShiny": true,
      "showLegendary": true,
      "statDisplayModes": {
        "level": "HOVER",
        "ivs": "HOVER",
        "evs": "HOVER",
        "nature": "HOVER",
        "gender": "HOVER",
        "coordinates": "HOVER",
        "biome": "HOVER"
      },
      "customAlertMessage": ""
},
```
![Hoverable stats](https://cdn.modrinth.com/data/cached_images/8f4896e3abf83dedadb1510261b60fbdb36f10ed.png)

## Config - Templates (client)
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig`.<br><br>

`message_templates.json` is where you can find and edit the default messages, and message parts. These apply to EVERY spawn, with the exception of `fullSpawnMessage`, which is only used when a custom spawn message is not provided. These templates are what replace the dynamic replacement placeholders.<br>

### Finding the Default Templates:<br>
The default values for the templates are found in your Minecraft instance's language file for the mod. The defaults for en_us.json (the only currently added language) are:<br>
```json
{
  "cobblemon-spawn-alerts.client_config_reloading": "<green>[CobblemonSpawnAlerts] </green><white>Client config reloading...</white>",
  "cobblemon-spawn-alerts.client_config_reloaded": "<green>[CobblemonSpawnAlerts] </green><white>Client config reloaded!</white>",
  "cobblemon-spawn-alerts.client_config_reload_failed": "<green>[CobblemonSpawnAlerts] </green><red>Client config reload failed.</red>",
  "cobblemon-spawn-alerts.config_load_failed": "<green>[CobblemonSpawnAlerts] </green><red>Config failed to load properly while loading `%s`.</red>",
  "cobblemon-spawn-alerts.config_save_failed": "<green>[CobblemonSpawnAlerts] </green><red>Config failed to save properly while saving `%s`.</red>",
  "cobblemon-spawn-alerts.multiplayer_warning": "<green>[CobblemonSpawnAlerts]</green> <yellow>WARNING!</yellow> <white>You are playing on a server. If the server doesn't have the mod installed, or has disabled broadcasting of Pokemon info, certain things, like IVs, EV yield, or Nature, may be displayed incorrectly!",

  "cobblemon-spawn-alerts.default_spawn_message": "<green>A wild {legendary}{shiny}{gender}<white>{name}</white> {level}{ivs}{evs}{nature}has appeared{coords}{biome}!</green>",
  "cobblemon-spawn-alerts.default_despawn_message": "<green>A {legendary}{shiny}<white>{name}</white> {despawned}.",
  "cobblemon-spawn-alerts.despawn_reason_despawned": "despawned",
  "cobblemon-spawn-alerts.despawn_reason_captured": "was captured by %s",
  "cobblemon-spawn-alerts.despawn_reason_fainted": "was defeated by %s",
  "cobblemon-spawn-alerts.shiny": "<gold>Shiny </gold>",
  "cobblemon-spawn-alerts.shiny_unformatted": "Shiny ",
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
  "cobblemon-spawn-alerts.gender": "%s ",
  "cobblemon-spawn-alerts.gender_hover": "Gender: %s",
  "cobblemon-spawn-alerts.gender_unformatted": "%s",
  "cobblemon-spawn-alerts.male": "<aqua>♂ Male</aqua>",
  "cobblemon-spawn-alerts.female": "<light_purple>♀ Female</light_purple>",
  "cobblemon-spawn-alerts.genderless": "<gray>Genderless</gray>",
  "cobblemon-spawn-alerts.coords": " at <gray>(%s, %s, %s)</gray>",
  "cobblemon-spawn-alerts.coords_hover": "Coordinates: <gray>(%s, %s, %s)</gray>",
  "cobblemon-spawn-alerts.coords_unformatted": "(%s, %s, %s)",
  "cobblemon-spawn-alerts.biome": " in a <gray>%s</gray> biome",
  "cobblemon-spawn-alerts.biome_hover": "Biome: <gray>%s</gray>",
  "cobblemon-spawn-alerts.biome_unformatted": "%s",
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
<br>

### Modifying Templates:<br>
When modifying templates, keep in mind the spacing and parameters. `%s` is used internally to insert values, and the amount of them **MUST** match the default template. For example, if you are modifying the template for IVs, your new template must have EXACTLY 6 `%s` - no more, no less. These also insert values in order, so unfortunately changing the order of IVs or coordinates is currently impossible.<br>**Note:** Some of the default templates have spaces in them to make formatting work properly, so just be aware of that if you are modifying a template.<br>
### Examples:<br>
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

## Custom Alert Messages
Custom alert messages can utilize dynamic replacement to include info about the Pokemon.
Currently, the available dynamic replacements are:
* **{name} / {name_lower} / {name_upper}**: Inserts the Pokemon's name
* **{legendary} / {legendary_unformatted}**: Inserts the Pokemon's rarity (legendary/mythical/ultra beast) if `showLegendary` is enabled
* **{shiny} / {shiny_unformatted}**: Inserts a shiny message if the Pokemon is shiny and `alertShiny` is enabled
* **{level} / {level_unformatted}**: Inserts the Pokemon's level if `showLevel` is enabled
* **{ivs} / {ivs_unformatted}**: Inserts the Pokemon's IVs if `showIVs` is enabled
* **{nature / {nature_unformatted}**: Inserts the Pokemon's nature if `showNature` is enabled
* **{gender} / {gender_unformatted}**: Inserts the Pokemon's gender if `showGender` is enabled
* **{coords} / {coords_unformatted}**: Inserts the Pokemon's coordinates if `showCoordinates` is enabled
* **{biome} / {biome_unformatted}**: Inserts the biome the Pokemon spawned in if `showBiome` is enabled
* **{despawn}**: Inserts the Pokemon's despawn message for despawn, captured, and fainted respectively

### Examples:<br>
Creating a custom alert message:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLegendary": true,
    "statDisplayModes": {
        "level": "MAIN_MESSAGE",
        "ivs": "DISABLED",
        "evs": "DISABLED",
        "nature": "DISABLED",
        "gender": "DISABLED",
        "coordinates": "MAIN_MESSAGE",
        "biome": "DISABLED"
      },
    "customAlertMessage": "<blue>My beautiful boy {shiny}<gradient:blue:green><b>BIDOOF</b></gradient> {level}spawned{coords}!</blue>"
},
```
**Note:** This can also be done by modifying the `fullSpawnMessage` template or the default Pokemon config if you want it to apply to all Pokemon.
![Custom spawn message](https://cdn.modrinth.com/data/cached_images/71ff33f8e14b2520cc97c897754ce8579037d4b5.png)

## More to come!
My current plans, in no particular order, are to add the following:
* Sounds (via resource packs)
* Abilities/ability hunting
* IV/EV hunting

I may also add the following, but I'm unsure:
* Alert to specific type or egg group? Idk seems unnecessary

If you have any other ideas, feel free to share them with me!

## Feedback | Contact me
You can contact me on Discord; my username is `stasis_the_shattered`. You can find me in the Cobblemon discord in #content-zone_help and search for Cobblemon Spawn Alerts. Or, if you'd like, message me directly. I'm looking to make this mod as polished as I can, so hit me up with any bugs or suggestions.
