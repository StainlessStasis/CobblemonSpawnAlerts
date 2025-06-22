# CobblemonSpawnAlerts
A highly configurable, purely clientside mod to alert you when a certain Pokemon spawns

## No more staring at the minimap!
Have you ever been hunting for an ultra-rare, and as you're flying around your eyes are too focused on reading each Pokemon's name that you miss something? Well with this mod, you can simply receive a message in chat when the Pokemon spawns instead! The config is a JSON file that is very easy to edit and add any Pokemon you want.

## Customizability!
Each Pokemon can be individually customized exactly to your needs. If you want to shiny hunt for a Ralts while making sure you don't miss out on any beautiful Bidoofs (i love bidoof), you can do that. Messages use MiniMessage formatting to easily color or format messages however you like (see the [MiniMessage docs](https://docs.advntr.dev/minimessage/format.html)). 

## Config
The config is found in your Minecraft instance folder under `config -> cobblemon-spawn-alerts`.<br><br>
`default_spawn_message.txt` is, as the name suggests, the default spawn message for when a Pokemon spawns. By default it is set to `cobblemon-spawn-alerts.default_spawn_message`.<br><br>
`main.json` is where the bulk of the config is at. By default, Arceus will be enabled for testing purposes. Feel free to remove or modify it. You can copy the Arceus formatting and change the name of the Pokemon to add a new spawn message for any other Pokemon.<br><br>
**Config Parameters:**<br>
*enabled*: Enables the spawn message for the Pokemon. If set to false, this setting will override every other config setting for the Pokemon and make its spawn message never display.<br>
*alwaysAlert*: Whether to always alert the Pokemon's spawn message, assuming enabled is set to true. Setting this to false will only display a spawn message given some other condition is true (e.g. alertShiny).<br>
*alertShiny*: Whether to alert a shiny Pokemon, or if the Pokemon is shiny. If alwaysAlert is set to false, this will ONLY alert that Pokemon's spawn if it is shiny. If alwaysAlert is set to true, then it will simply specify if the spawned Pokemon is shiny.<br>
*customAlertMessage*: Used to create a custom alert message for a Pokemon using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format.

## Custom Alert Messages: Dynamic Replacement
Custom alert messages currently only have the option to display whether a Pokemon is shiny or not (more to come soon!).<br>
To include the shiny message in your custom one, add `{shiny}` EXACTLY like that. This will replace `{shiny}` with nothing if it isn't shiny, or `<gold>Shiny </gold>` if it is shiny.<br>
**Example:**<br>
```json
"bidoof": {
    "enabled": true,
    "alwaysAlert": true,
    "alertShiny": true,
    "customAlertMessage": "<white>A</white> {shiny}<rainbow>Bidoof <green>spawned!"
  }
```
