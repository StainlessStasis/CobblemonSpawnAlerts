# Cobblemon Spawn Alerts
A highly customizable Cobblemon sidemod to alert you when a certain Pokemon spawns

## This mod is no longer being maintained.
No updates. No patches. For more details, see the bottom of the page. Thanks for everything.

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

## Discord Webhooks!
Both the server *and* clients can send Discord webhooks, fully customizable with embeds and everything. You can even embed an image of the spawned Pokemon! (Using `"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{dex}.png"`)

Servers can enable webhooks for global alerts, while clients can enable webhooks for every single alert they receive.

![discord webhook integration](https://cdn.modrinth.com/data/cached_images/2e0bb5a7223ae56cf9913b79fcbffeff5fd40e7f_0.webp)

## FAQ:
### How do I do x? How does y work?
Please [read the docs](https://stainlessstasis.github.io/CSA-Docs/config) before you go on asking me how the configs work. If you still can't figure it out that's fine, just at least *attempt* it on your own first.
### My alerts don't work when using commands?
This is intended behavior, which I still need to find a better solution for (if one exists). You *can* use `enableSpawnCommandAlerts` in `server.json`, but **use this at your own risk**. This can break spawning of some Pokemon, such as in Cobblemon: Path to Legends.<br>
*Technical details: This mixins to SpawnPokemon#execute and uses the player spawner system to forcibly post a spawn event.*
### Support for Xaero's Minimap waypoints?
See the bottom of the page.
### When will you...
See the bottom of the page.

## Known Issues:
- Some modpacks make changes to the default configs, causing some things to not work by default. If CSA was part of the modpack and some alerts (e.g. shinies) arent' working, try this:<br>
1) /csa openconfig - Backup these files if you have anything important<br>
2) Delete main.json, pokemon.json, and message_templates.json on your client, and/or server.json if you're in singleplayer or are hosting a server<br>
3) /csa reload - This creates new config files with default settings
- Cobblemon Academy 2.0 uses a resource pack which entirely overrides CSA's lang file, breaking the new formatting in 1.12.0. You have the option to either edit the `message_templates.json`, or, if you want to use the default lang file of CSA, go to `resourcepacks -> Academy -> assets` and delete the `cobblemon_spawn_alerts` directory entirely.

## Config:
To get started with editing the config, [read the docs]([https://stainlessstasis.github.io/CSA-Docs/](https://stainlessstasis.github.io/CSA-Docs/config)).

## Support
I'll still help people even though I no longer will be maintaining the mod. For support, go to the [Cobblemon discord](https://discord.com/invite/cobblemon), then in #content-zone-help, search for Cobblemon Spawn Alerts.

## Why I'm not maintaining the mod anymore, and notes for other developers
#### Why?
1) I don't have the time, energy, motivation, or passion for this project anymore. I have other things I would much rather spend my time on, to be completely honest.
2) The mod was never supposed to be this big. I made it for my own use, and it grew to something much larger than I was capable of handling alone as someone who's still relatively new to modding.
3) The codebase is a complete mess. To add new features or fix other things would require massive refactors. Honestly, the entire mod needs to be rewritten with things like addons or the new Cobblemon systems in mind.
#### For developers:
This mod and the docs site are licensed as MIT. You may freely modify and/or redistribute any of the code. Feel free to use any of the same ideas or concepts. Do whatever you want. You don't have to credit me for anything.

I will NOT merge PRs or resolve issues.
