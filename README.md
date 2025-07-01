# Cobblemon Spawn Alerts
A highly customizable Cobblemon sidemod to alert you when a certain Pokemon spawns

## IMPORTANT:<br>
**Some Pokemon info, like IVs or Nature, may not display properly if you are on a server!** Servers can optionally install this mod to broadcast this info to clients if they desire, also with a config of its own. If a server does not have this mod installed, then things will not display correctly.

## No more staring at the minimap!
Have you ever been hunting for an ultra-rare, and as you're flying around your eyes are too focused on reading each Pokemon's name that you miss something? Well with this mod, you can simply receive a message in chat when the Pokemon spawns instead! The config is a JSON file that is very easy to edit and add any Pokemon you want.

## Complete the Pokedex!
By simply editing the config, you can be alerted whenever an unregistered or uncaught Pokemon spawns near you!

## Customizability!
Each Pokemon can be individually customized exactly to your needs. If you want to shiny hunt for a Ralts while making sure you don't miss out on any beautiful Bidoofs (i love bidoof), you can do that. Messages use MiniMessage formatting to easily color or format messages however you like (see the [MiniMessage docs](https://docs.advntr.dev/minimessage/format.html)). <br>The default message looks like this:<br>
![Default message](https://cdn.modrinth.com/data/cached_images/4b5500d73cb2c2d1a630cc5c1bee5b220bdb9eb7.png)<br>
But can be modified to look like this, or however you want!
![Custom spawn message](https://cdn.modrinth.com/data/cached_images/71ff33f8e14b2520cc97c897754ce8579037d4b5.png)

## Works with exiting spawn notifications mod!
The mod does not at all affect the [Cobblemon Spawn Notification](https://modrinth.com/mod/cobblemon-spawn-notification) mod by [tmetcalfe89](https://modrinth.com/user/tmetcalfe89). Cobblemon Spawn Notification is a great mod, so if you somehow don't know about it already I highly recommend using it on your server! It is also partly what inspired this mod.

## Config - Main (client)
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig`.<br><br>
This file is called `main.json`

### Config Settings:<br>
**alertAllShinies**: Alerts you when any shiny spawns, unless `alertShiny` is disabled in its config, or its config is disabled.<br>
**alertAllLegendaries**: Alerts you when any legendary spawns, unless its config is disabled.<br>
**alertAllMythicals**: Alerts you when any mythical spawns, unless its config is disabled.<br>
**alertAllUltraBeasts**: Alerts you when any ultra beast spawns, unless its config is disabled.<br>
**alertAllParadox**: Alerts you when any paradox spawns, unless its config is disabled.<br>
**alertAllNotInDex**: Alerts you when any Pokemon which is not already registered in your Pokedex spawns, unless its config is disabled.<br>
**alertAllUncaught**: Alerts you when any Pokemon which you have not caught spawns, unless its config is disabled.<br>

### Reloading the Config:<br>
You can edit the config while the game is running, and simply use the command `/cobblemonspawnalerts reload` to reload it. Leaving/entering a world will **NOT** reload the config. The command must be run to take effect.<br>

## Config - Server
The config is found in your server folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig` in singleplayer.<br><br>
This file is called `server.json`

### Config Settings:<br>
**broadcastIVs**: Tells clients what IVs a spawned Pokemon has.<br>
**broadcastNature**: Tells clients what Nature a spawned Pokemon has.<br>
**Note:** Disabling these on your client instance will also stop things from displaying properly in singleplayer... so don't do that (unless you want to)

### Reloading the Config:<br>
You can edit the config while the game is running, and simply use the command `/cobblemonspawnalerts-server reload` to reload it. You must have permission level 3 (OP) or higher to use this command.<br>

## Config - Pokemon (client)
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`. You can also use the command `/cobblemonspawnalerts openconfig`.<br><br>

`pokemon.json` is where the bulk of the config is at. By default, the config will come with a default set of options that will be applied to any alert for a Pokemon that is not added to the config. **DO NOT DELETE OR RENAME THIS.** You can freely edit its contents, but leave the name as it is *exactly*. Also, Bidoof will be included (but not enabled) to provide a foundation for making your own config. You can copy the formatting and change the name of the Pokemon to add a new spawn message for any other Pokemon.<br>
### Config Parameters:<br>
**enabled**: Enables the spawn message for the Pokemon. If set to false, this setting will override every other config setting for the Pokemon and make its spawn message never display.<br>
**alwaysAlert**: Whether to always alert the Pokemon's spawn message, assuming enabled is set to true. Setting this to false will only display a spawn message given some other condition is true (e.g. alertShiny).<br>
**alertShiny**: Whether to alert a shiny Pokemon, or if the Pokemon is shiny. If alwaysAlert is set to false, this will ONLY alert that Pokemon's spawn if it is shiny. If alwaysAlert is set to true, then it will simply specify if the spawned Pokemon is shiny.<br>
**showLegendary**: Shows whether the Pokemon is legendary, mythical, paradox, or ultra beast.<br>
**showLevel**: Shows the Pokemon's level.<br>
**showIVs**: (Currently only works in singleplayer) Lists the Pokemon's IVs in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.<br>
**showNature**: (Currently only works in singleplayer) Shows the Pokemon's nature.<br>
**showGender**: Shows the Pokemon's gender.<br>
**showCoordinates**: Shows the Pokemon's coordinates (x/y/z).<br>
**showBiome**: Shows the biome the Pokemon spawned in.<br>
**showInfoAsHover**: Shows certain info only when the message is hovered over instead of in the main message. This includes: level, IVs, nature, and coordinates. These are not currently individually configurable to be hovered, so it either shows all the enabled stats in the hover, or all in the main message. I will be adding this feature soon though!<br>
**customAlertMessage**: Used to create a custom alert message for a Pokemon using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format.<br>

### Examples: <br>
Show all stats in message:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLegendary": true,
    "showLevel": true,
    "showIVs": true,
    "showNature": true,
    "showGender": true,
    "showCoordinates": true,
    "showBiome": true,
    "showInfoAsHover": false,
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
    "showLevel": true,
    "showIVs": true,
    "showNature": true,
    "showGender": true,
    "showCoordinates": true,
    "showBiome": true,
    "showInfoAsHover": true,
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

  "cobblemon-spawn-alerts.default_spawn_message": "<green>A wild {legendary}{shiny}{gender}<white>{name}</white> {level}{ivs}{nature}has appeared{coords}{biome}!</green>",
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
* **{shiny} / {shiny_unformatted}**: Inserts a shiny message if the Pokemon is shiny and `alertShiny` is enabled
* **{level} / {level_unformatted}**: Inserts the Pokemon's level if `showLevel` is enabled
* **{ivs} / {ivs_unformatted}**: Inserts the Pokemon's IVs if `showIVs` is enabled
* **{nature / {nature_unformatted}**: Inserts the Pokemon's nature if `showNature` is enabled
* **{gender} / {gender_unformatted}**: Inserts the Pokemon's gender if `showGender` is enabled
* **{coords} / {coords_unformatted}**: Inserts the Pokemon's coordinates if `showCoordinates` is enabled
* **{biome} / {biome_unformatted}**: Inserts the biome the Pokemon spawned in if `showBiome` is enabled
* **{legendary} / {legendary_unformatted}**: Inserts the Pokemon's rarity (legendary/mythical/ultra beast) if `showLegendary` is enabled
<br><br>
### Examples:<br>
Creating a custom alert message:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLegendary": true,
    "showLevel": true,
    "showIVs": false,
    "showNature": false,
    "showGender": false,
    "showCoordinates": true,
    "showBiome": false,
    "showInfoAsHover": false,
    "customAlertMessage": "<blue>My beautiful boy {shiny}<gradient:blue:green><b>BIDOOF</b></gradient> {level}spawned{coords}!</blue>"
  },
```
**Note:** This can also be done by modifying the `fullSpawnMessage` template if you want it to apply to all Pokemon.
![Custom spawn message](https://cdn.modrinth.com/data/cached_images/71ff33f8e14b2520cc97c897754ce8579037d4b5.png)

## More to come!
I just started making this mod, so it is quite obviously lacking many features. In no particular order, I plan to add:
* Serverside global alerts & alert for despawn/faint
* Sounds
* Abilities
* EVs
* Individually customizable hoverable stats

I may also add the following, but I'm unsure:
* Xaero's map integration? I feel like coordinates is kind of enough tho
* Customize config in game via command (i do not feel like making a GUI but if its really requested... i could)
* Support for entire evolution line (can very easily just be copy pasted as a workaround so is it really worth the effort?)
* Alert to specific type, egg group, or stuff like good IVs? No idea how difficult that would be for egg group

If you have any other ideas, feel free to share them with me!

## Feedback | Contact me
You can contact me on Discord; my username is `stasis_the_shattered`. You can find me in the Cobblemon discord in #content-zone_help and search for Cobblemon Spawn Alerts. Or, if you'd like, message me directly. I'm looking to make this mod as polished as I can, so hit me up with any bugs or suggestions.
