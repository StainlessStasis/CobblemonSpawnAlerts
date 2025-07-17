package io.github.stainlessstasis.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.stainlessstasis.util.ComponentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Provides help and documentation for all available commands
 */
public class HelpCommandHandler {
    
    public static int showMainHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== CobblemonSpawnAlerts v2.0 Commands ===</gold>");
        sendHelpMessage(player, "<yellow>Configuration Commands:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config show</aqua> - Display current configuration");
        sendHelpMessage(player, "  <aqua>/csa config reset</aqua> - Reset all settings to defaults");
        sendHelpMessage(player, "  <aqua>/csa help alerts</aqua> - Show alert configuration commands");
        sendHelpMessage(player, "  <aqua>/csa help features</aqua> - Show feature configuration commands");
        sendHelpMessage(player, "  <aqua>/csa help hunting</aqua> - Show IV/EV hunting commands");
        sendHelpMessage(player, "  <aqua>/csa help hover</aqua> - Show hover display commands");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<yellow>Legacy Commands:</yellow>");
        sendHelpMessage(player, "  <aqua>/cobblemonspawnalerts reload</aqua> - Reload configuration from files");
        sendHelpMessage(player, "  <aqua>/cobblemonspawnalerts openconfig</aqua> - Open config directory");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<gray>Use </gray><white>/csa help [category]</white><gray> for detailed command information</gray>");
        
        return 1;
    }

    public static int showAlertsHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== Alert Configuration Commands ===</gold>");
        sendHelpMessage(player, "<yellow>Global Alert Settings:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config alerts shinies <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for all shiny Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts legendaries <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for all legendary Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts mythicals <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for all mythical Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts ultrabeasts <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for all ultra beast Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts paradox <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for all paradox Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts notindex <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for Pokémon not in your Pokédex");
        sendHelpMessage(player, "  <aqua>/csa config alerts uncaught <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for uncaught Pokémon");
        sendHelpMessage(player, "  <aqua>/csa config alerts everything <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable alerts for ALL Pokémon spawns");
        
        return 1;
    }

    public static int showFeaturesHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== Feature Configuration Commands ===</gold>");
        sendHelpMessage(player, "<yellow>Glow and Visual Features:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config features clickableglow <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable clickable glow effects in chat messages");
        sendHelpMessage(player, "  <aqua>/csa config features autoglow <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable automatic glow effects on rare spawns");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<yellow>System Features:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config features debug <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable debug output to console");
        sendHelpMessage(player, "  <aqua>/csa config features multiplayerwarning <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable multiplayer server warnings");
        
        return 1;
    }

    public static int showHuntingHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== IV/EV Hunting Configuration Commands ===</gold>");
        sendHelpMessage(player, "<yellow>IV Hunting Settings:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting enabled <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable IV hunting mode");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting requireallmins <true|false></aqua>");
        sendHelpMessage(player, "    - Require ALL minimum IV conditions to be met");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting minperfect <0-6></aqua>");
        sendHelpMessage(player, "    - Minimum number of perfect IVs (31) required");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting min[hp|atk|def|spatk|spdef|speed] <0-31></aqua>");
        sendHelpMessage(player, "    - Set minimum IV values for specific stats");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<yellow>EV Hunting Settings:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config evhunting enabled <true|false></aqua>");
        sendHelpMessage(player, "    - Enable/disable EV yield hunting mode");
        sendHelpMessage(player, "  <aqua>/csa config evhunting min[hp|atk|def|spatk|spdef|speed] <0-3></aqua>");
        sendHelpMessage(player, "    - Set minimum EV yield values for specific stats");
        
        return 1;
    }

    public static int showHoverHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== Hover Display Configuration Commands ===</gold>");
        sendHelpMessage(player, "<yellow>Tooltip Settings:</yellow>");
        sendHelpMessage(player, "  <aqua>/csa config hover showivs <true|false></aqua>");
        sendHelpMessage(player, "    - Always show IV values in Pokémon hover tooltips");
        sendHelpMessage(player, "  <aqua>/csa config hover showevs <true|false></aqua>");
        sendHelpMessage(player, "    - Always show EV yield values in Pokémon hover tooltips");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<gray>Note: These settings only work when the server has</gray>");
        sendHelpMessage(player, "<gray>CobblemonSpawnAlerts installed and configured to broadcast</gray>");
        sendHelpMessage(player, "<gray>the relevant data.</gray>");
        
        return 1;
    }

    public static int showExamplesHelp(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        sendHelpMessage(player, "<gold>=== Command Examples ===</gold>");
        sendHelpMessage(player, "<yellow>Common Usage Examples:</yellow>");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<white>Enable shiny hunting mode:</white>");
        sendHelpMessage(player, "  <aqua>/csa config alerts shinies true</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config alerts legendaries false</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config alerts everything false</aqua>");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<white>Set up competitive IV hunting:</white>");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting enabled true</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting minperfect 4</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config ivhunting requireallmins false</aqua>");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<white>Hunt for specific stat Pokémon:</white>");
        sendHelpMessage(player, "  <aqua>/csa config evhunting enabled true</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config evhunting minatk 2</aqua>");
        sendHelpMessage(player, "  <aqua>/csa config evhunting minspeed 1</aqua>");
        sendHelpMessage(player, "");
        sendHelpMessage(player, "<white>Reset everything to defaults:</white>");
        sendHelpMessage(player, "  <aqua>/csa config reset</aqua>");
        
        return 1;
    }

    private static void sendHelpMessage(Player player, String message) {
        Component component = ComponentUtil.convertFromAdventure("<blue>[CSA]</blue> " + message);
        player.sendSystemMessage(component);
    }
}
