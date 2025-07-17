# Changelog

## [1.10.1] - 2025-07-17 - Quality of Life Update

### ✨ New Features
- **Show Current Settings**: Commands without arguments now display current setting values
  - Example: `/csa config alerts shinies` shows current state without changing it
  - Example: `/csa config ivhunting minhp` shows current minimum HP requirement
  - Makes it easy to check settings before changing them

### 🔧 Improvements
- Enhanced user experience with immediate feedback on current configuration states
- No need to use `/csa config show` for individual setting checks

---

## [1.10.0] - 2025-07-17 - Major Update: In-Game Configuration System

### 🎉 Major New Features

#### Complete In-Game Configuration System
- **New primary command**: `/csa` - Short, easy-to-remember command for all configuration needs
- **Comprehensive command structure**: Configure every setting without editing JSON files
- **Real-time configuration**: Changes apply immediately without requiring restarts
- **Backwards compatibility**: All existing `/cobblemonspawnalerts` commands still work

#### Rich Help System
- `/csa help` - Complete command documentation
- `/csa help alerts` - Alert configuration help  
- `/csa help features` - Feature configuration help
- `/csa help hunting` - IV/EV hunting help
- `/csa help hover` - Hover display help
- `/csa help examples` - Common usage examples

#### Alert Configuration Commands
- `/csa config alerts shinies <true|false>` - Toggle shiny alerts
- `/csa config alerts legendaries <true|false>` - Toggle legendary alerts
- `/csa config alerts mythicals <true|false>` - Toggle mythical alerts
- `/csa config alerts ultrabeasts <true|false>` - Toggle ultra beast alerts
- `/csa config alerts paradox <true|false>` - Toggle paradox alerts
- `/csa config alerts notindex <true|false>` - Toggle not-in-dex alerts
- `/csa config alerts uncaught <true|false>` - Toggle uncaught alerts
- `/csa config alerts everything <true|false>` - Toggle all spawn alerts

#### Feature Configuration Commands
- `/csa config features clickableglow <true|false>` - Toggle clickable glow effects
- `/csa config features autoglow <true|false>` - Toggle automatic glow effects
- `/csa config features debug <true|false>` - Toggle debug output
- `/csa config features multiplayerwarning <true|false>` - Toggle server warnings

#### IV/EV Hunting Configuration Commands
- **IV Hunting**: Complete control over IV hunting parameters
  - `/csa config ivhunting enabled <true|false>` - Enable/disable IV hunting
  - `/csa config ivhunting requireallmins <true|false>` - Require all minimums
  - `/csa config ivhunting minperfect <0-6>` - Set minimum perfect IVs
  - `/csa config ivhunting min[hp|atk|def|spatk|spdef|speed] <0-31>` - Set individual IV minimums

- **EV Hunting**: Configure EV yield hunting
  - `/csa config evhunting enabled <true|false>` - Enable/disable EV hunting
  - `/csa config evhunting min[hp|atk|def|spatk|spdef|speed] <0-3>` - Set EV yield minimums

#### Hover Display Configuration
- `/csa config hover showivs <true|false>` - Toggle IV display in tooltips
- `/csa config hover showevs <true|false>` - Toggle EV display in tooltips

#### Configuration Management Commands
- `/csa config show` - Display current configuration with color-coded output
- `/csa config reset` - Reset all settings to defaults

### 🔧 Technical Improvements

#### Multi-Platform Implementation
- **Fabric Support**: Full command system integration with Fabric's client commands
- **NeoForge Support**: Complete compatibility with NeoForge's command system
- **Shared Logic**: Common configuration management across platforms

#### Enhanced User Experience
- **Color-coded messages**: Green for enabled, red for disabled, blue for info
- **Input validation**: Proper error handling and user feedback
- **Tab completion**: Built-in tab completion for all commands
- **Immediate feedback**: Success/error messages for all operations

#### Configuration System Enhancements
- **Auto-save**: All changes are immediately saved to config files
- **Error handling**: Graceful error handling with descriptive messages
- **Type safety**: Proper validation for boolean and integer values
- **Path-based updates**: Efficient configuration updates using dot notation

### 📝 Common Usage Examples

#### Quick Shiny Hunting Setup
```
/csa config alerts shinies true
/csa config alerts legendaries false
/csa config alerts everything false
```

#### Competitive IV Hunting
```
/csa config ivhunting enabled true
/csa config ivhunting minperfect 4
/csa config ivhunting requireallmins false
```

#### EV Training Helper
```
/csa config evhunting enabled true
/csa config evhunting minatk 2
/csa config evhunting minspeed 1
```

### 🔄 Backwards Compatibility
- All existing configuration files work without modification
- All `/cobblemonspawnalerts` commands continue to function
- No breaking changes to existing functionality
- Seamless upgrade path from v1.x

### 🎯 Quality of Life Improvements
- **No more JSON editing**: Configure everything in-game
- **Instant validation**: Immediate feedback on invalid settings
- **Clear documentation**: Built-in help for all features
- **Intuitive commands**: Logical command structure that's easy to learn

---

## [1.9.10] - 2025-07-17

### Added
- **Clickable Glow Feature**: Pokemon spawn alerts are now clickable to apply a 30-second glow effect using Minecraft's built-in glowing effect
- **Server Mod Detection**: Intelligent warning system that only alerts when server lacks the mod or has outdated version
- **Configurable Debug Output**: Debug messages can now be toggled via config (disabled by default)
- **Glow Success Feedback**: Chat notifications confirm whether glow effects were applied successfully
- **Auto-Glow Option**: Config setting to automatically apply glow effects to spawned Pokemon without clicking

### Technical Changes
- Implemented client-server packet system for glow effects using vanilla `MobEffects.GLOWING`
- Added UUID synchronization between client and server for proper entity targeting
- Extended MainConfig with `enableDebugOutput` and `enableAutoGlow` boolean flags
- Added `GlowEffectManager` for client-side glow coordination
- Added `GlowEffectPacket` for client-server communication
- Added `PacketHandlers` for server-side effect application
- Preserved hover text in clickable messages while adding glow functionality

### Improvements
- Enhanced spawn alert messages with preserved formatting and interactive elements
- Improved multiplayer compatibility with server-authoritative glow effects
- Better user feedback with clear success/failure messages
- Cleaner default experience with optional debug output

---

## Version History Summary (1.8.1 → 1.9.10)
This release focused entirely on implementing the clickable glow feature for Pokemon spawn alerts, evolving from initial client-side attempts to a robust server-side packet system that properly synchronizes glow effects across multiplayer environments while maintaining backward compatibility and user experience quality.
