# Changelog from February 21, 2026 onwards. See Modrinth for previous versions.

## [1.12.0]
### 🚨 BREAKING CHANGES 🚨
Replaced the Adventure library with Ember's Text API - Thanks to @TysonTheEmber!
- CSA now requires [Ember's Text API](https://modrinth.com/mod/embers-text-api) version 2.5.0 or higher.
- Breaks existing configs which use MiniMessage formatting. Refer to ETA's [markup guide](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/) for updating your formatting.
- Required dependency on clients, and Fabric servers. NeoForge dedicated servers do not depend on ETA.
- This fixes issues regarding Adventure being JiJ'd or shadowed, conflicting with other mods which also bundle Adventure.
- This also significantly decreases the jar file size.
- ETA, while it may slightly differ from MiniMessage, should be just as, if not *more* capable of making cool things.

### New Features
- Added rarity buckets finally! There is a `server.json` config option for global alerts, and same for `main.json` and `message_templates.json`, as per usual. You can list out multiple buckets like so: `"COMMON", "UNCOMMON", "RARE", "ULTRA_RARE"`.
- Added `customAlertTooltip` and `customAlertClickEvent`. Tooltips display a custom message when hovered, using [ETA markup](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/). 
Click events use the following syntax: `event_name:action`. The click events are `open_url, open_file, run_command, suggest_command, change_page, and copy_to_clipboard`. For example, `run_command:/csa openconfig` would open the config folder when clicked on.
- Added sounds for despawns in `pokemon.json`.
- Added `enableAlerts`, `enableDespawnAlerts`, and `enableSounds` in `main.json`.
- Added colorable autoglow using ARGB32 (#AARRGGBB) formatting in `pokemon.json`.
- Added filter by distance in `main.json`.
- Added config to modify what is classified as legendary, starter, etc. On the server side, this config is only used for starter global alerts. It is primarily used on the client side. However, since it is still common between server and client, it is reloaded via `/csa-common reload`.

### Changes & Fixes
- Fixed Adventure crashes and incompatibilities (e.g. BlueMap). See the breaking changes section above.
- Renamed `/csa-server` command to `/csa-common`.
- Fixed an oversight where the server config was redundantly checked for the status of a Pokemon when sending alert data to clients. E.g. if a shiny legendary spawned, but the server config had shinies disabled, clients would not be alerted that it was a shiny. Because servers may still want to hide the fact that a Pokemon is shiny, `broadcastShiny` has been added as well.
- Fixed despawn alerts triggering for Pokemon your client never actually alerted.
- Fixed despawn alerts falsely triggering when the chunk the Pokemon was in was unloaded, even though it didn't actually despawn. They now properly alert when the entity is fully removed.

## [1.12.1]
Fixed NeoForge startup crash due to accidental hard dependency on Journeymap

## [1.12.2]
### New Features
- Added `broadcastBucket` to `server.json`

### Changes & Fixes
- Fixed PokemonDataPacket networking error due to null buckets. Added error handling and logging to it.
- Fixed stupid idiot error where I mistyped ultra-rare as ultra_rare and so ultra rares wouldn't alert :)

## [1.12.3]
### New Features
- Added `{x}`, `{y}`, and `{z}` dynamic replacements for coordinates.
- Added version checker to notify players of important changes. You may disable this in `main.json`. 
However, it is highly recommended to leave this setting on. The message will go away after restarting your game, assuming the version stays consistent.

### Changes & Fixes
- Changed DespawnDataPacket to contain the entire AlertDataPacket for the Pokemon, giving access to much more information in despawn messages.
- Made coordinates display as "N/A" when unavilable, instead of just 0.
- Added configVersion to message_templates, server, and rarities configs

## [1.13.0]
### 🚨 BREAKING CHANGES 🚨
NeoForge dedicated servers now require Ember's Text API version 2.5.0+. Fabric already required it, so nothing changes there.

Added regex pattern matcher to remove anything within {curly braces} after dynamic replacements are applied.
- This means that anything you put within {curly braces} will get replaced with an empty String.
- Necessary change to clean up code a bit.
- Allows for a more modular system to potentially support custom DRs in the future. Also makes adding new ones much faster and easier to manage.

### New Features
- Added Discord webhooks! See `webhooks.json`, `server_message_templates.json`, `pokemon.json`, and `server.json` for relevant info. Webhooks can be enabled by your client (any alert) or by the server (global alerts).
- Added `BOTH` option for stat display modes. Displays the stat in both the main message and hover.
- Added `{dex}`, `{dex_hover}`, and `{dex_unformatted}`. Tip: use `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{dex}.png` to embed a Pokemon's image in your webhooks!
- Added `{timestamp}`

### Changes & Fixes
- Journeymap waypoint names now support dynamic replacements. Recommended to use the `unformatted` versions when available. 
- Discord webhooks required shadowing https://github.com/n1netails/n1netails-discord-webhook-client and its dependencies. As such, the mod's file size has increased to about ~2.8MB, up from ~0.5MB.

## [1.13.1]
### New Features
- Added proper webhook example to default `webhooks.json` config.

### Changes & Fixes
- Fixed `{nearest_player}` not working as a side effect of 1.13.0.
- Fixed `{shiny}` and `{HA}` not working for serverside webhooks.
- Fixed `{bucket}` incorrect spacing in lang file.
- Renamed `nearestPlayer` to `nearest_player` and `coordinates` to `coords` in `pokemon.json`. Automatically fixes itself.
