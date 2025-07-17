package io.github.stainlessstasis.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.ComponentUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Client-side command handler for Fabric
 */
public class ClientConfigCommandHandler {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommandManager.literal("csa")
                .executes(ClientConfigCommandHandler::showMainHelp)
                .then(ClientCommandManager.literal("help")
                    .executes(ClientConfigCommandHandler::showMainHelp)
                    .then(ClientCommandManager.literal("alerts")
                        .executes(ClientConfigCommandHandler::showAlertsHelp))
                    .then(ClientCommandManager.literal("features")
                        .executes(ClientConfigCommandHandler::showFeaturesHelp))
                    .then(ClientCommandManager.literal("hunting")
                        .executes(ClientConfigCommandHandler::showHuntingHelp))
                    .then(ClientCommandManager.literal("hover")
                        .executes(ClientConfigCommandHandler::showHoverHelp))
                    .then(ClientCommandManager.literal("examples")
                        .executes(ClientConfigCommandHandler::showExamplesHelp)))
                .then(ClientCommandManager.literal("config")
                    .then(ClientCommandManager.literal("show")
                        .executes(ClientConfigCommandHandler::showConfig))
                    .then(ClientCommandManager.literal("reset")
                        .executes(ClientConfigCommandHandler::resetConfig))
                    .then(ClientCommandManager.literal("alerts")
                        .then(ClientCommandManager.literal("shinies")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertShinies(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("legendaries")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertLegendaries(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("mythicals")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertMythicals(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("ultrabeasts")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertUltraBeasts(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("paradox")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertParadox(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("notindex")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertNotInDex(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("uncaught")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertUncaught(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("everything")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertEverything(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(ClientCommandManager.literal("features")
                        .then(ClientCommandManager.literal("clickableglow")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setClickableGlow(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("autoglow")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAutoGlow(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("debug")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setDebugOutput(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("multiplayerwarning")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setMultiplayerWarning(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(ClientCommandManager.literal("hover")
                        .then(ClientCommandManager.literal("showivs")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setShowIVsInHover(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("showevs")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setShowEVsInHover(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(ClientCommandManager.literal("ivhunting")
                        .then(ClientCommandManager.literal("enabled")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setIVHuntingEnabled(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("requireallmins")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setIVHuntingRequireAllMins(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("minperfect")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> setIVHuntingMinPerfect(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minhp")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinHP(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minatk")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("mindef")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspatk")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspdef")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspeed")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpeed(ctx, IntegerArgumentType.getInteger(ctx, "value"))))))
                    .then(ClientCommandManager.literal("evhunting")
                        .then(ClientCommandManager.literal("enabled")
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setEVHuntingEnabled(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(ClientCommandManager.literal("minhp")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinHP(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minatk")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("mindef")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspatk")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspdef")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(ClientCommandManager.literal("minspeed")
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpeed(ctx, IntegerArgumentType.getInteger(ctx, "value")))))))
        );
    }

    // Help command methods
    public static int showMainHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    public static int showAlertsHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    public static int showFeaturesHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    public static int showHuntingHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    public static int showHoverHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    public static int showExamplesHelp(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
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

    private static int showConfig(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;

        MainConfig config = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig();
        
        sendConfigMessage(player, "<gold>=== CobblemonSpawnAlerts Configuration ===</gold>");
        sendConfigMessage(player, "<yellow>Alert Settings:</yellow>");
        sendConfigMessage(player, "  Shinies: " + formatBoolean(config.alertAllShinies()));
        sendConfigMessage(player, "  Legendaries: " + formatBoolean(config.alertAllLegendaries()));
        sendConfigMessage(player, "  Mythicals: " + formatBoolean(config.alertAllMythicals()));
        sendConfigMessage(player, "  Ultra Beasts: " + formatBoolean(config.alertAllUltraBeasts()));
        sendConfigMessage(player, "  Paradox: " + formatBoolean(config.alertAllParadox()));
        sendConfigMessage(player, "  Not in Dex: " + formatBoolean(config.alertAllNotInDex()));
        sendConfigMessage(player, "  Uncaught: " + formatBoolean(config.alertAllUncaught()));
        sendConfigMessage(player, "  Everything: " + formatBoolean(config.alertEverything()));
        
        sendConfigMessage(player, "<yellow>Feature Settings:</yellow>");
        sendConfigMessage(player, "  Clickable Glow: " + formatBoolean(config.enableClickableGlow()));
        sendConfigMessage(player, "  Auto Glow: " + formatBoolean(config.enableAutoGlow()));
        sendConfigMessage(player, "  Debug Output: " + formatBoolean(config.enableDebugOutput()));
        sendConfigMessage(player, "  Multiplayer Warning: " + formatBoolean(config.multiplayerWarning()));
        
        sendConfigMessage(player, "<yellow>Hover Settings:</yellow>");
        sendConfigMessage(player, "  Show IVs: " + formatBoolean(config.alwaysShowIVsInHover()));
        sendConfigMessage(player, "  Show EVs: " + formatBoolean(config.alwaysShowEVsInHover()));
        
        sendConfigMessage(player, "<yellow>IV Hunting:</yellow>");
        sendConfigMessage(player, "  Enabled: " + formatBoolean(config.ivHunting().enabled()));
        if (config.ivHunting().enabled()) {
            sendConfigMessage(player, "  Require All Minimums: " + formatBoolean(config.ivHunting().requireAllMinimumsMet()));
            sendConfigMessage(player, "  Min Perfect IVs: " + config.ivHunting().minPerfectIVs());
            sendConfigMessage(player, "  Min HP: " + config.ivHunting().minHp());
            sendConfigMessage(player, "  Min Attack: " + config.ivHunting().minAtk());
            sendConfigMessage(player, "  Min Defense: " + config.ivHunting().minDef());
            sendConfigMessage(player, "  Min Sp. Attack: " + config.ivHunting().minSpAtk());
            sendConfigMessage(player, "  Min Sp. Defense: " + config.ivHunting().minSpDef());
            sendConfigMessage(player, "  Min Speed: " + config.ivHunting().minSpeed());
        }
        
        sendConfigMessage(player, "<yellow>EV Hunting:</yellow>");
        sendConfigMessage(player, "  Enabled: " + formatBoolean(config.evHunting().enabled()));
        if (config.evHunting().enabled()) {
            sendConfigMessage(player, "  Min HP: " + config.evHunting().minHp());
            sendConfigMessage(player, "  Min Attack: " + config.evHunting().minAtk());
            sendConfigMessage(player, "  Min Defense: " + config.evHunting().minDef());
            sendConfigMessage(player, "  Min Sp. Attack: " + config.evHunting().minSpAtk());
            sendConfigMessage(player, "  Min Sp. Defense: " + config.evHunting().minSpDef());
            sendConfigMessage(player, "  Min Speed: " + config.evHunting().minSpeed());
        }
        
        return 1;
    }

    private static int resetConfig(CommandContext<FabricClientCommandSource> ctx) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;

        try {
            CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.resetToDefaults();
            sendSuccessMessage(player, "Configuration reset to defaults!");
        } catch (Exception e) {
            sendErrorMessage(player, "Failed to reset configuration: " + e.getMessage());
            return 0;
        }
        return 1;
    }

    // Alert setting methods
    private static int setAlertShinies(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllShinies", enabled, "Shiny alerts");
    }

    private static int setAlertLegendaries(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllLegendaries", enabled, "Legendary alerts");
    }

    private static int setAlertMythicals(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllMythicals", enabled, "Mythical alerts");
    }

    private static int setAlertUltraBeasts(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllUltraBeasts", enabled, "Ultra Beast alerts");
    }

    private static int setAlertParadox(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllParadox", enabled, "Paradox alerts");
    }

    private static int setAlertNotInDex(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllNotInDex", enabled, "Not-in-Dex alerts");
    }

    private static int setAlertUncaught(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllUncaught", enabled, "Uncaught alerts");
    }

    private static int setAlertEverything(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertEverything", enabled, "Alert everything");
    }

    // Feature setting methods
    private static int setClickableGlow(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableClickableGlow", enabled, "Clickable glow");
    }

    private static int setAutoGlow(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableAutoGlow", enabled, "Auto glow");
    }

    private static int setDebugOutput(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableDebugOutput", enabled, "Debug output");
    }

    private static int setMultiplayerWarning(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "multiplayerWarning", enabled, "Multiplayer warning");
    }

    // Hover setting methods
    private static int setShowIVsInHover(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alwaysShowIVsInHover", enabled, "Show IVs in hover");
    }

    private static int setShowEVsInHover(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alwaysShowEVsInHover", enabled, "Show EVs in hover");
    }

    // IV Hunting methods
    private static int setIVHuntingEnabled(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "ivHunting.enabled", enabled, "IV hunting");
    }

    private static int setIVHuntingRequireAllMins(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "ivHunting.requireAllMinimumsMet", enabled, "IV hunting require all minimums");
    }

    private static int setIVHuntingMinPerfect(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minPerfectIVs", value, "IV hunting min perfect IVs");
    }

    private static int setIVHuntingMinHP(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minHp", value, "IV hunting min HP");
    }

    private static int setIVHuntingMinAtk(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minAtk", value, "IV hunting min Attack");
    }

    private static int setIVHuntingMinDef(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minDef", value, "IV hunting min Defense");
    }

    private static int setIVHuntingMinSpAtk(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpAtk", value, "IV hunting min Sp. Attack");
    }

    private static int setIVHuntingMinSpDef(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpDef", value, "IV hunting min Sp. Defense");
    }

    private static int setIVHuntingMinSpeed(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpeed", value, "IV hunting min Speed");
    }

    // EV Hunting methods
    private static int setEVHuntingEnabled(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "evHunting.enabled", enabled, "EV hunting");
    }

    private static int setEVHuntingMinHP(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minHp", value, "EV hunting min HP");
    }

    private static int setEVHuntingMinAtk(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minAtk", value, "EV hunting min Attack");
    }

    private static int setEVHuntingMinDef(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minDef", value, "EV hunting min Defense");
    }

    private static int setEVHuntingMinSpAtk(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpAtk", value, "EV hunting min Sp. Attack");
    }

    private static int setEVHuntingMinSpDef(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpDef", value, "EV hunting min Sp. Defense");
    }

    private static int setEVHuntingMinSpeed(CommandContext<FabricClientCommandSource> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpeed", value, "EV hunting min Speed");
    }

    // Helper methods
    private static int updateBooleanConfig(CommandContext<FabricClientCommandSource> ctx, String configPath, boolean value, String settingName) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;

        try {
            CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.updateBooleanSetting(configPath, value);
            sendSuccessMessage(player, settingName + " set to " + formatBoolean(value));
        } catch (Exception e) {
            sendErrorMessage(player, "Failed to update " + settingName + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static int updateIntegerConfig(CommandContext<FabricClientCommandSource> ctx, String configPath, int value, String settingName) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;

        try {
            CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.updateIntegerSetting(configPath, value);
            sendSuccessMessage(player, settingName + " set to " + value);
        } catch (Exception e) {
            sendErrorMessage(player, "Failed to update " + settingName + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static void sendConfigMessage(Player player, String message) {
        Component component = ComponentUtil.convertFromAdventure("<blue>[CSA]</blue> " + message);
        player.sendSystemMessage(component);
    }

    private static void sendSuccessMessage(Player player, String message) {
        Component component = ComponentUtil.convertFromAdventure("<green>[CSA]</green> <white>" + message + "</white>");
        player.sendSystemMessage(component);
    }

    private static void sendErrorMessage(Player player, String message) {
        Component component = ComponentUtil.convertFromAdventure("<red>[CSA]</red> <white>" + message + "</white>");
        player.sendSystemMessage(component);
    }

    private static void sendHelpMessage(Player player, String message) {
        Component component = ComponentUtil.convertFromAdventure("<blue>[CSA]</blue> " + message);
        player.sendSystemMessage(component);
    }

    private static String formatBoolean(boolean value) {
        return value ? "<green>enabled</green>" : "<red>disabled</red>";
    }
}
