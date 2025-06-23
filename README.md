# CobblemonSpawnAlerts
A highly configurable, purely clientside mod to alert you when a certain Pokemon spawns

## No more staring at the minimap!
Have you ever been hunting for an ultra-rare, and as you're flying around your eyes are too focused on reading each Pokemon's name that you miss something? Well with this mod, you can simply receive a message in chat when the Pokemon spawns instead! The config is a JSON file that is very easy to edit and add any Pokemon you want.

## Customizability!
Each Pokemon can be individually customized exactly to your needs. If you want to shiny hunt for a Ralts while making sure you don't miss out on any beautiful Bidoofs (i love bidoof), you can do that. Messages use MiniMessage formatting to easily color or format messages however you like (see the [MiniMessage docs](https://docs.advntr.dev/minimessage/format.html)). <br>The default message looks like this:<br>
![more bidoof](https://cdn.modrinth.com/data/cached_images/c155665d7d3160c832bd1efeb08d4da50b54be92.png)

## Works with exiting spawn notifications mod!
The mod is fully clientside so it does not at all affect the [Cobblemon Spawn Notification](https://modrinth.com/mod/cobblemon-spawn-notification) mod by [tmetcalfe89](https://modrinth.com/user/tmetcalfe89). Cobblemon Spawn Notification is a great mod, so if you somehow don't know about it already I highly recommend using it on your server! It is also partly what inspired this mod.

## Config - Main
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`.<br>
`main.json` is where the bulk of the config is at. By default, Bidoof and Arceus will be included (but not enabled) to provide a foundation for making your own config. You can copy the formatting and change the name of the Pokemon to add a new spawn message for any other Pokemon.<br>
### Config Parameters:<br>
**enabled**: Enables the spawn message for the Pokemon. If set to false, this setting will override every other config setting for the Pokemon and make its spawn message never display.<br>
**alwaysAlert**: Whether to always alert the Pokemon's spawn message, assuming enabled is set to true. Setting this to false will only display a spawn message given some other condition is true (e.g. alertShiny).<br>
**alertShiny**: Whether to alert a shiny Pokemon, or if the Pokemon is shiny. If alwaysAlert is set to false, this will ONLY alert that Pokemon's spawn if it is shiny. If alwaysAlert is set to true, then it will simply specify if the spawned Pokemon is shiny.<br>
**showLevel**: Shows the Pokemon's level.<br>
**showIVs**: Lists the Pokemon's IVs in order of HP/Atk/Def/Sp.Atk/Sp.Def/Speed.<br>
**showNature**: Shows the Pokemon's nature.<br>
**showCoordinates**: Shows the Pokemon's coordinates (x/y/z).<br>
**showInfoAsHover**: Shows certain info only when the message is hovered over instead of in the main message. This includes: level, IVs, nature, and coordinates. These are not currently individually configurable to be hovered, so it either shows all the enabled stats in the hover, or all in the main message. I will be adding this feature soon though!.<br>
**customAlertMessage**: Used to create a custom alert message for a Pokemon using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format.<br>

### Reloading the Config:<br>
You can edit the config while the game is running, and simply use the command `/cobblemonspawnalerts reloadconfig` to reload it. Leaving/entering a world will **NOT** reload the config. The command must be run to take effect.<br>

## Config - Templates
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`.<br><br>
`message_templates.json` is where you can find and edit the default messages, and message parts. These apply to EVERY spawn, with the exception of `fullSpawnMessage`, which is only used when a custom spawn message is not provided. These templates are what replace the dynamic replacement placeholders.<br>

### Finding the Default Templates:<br>
The default values for the templates are found in your Minecraft instance's language file. The defaults for en_us.json (the only currently added language) are:<br>
```json
{
  "cobblemon-spawn-alerts.config_reloading": "<green>[CobblemonSpawnAlerts] </green><white>Config reloading...</white>",
  "cobblemon-spawn-alerts.config_reloaded": "<green>[CobblemonSpawnAlerts] </green><white>Config reloaded!</white>",
  "cobblemon-spawn-alerts.default_spawn_message": "<green>A wild {shiny}<white>{name}</white> {level}{ivs}{nature}has appeared{coords}!</green>",
  "cobblemon-spawn-alerts.shiny": "<gold>Shiny </gold>",
  "cobblemon-spawn-alerts.level": "<gray>(Lvl. %s) </gray>",
  "cobblemon-spawn-alerts.level_hover": "Level: <gray>%s</gray>",
  "cobblemon-spawn-alerts.ivs": "with IVs: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.ivs_hover": "IVs: <gray>(%s/%s/%s/%s/%s/%s)</gray> ",
  "cobblemon-spawn-alerts.nature": "with Nature: %s ",
  "cobblemon-spawn-alerts.nature_hover": "Nature: <gray>%s</gray> ",
  "cobblemon-spawn-alerts.coords": " at <gray>(%s, %s, %s)</gray>",
  "cobblemon-spawn-alerts.coords_hover": "Coordinates: <gray>(%s, %s, %s)</gray>"
}
```
<br>

### Modifying Templates:<br>
When modifying templates, keep in mind the spacing and parameters. `%s` is used to insert values, and the amount of them **MUST** match the default template. For example, if you are modifying the template for IVs, your new template must have EXACTLY 6 `%s` - no more, no less. These also insert values in order, so unfortunately changing the order of IVs or coordinates is currently impossible.<br>
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
* **{name}**: Inserts the Pokemon's name
* **{shiny}**: Inserts a shiny message if the Pokemon is shiny and `alertShiny` is enabled
* **{level}**: Inserts the Pokemon's level
* **{ivs}**: Inserts the Pokemon's IVs
* **{nature}**: Inserts the Pokemon's nature
* **{coords}**: Inserts the Pokemon's coordinates

<br>**Note:** Some of the default ones have spaces in them to make formatting work properly, so just be aware of that if you are modifying its template in `message_templates.json`.<br><br>
### Examples:<br>
Show all stats in message:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLevel": true,
    "showIVs": true,
    "showNature": true,
    "showCoordinates": true,
    "showInfoAsHover": false,
    "customAlertMessage": ""
  },
```
![Stats in the main message](https://cdn.modrinth.com/data/cached_images/820a65412160b51140b426453f71fbd4230ea577.png)
Show all stats in hover:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLevel": true,
    "showIVs": true,
    "showNature": true,
    "showCoordinates": true,
    "showInfoAsHover": true,
    "customAlertMessage": ""
  },
```
![Hoverable stats](https://cdn.modrinth.com/data/cached_images/19d913c455e3b368f38b7a56673e24617a454bb0.png)
Creating a custom spawn message:<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "showLevel": true,
    "showIVs": true,
    "showNature": true,
    "showCoordinates": true,
    "showInfoAsHover": false,
    "customAlertMessage": "<blue>My beautiful boy {shiny}<gradient:blue:green><b>BIDOOF</b></gradient> {level}spawned{coords}!</blue>"
  },
```
**Note:** This can also be done by modifying the `fullSpawnMessage` template if you want it to apply to all Pokemon.
![Custom spawn message](https://cdn.modrinth.com/data/cached_images/71ff33f8e14b2520cc97c897754ce8579037d4b5.png)

## More to come!
I just started making this mod, so it is quite obviously lacking many features. In no particular order, I 100% plan to add:
* Special customizable messages for legendaries/mythicals/ultra beasts repectively
* Individually customizable hoverable stats
* Separate the stats from the formatting in dynamic replacements. What I mean is instead of {level} formatting it with the text and everything, it would just provide the level number. The formatted version would be {level_formatted}.
* Sounds

I may also add the following, but I'm unsure:
* Xaero's map integration? I feel like coordinates is kind of enough tho
* Customize config in game via command (i do not feel like making a GUI but if its really requested... i could)
* Gender, for Pokemon like Ralts
* Support for entire evolution line (can very easily just be copy pasted as a workaround so is it really worth the effort?)
* Alert to specific type, egg group, or stuff like good IVs? No idea how difficult that would be for egg group

If you have any other ideas, feel free to share them with me!

## Feedback | Contact me
You can contact me on Discord; my username is `stasis_the_shattered`. I don't have a server of my own or anything, so ping me in the Cobblemon server or just DM me. I'm looking to make this mod as polished as I can, so hit me up with any bugs or suggestions.
