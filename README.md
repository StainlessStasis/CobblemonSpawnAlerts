# Cobblemon Spawn Alerts
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
![default message](https://cdn.modrinth.com/data/cached_images/26f2de805c42becb0b858f4eec2069e7ad447405.png)<br>
But can be modified to look like this, or however you want!
![cool bidoof](https://cdn.modrinth.com/data/cached_images/7705971fe44c8dc5568b10c7fe67584bd9be37a4.png)

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
- Cobblemon Academy 2.0 uses a resource pack which entirely overrides CSA's lang file, breaking the new formatting in 1.12.0. You have the option to either edit the `message_templates.json`, or, if you want to use the default lang file of CSA, go to `resourcepacks -> Academy -> assets` and delete the `cobblemon_spawn_alerts` directory entirely.
- Cobblemon Academy 2.0 makes some changes to the configs, causing shinies not to alert by default. In the CSA `main.json` config (use `/csa openconfig` to open the directory), set `alertAllShinies` to true. Then run `/csa reload`. Now, if you are the owner of the server, go  to `server.json` and set `alertShinies` to true. Then run `/csa-server reload` (or `/csa-common reload` on 1.12.0+). Shiny alerts should work now.

## Config:
To get started with editing the config, [read the docs](https://stainlessstasis.github.io/CSA-Docs/).

## TODO:
* In-game editable config
* More dynamic replacements, possibly conditionals
* Spawn history/AFK logging mode
* Alert simulation for testing
* Discord server
* And more...

If you have any other ideas, feel free to share them with me!

## Feedback | Contact me

If you wish to provide in-depth feedback, I've set up a [Google forms survey](https://docs.google.com/forms/d/e/1FAIpQLSek3U1Df_Ycwb_h5R1DPQKedsTzK9eTD2jPayqD3zw7oAJXkg/viewform).

For direct feedback or support, you can find me in the [Cobblemon discord](https://discord.com/invite/cobblemon) in #content-zone-help and search for Cobblemon Spawn Alerts. My username is `stasis_the_shattered`. Feel free to ping so I see your message.<br>
*Note: Please do not send a friend request or DM, I will ignore it. Only reason I'm choosing to do this now is so future problems can be solved by searching in that channel.*
