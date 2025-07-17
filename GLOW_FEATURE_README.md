# CobblemonSpawnAlerts - Glow Feature

## New Feature: Clickable Pokémon Glow Effect

This enhanced version of CobblemonSpawnAlerts adds a fantastic new feature that makes finding spawned Pokémon much easier!

### How it Works

When you receive a spawn alert in chat, you can now **click on the message** to make the Pokémon glow for 30 seconds! This makes it much easier to find the Pokémon even if it has wandered away from its spawn location.

### Features

- **Click to Glow**: Click any spawn alert message to highlight the Pokémon with a bright glow effect
- **30-Second Duration**: The glow effect lasts for 30 seconds, giving you plenty of time to reach the Pokémon
- **Hover Tooltip**: Hover over spawn messages to see instructions about clicking to highlight
- **Automatic Cleanup**: Glow effects are automatically removed when you disconnect or change worlds
- **Configurable**: Can be enabled/disabled in your config file

### Configuration

The glow feature can be controlled via the `enableClickableGlow` setting in your `main.json` config file:

```json
{
  "enableClickableGlow": true,
  ...
}
```

Set to `false` if you prefer the original non-clickable messages.

### Usage Instructions

1. When you see a spawn alert in chat, simply **click on the message**
2. The Pokémon will immediately start glowing with a bright outline
3. Navigate to the coordinates and look for the glowing Pokémon
4. The glow effect will automatically disappear after 30 seconds

### Technical Details

- Uses Minecraft's built-in glow effect (same as spectral arrows)
- Client-side only - no server performance impact
- Works with both Fabric and NeoForge
- Compatible with all existing CobblemonSpawnAlerts features

### Installation

1. Replace your existing CobblemonSpawnAlerts mod with this enhanced version
2. The feature is enabled by default for new installations
3. Existing users can enable it by setting `"enableClickableGlow": true` in their config

### Troubleshooting

- **Glow not working?** Make sure `enableClickableGlow` is set to `true` in your config
- **Can't find the Pokémon?** The Pokémon may have despawned or been caught by another player
- **Click not working?** Make sure you're clicking directly on the spawn alert message

This feature dramatically improves the user experience by solving the common problem of losing track of spawned Pokémon!
