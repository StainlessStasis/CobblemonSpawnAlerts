# Cobblemon Spawn Alerts - Version 1.9.10 Fixes

## Issues Fixed

### 1. Auto-Glow Feature Not Working

**Problem**: The auto-glow feature wasn't functioning properly due to issues with the packet sending mechanism.

**Solution**: 
- Refactored `GlowEffectManager` to use the platform service system instead of reflection
- Added `sendGlowEffectPacket()` method to `IPlatformHelper` interface
- Implemented the method in both Fabric and NeoForge platform helpers
- The auto-glow feature now properly sends packets to the server when enabled

### 2. Independent EV/IV Display in Hover Text

**Problem**: EV and IV information could only be shown in hover text if EV/IV hunting was enabled, which caused unwanted additional alerts.

**Solution**:
- Added two new configuration options to `MainConfig`:
  - `alwaysShowIVsInHover`: Show IVs in hover text regardless of IV hunting settings
  - `alwaysShowEVsInHover`: Show EVs in hover text regardless of EV hunting settings
- Modified `AlertHandler.applyDynamicReplacements()` to check these new options
- IVs/EVs will now appear in hover text if either the specific display mode is set to HOVER OR the new always-show option is enabled
- **These options are now enabled by default**

### 3. Improved Server Mod Detection and Warnings

**Problem**: Warning messages were vague about whether the server had the mod installed, and glow features would attempt to work even when the server didn't support them.

**Solution**:
- Updated warning message to specifically state when the server does NOT have the mod
- Added server mod checks to disable glow functionality when server doesn't support it:
  - Auto-glow is disabled when server lacks the mod
  - Click-to-glow functionality is disabled when server lacks the mod
  - Glow commands show helpful error messages when server lacks the mod
- All glow-related features now check server compatibility before attempting to function

## Configuration

### New Options (Enabled by Default):
```json
{
  "alwaysShowIVsInHover": true,
  "alwaysShowEVsInHover": true
}
```

### Updated Warning Message:
The multiplayer warning now clearly states: **"The server does NOT have CobblemonSpawnAlerts installed. Pokemon stats (IVs, EVs, Nature, Ability) will not display correctly and some features like glow effects are disabled."**

## Changes Made

### Files Modified:
1. `MainConfig.java` - Added new configuration options, set them to true by default
2. `AlertHandler.java` - Added logic for independent EV/IV display and server mod checks for glow features
3. `GlowEffectManager.java` - Fixed packet sending mechanism  
4. `IPlatformHelper.java` - Added sendGlowEffectPacket method
5. `FabricPlatformHelper.java` - Implemented packet sending
6. `NeoForgePlatformHelper.java` - Implemented packet sending
7. `CSANeoClient.java` - Added packet sending method
8. `ClientCommandHandler.java` (both Fabric & NeoForge) - Added server mod checks for glow commands
9. `en_us.json` - Updated warning message text
10. `main.json` - Updated config version and enabled new options

### Behavioral Changes:
- **Glow Features**: Only work when server has the mod installed
- **Auto-Glow**: Disabled automatically when server lacks mod support
- **Click-to-Glow**: Disabled automatically when server lacks mod support  
- **Glow Commands**: Show helpful error when server lacks mod support
- **Warning Messages**: Now clearly indicate server mod status
- **EV/IV Display**: Enabled by default in hover text independent of hunting

## Testing

### To Test Server Detection:
1. Join a server without the mod - you should see the clear warning message
2. Try clicking a Pokemon spawn message - no click functionality should be added
3. Try the `/csa_glow` command - should show "server does not have mod" error

### To Test EV/IV Display:
1. The new options are already enabled in your config
2. Hover over Pokemon spawn messages - should see IV/EV info even with hunting disabled
3. Works both with and without server mod support

## Debug Information

Debug output shows:
- `[CSA CLIENT]` messages for glow packet sending
- `[CSA SERVER]` messages for glow effect application  
- Server mod detection status
- Feature enable/disable messages based on server capabilities

Set `"enableDebugOutput": false` to disable debug messages.
