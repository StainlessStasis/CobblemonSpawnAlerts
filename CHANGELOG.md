# Changelog from February 21, 2026 onwards. See Modrinth for previous versions.

## [1.12.0] (STILL WIP)
### 🚨 BREAKING CHANGES 🚨
Replaced the Adventure library with Ember's Text API - Thanks to @TysonTheEmber!
- Breaks existing configs which use MiniMessage formatting. Refer to ETA's [markup guide](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/) for updating your formatting.
- This fixes issues regarding Adventure being JiJ'd or shadowed, conflicting with other mods which also bundle Adventure.
- ETA, while it may slightly differ from MiniMessage, should be just as, if not *more* capable of making cool things. 

### New Features
- Added `customAlertTooltip` and `customAlertClickEvent`. Tooltips display a custom message when hovered, using [ETA markup](https://tysontheember.dev/embers-text-api/for-modpack-creators/markup-guide/). 
Click events use the following syntax: `event_name:action`. The click events are `open_url, open_file, run_command, suggest_command, change_page, and copy_to_clipboard`. For example, `run_command:/csa openconfig` would open the config folder when clicked on.

### Changes & Fixes
- Fixed Adventure crashes and incompatibilities (e.g. BlueMap). See the breaking changes section above.
