# Changelog from February 21, 2026 onwards. See Modrinth for previous versions.

## [1.12.0] (STILL WIP)
### 🚨 BREAKING CHANGES 🚨
Replaced the Adventure library with Ember's Text API - Thanks to @TysonTheEmber!
- CSA now requires [Ember's Text API](https://modrinth.com/mod/embers-text-api) version 2.5.0 or higher
- Breaks existing configs which use MiniMessage formatting. Refer to ETA's [markup guide](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/) for updating your formatting.
- This fixes issues regarding Adventure being JiJ'd or shadowed, conflicting with other mods which also bundle Adventure.
- ETA, while it may slightly differ from MiniMessage, should be just as, if not *more* capable of making cool things.

### New Features
- Added rarity buckets finally! There is a `server.json` config option for global alerts, and same for `main.json` and `message_templates.json`, as per usual. You can list out multiple buckets like so: `"COMMON", "UNCOMMON", "RARE", "ULTRA_RARE"`.
- Added `customAlertTooltip` and `customAlertClickEvent`. Tooltips display a custom message when hovered, using [ETA markup](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/). 
Click events use the following syntax: `event_name:action`. The click events are `open_url, open_file, run_command, suggest_command, change_page, and copy_to_clipboard`. For example, `run_command:/csa openconfig` would open the config folder when clicked on.
- Added sounds for despawns in `pokemon.json`.
- Added `enableAlerts`, `enableDespawnAlerts`, and `enableSounds` in `main.json`.
- Added colorable autoglow using ARGB32 (#AARRGGBB) formatting in `pokemon.json`.
- Added filter by distance in `main.json`.

### Changes & Fixes
- Fixed Adventure crashes and incompatibilities (e.g. BlueMap). See the breaking changes section above.
- Fixed an oversight where the server config was redundantly checked for the status of a Pokemon when sending alert data to clients. E.g. if a shiny legendary spawned, but the server config had shinies disabled, clients would not be alerted that it was a shiny. Because servers may still want to hide the fact that a Pokemon is shiny, `broadcastShiny` has been added as well.
- Fixed despawn alerts triggering for Pokemon your client never actually alerted.
- Fixed despawn alerts falsely triggering when the chunk the Pokemon was in was unloaded, even though it didn't actually despawn. They now properly alert when the entity is fully removed.
