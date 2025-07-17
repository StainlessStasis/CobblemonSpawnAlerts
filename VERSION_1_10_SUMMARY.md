# CobblemonSpawnAlerts v1.10 - In-Game Configuration System

## New Features

### Complete In-Game Configuration
Version 1.10 introduces a comprehensive command system that allows players to modify all configuration settings without needing to edit JSON files manually.

### New Command Structure
- **Primary command**: `/csa` (short and easy to type)
- **Backwards compatibility**: All existing `/cobblemonspawnalerts` commands still work
- **Comprehensive help system**: Built-in help commands for all features

## Command Categories

### 1. Help Commands
- `/csa` or `/csa help` - Show main help
- `/csa help alerts` - Alert configuration help
- `/csa help features` - Feature configuration help
- `/csa help hunting` - IV/EV hunting help
- `/csa help hover` - Hover display help
- `/csa help examples` - Common usage examples

### 2. Configuration Commands

#### Display & Reset
- `/csa config show` - Display current configuration
- `/csa config reset` - Reset all settings to defaults

#### Alert Settings
- `/csa config alerts shinies <true|false>` - Toggle shiny alerts
- `/csa config alerts legendaries <true|false>` - Toggle legendary alerts
- `/csa config alerts mythicals <true|false>` - Toggle mythical alerts
- `/csa config alerts ultrabeasts <true|false>` - Toggle ultra beast alerts
- `/csa config alerts paradox <true|false>` - Toggle paradox alerts
- `/csa config alerts notindex <true|false>` - Toggle not-in-dex alerts
- `/csa config alerts uncaught <true|false>` - Toggle uncaught alerts
- `/csa config alerts everything <true|false>` - Toggle all spawn alerts

#### Feature Settings
- `/csa config features clickableglow <true|false>` - Toggle clickable glow
- `/csa config features autoglow <true|false>` - Toggle automatic glow
- `/csa config features debug <true|false>` - Toggle debug output
- `/csa config features multiplayerwarning <true|false>` - Toggle server warnings

#### Hover Display Settings
- `/csa config hover showivs <true|false>` - Toggle IV display in tooltips
- `/csa config hover showevs <true|false>` - Toggle EV display in tooltips

#### IV Hunting Configuration
- `/csa config ivhunting enabled <true|false>` - Enable/disable IV hunting
- `/csa config ivhunting requireallmins <true|false>` - Require all minimums
- `/csa config ivhunting minperfect <0-6>` - Set minimum perfect IVs
- `/csa config ivhunting minhp <0-31>` - Set minimum HP IV
- `/csa config ivhunting minatk <0-31>` - Set minimum Attack IV
- `/csa config ivhunting mindef <0-31>` - Set minimum Defense IV
- `/csa config ivhunting minspatk <0-31>` - Set minimum Sp. Attack IV
- `/csa config ivhunting minspdef <0-31>` - Set minimum Sp. Defense IV
- `/csa config ivhunting minspeed <0-31>` - Set minimum Speed IV

#### EV Hunting Configuration
- `/csa config evhunting enabled <true|false>` - Enable/disable EV hunting
- `/csa config evhunting minhp <0-3>` - Set minimum HP EV yield
- `/csa config evhunting minatk <0-3>` - Set minimum Attack EV yield
- `/csa config evhunting mindef <0-3>` - Set minimum Defense EV yield
- `/csa config evhunting minspatk <0-3>` - Set minimum Sp. Attack EV yield
- `/csa config evhunting minspdef <0-3>` - Set minimum Sp. Defense EV yield
- `/csa config evhunting minspeed <0-3>` - Set minimum Speed EV yield

## Common Usage Examples

### Shiny Hunting Setup
```
/csa config alerts shinies true
/csa config alerts legendaries false
/csa config alerts everything false
```

### Competitive IV Hunting
```
/csa config ivhunting enabled true
/csa config ivhunting minperfect 4
/csa config ivhunting requireallmins false
```

### EV Training Helper
```
/csa config evhunting enabled true
/csa config evhunting minatk 2
/csa config evhunting minspeed 1
```

### Reset Everything
```
/csa config reset
```

## Technical Implementation

### Multi-Platform Support
- **Fabric**: Uses `ClientConfigCommandHandler` with Fabric's client command system
- **NeoForge**: Uses `NeoForgeClientCommandHandler` with NeoForge's client command system
- **Common**: Shared `ConfigCommandHandler` for server-side commands (if needed in future)

### Configuration Management
- All commands modify the `main.json` configuration file
- Changes are immediately saved and applied
- User-friendly success/error messages with colored formatting
- Input validation for all parameters

### Backwards Compatibility
- All existing `/cobblemonspawnalerts` commands continue to work
- Existing configuration files are fully compatible
- No breaking changes to existing functionality

## User Experience Improvements

### Intuitive Command Structure
- Short, memorable primary command (`/csa`)
- Logical command hierarchy
- Tab completion support
- Clear parameter validation

### Rich Help System
- Context-sensitive help messages
- Categorized command documentation
- Practical usage examples
- Color-coded output for better readability

### Immediate Feedback
- Success messages confirm changes
- Error messages explain what went wrong
- Current settings displayed clearly
- Boolean values shown as "enabled/disabled" with colors

## Files Added/Modified

### New Files
- `common/src/main/java/io/github/stainlessstasis/commands/ConfigCommandHandler.java`
- `common/src/main/java/io/github/stainlessstasis/commands/HelpCommandHandler.java`
- `fabric/src/main/java/io/github/stainlessstasis/commands/ClientConfigCommandHandler.java`
- `neoforge/src/main/java/io/github/stainlessstasis/commands/NeoForgeClientCommandHandler.java`

### Modified Files
- `common/src/main/java/io/github/stainlessstasis/config/ClientConfigManager.java`
- `fabric/src/main/java/io/github/stainlessstasis/CSAFabricClient.java`
- `neoforge/src/main/java/io/github/stainlessstasis/CSANeoClient.java`

## Future Enhancements

### Potential v2.1 Features
- Pokémon-specific configuration commands
- Message template customization commands
- Import/export configuration presets
- Quick setup commands for common scenarios
- Tab completion for Pokémon names

This represents a major quality-of-life improvement for users, eliminating the need to manually edit JSON files and providing a much more user-friendly configuration experience.
