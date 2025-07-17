package io.github.stainlessstasis.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.ComponentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Handles all config-related commands for version 1.10
 * Allows players to modify all config settings through in-game commands
 */
public class ConfigCommandHandler {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cobblemonSpawnAlertsNode = dispatcher.register(
            Commands.literal("csa")
                .executes(HelpCommandHandler::showMainHelp)
                .then(Commands.literal("help")
                    .executes(HelpCommandHandler::showMainHelp)
                    .then(Commands.literal("alerts")
                        .executes(HelpCommandHandler::showAlertsHelp))
                    .then(Commands.literal("features")
                        .executes(HelpCommandHandler::showFeaturesHelp))
                    .then(Commands.literal("hunting")
                        .executes(HelpCommandHandler::showHuntingHelp))
                    .then(Commands.literal("hover")
                        .executes(HelpCommandHandler::showHoverHelp))
                    .then(Commands.literal("examples")
                        .executes(HelpCommandHandler::showExamplesHelp)))
                .then(Commands.literal("config")
                    .then(Commands.literal("show")
                        .executes(ConfigCommandHandler::showConfig))
                    .then(Commands.literal("reset")
                        .executes(ConfigCommandHandler::resetConfig))
                    .then(Commands.literal("alerts")
                        .then(Commands.literal("shinies")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllShinies", "Shiny alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertShinies(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("legendaries")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllLegendaries", "Legendary alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertLegendaries(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("mythicals")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllMythicals", "Mythical alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertMythicals(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("ultrabeasts")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllUltraBeasts", "Ultra Beast alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertUltraBeasts(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("paradox")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllParadox", "Paradox alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertParadox(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("notindex")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllNotInDex", "Not-in-Dex alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertNotInDex(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("uncaught")
                            .executes(ctx -> showCurrentSetting(ctx, "alertAllUncaught", "Uncaught alerts"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertUncaught(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("everything")
                            .executes(ctx -> showCurrentSetting(ctx, "alertEverything", "Alert everything"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAlertEverything(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(Commands.literal("features")
                        .then(Commands.literal("clickableglow")
                            .executes(ctx -> showCurrentSetting(ctx, "enableClickableGlow", "Clickable glow"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setClickableGlow(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("autoglow")
                            .executes(ctx -> showCurrentSetting(ctx, "enableAutoGlow", "Auto glow"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setAutoGlow(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("debug")
                            .executes(ctx -> showCurrentSetting(ctx, "enableDebugOutput", "Debug output"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setDebugOutput(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("multiplayerwarning")
                            .executes(ctx -> showCurrentSetting(ctx, "multiplayerWarning", "Multiplayer warning"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setMultiplayerWarning(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(Commands.literal("hover")
                        .then(Commands.literal("showivs")
                            .executes(ctx -> showCurrentSetting(ctx, "alwaysShowIVsInHover", "Show IVs in hover"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setShowIVsInHover(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("showevs")
                            .executes(ctx -> showCurrentSetting(ctx, "alwaysShowEVsInHover", "Show EVs in hover"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setShowEVsInHover(ctx, BoolArgumentType.getBool(ctx, "enabled"))))))
                    .then(Commands.literal("ivhunting")
                        .then(Commands.literal("enabled")
                            .executes(ctx -> showCurrentBooleanSetting(ctx, "ivHunting.enabled", "IV hunting"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setIVHuntingEnabled(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("requireallmins")
                            .executes(ctx -> showCurrentBooleanSetting(ctx, "ivHunting.requireAllMinimumsMet", "IV hunting require all minimums"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setIVHuntingRequireAllMins(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("minperfect")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minPerfectIVs", "IV hunting min perfect IVs"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> setIVHuntingMinPerfect(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minhp")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minHp", "IV hunting min HP"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinHP(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minatk")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minAtk", "IV hunting min Attack"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("mindef")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minDef", "IV hunting min Defense"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspatk")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minSpAtk", "IV hunting min Sp. Attack"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspdef")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minSpDef", "IV hunting min Sp. Defense"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspeed")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "ivHunting.minSpeed", "IV hunting min Speed"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 31))
                                .executes(ctx -> setIVHuntingMinSpeed(ctx, IntegerArgumentType.getInteger(ctx, "value"))))))
                    .then(Commands.literal("evhunting")
                        .then(Commands.literal("enabled")
                            .executes(ctx -> showCurrentBooleanSetting(ctx, "evHunting.enabled", "EV hunting"))
                            .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> setEVHuntingEnabled(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
                        .then(Commands.literal("minhp")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minHp", "EV hunting min HP"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinHP(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minatk")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minAtk", "EV hunting min Attack"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("mindef")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minDef", "EV hunting min Defense"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspatk")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minSpAtk", "EV hunting min Sp. Attack"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpAtk(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspdef")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minSpDef", "EV hunting min Sp. Defense"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpDef(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("minspeed")
                            .executes(ctx -> showCurrentIntegerSetting(ctx, "evHunting.minSpeed", "EV hunting min Speed"))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> setEVHuntingMinSpeed(ctx, IntegerArgumentType.getInteger(ctx, "value")))))))
        );

        // Add alias for backwards compatibility
        dispatcher.register(Commands.literal("cobblemonspawnalerts").redirect(cobblemonSpawnAlertsNode));
    }

    private static int showConfig(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
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

    private static int resetConfig(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
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
    private static int setAlertShinies(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllShinies", enabled, "Shiny alerts");
    }

    private static int setAlertLegendaries(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllLegendaries", enabled, "Legendary alerts");
    }

    private static int setAlertMythicals(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllMythicals", enabled, "Mythical alerts");
    }

    private static int setAlertUltraBeasts(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllUltraBeasts", enabled, "Ultra Beast alerts");
    }

    private static int setAlertParadox(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllParadox", enabled, "Paradox alerts");
    }

    private static int setAlertNotInDex(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllNotInDex", enabled, "Not-in-Dex alerts");
    }

    private static int setAlertUncaught(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertAllUncaught", enabled, "Uncaught alerts");
    }

    private static int setAlertEverything(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alertEverything", enabled, "Alert everything");
    }

    // Feature setting methods
    private static int setClickableGlow(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableClickableGlow", enabled, "Clickable glow");
    }

    private static int setAutoGlow(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableAutoGlow", enabled, "Auto glow");
    }

    private static int setDebugOutput(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "enableDebugOutput", enabled, "Debug output");
    }

    private static int setMultiplayerWarning(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "multiplayerWarning", enabled, "Multiplayer warning");
    }

    // Hover setting methods
    private static int setShowIVsInHover(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alwaysShowIVsInHover", enabled, "Show IVs in hover");
    }

    private static int setShowEVsInHover(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "alwaysShowEVsInHover", enabled, "Show EVs in hover");
    }

    // IV Hunting methods
    private static int setIVHuntingEnabled(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "ivHunting.enabled", enabled, "IV hunting");
    }

    private static int setIVHuntingRequireAllMins(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "ivHunting.requireAllMinimumsMet", enabled, "IV hunting require all minimums");
    }

    private static int setIVHuntingMinPerfect(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minPerfectIVs", value, "IV hunting min perfect IVs");
    }

    private static int setIVHuntingMinHP(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minHp", value, "IV hunting min HP");
    }

    private static int setIVHuntingMinAtk(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minAtk", value, "IV hunting min Attack");
    }

    private static int setIVHuntingMinDef(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minDef", value, "IV hunting min Defense");
    }

    private static int setIVHuntingMinSpAtk(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpAtk", value, "IV hunting min Sp. Attack");
    }

    private static int setIVHuntingMinSpDef(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpDef", value, "IV hunting min Sp. Defense");
    }

    private static int setIVHuntingMinSpeed(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "ivHunting.minSpeed", value, "IV hunting min Speed");
    }

    // EV Hunting methods
    private static int setEVHuntingEnabled(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        return updateBooleanConfig(ctx, "evHunting.enabled", enabled, "EV hunting");
    }

    private static int setEVHuntingMinHP(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minHp", value, "EV hunting min HP");
    }

    private static int setEVHuntingMinAtk(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minAtk", value, "EV hunting min Attack");
    }

    private static int setEVHuntingMinDef(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minDef", value, "EV hunting min Defense");
    }

    private static int setEVHuntingMinSpAtk(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpAtk", value, "EV hunting min Sp. Attack");
    }

    private static int setEVHuntingMinSpDef(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpDef", value, "EV hunting min Sp. Defense");
    }

    private static int setEVHuntingMinSpeed(CommandContext<CommandSourceStack> ctx, int value) {
        return updateIntegerConfig(ctx, "evHunting.minSpeed", value, "EV hunting min Speed");
    }

    // Helper methods
    private static int updateBooleanConfig(CommandContext<CommandSourceStack> ctx, String configPath, boolean value, String settingName) {
        Player player = ctx.getSource().getPlayer();
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

    private static int updateIntegerConfig(CommandContext<CommandSourceStack> ctx, String configPath, int value, String settingName) {
        Player player = ctx.getSource().getPlayer();
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

    private static String formatBoolean(boolean value) {
        return value ? "<green>enabled</green>" : "<red>disabled</red>";
    }

    // Helper methods for showing current settings
    private static int showCurrentSetting(CommandContext<CommandSourceStack> ctx, String configPath, String settingName) {
        return showCurrentBooleanSetting(ctx, configPath, settingName);
    }

    private static int showCurrentBooleanSetting(CommandContext<CommandSourceStack> ctx, String configPath, String settingName) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        try {
            MainConfig config = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig();
            boolean currentValue = getBooleanFromConfig(config, configPath);
            sendConfigMessage(player, settingName + " is currently " + formatBoolean(currentValue));
        } catch (Exception e) {
            sendErrorMessage(player, "Failed to get " + settingName + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static int showCurrentIntegerSetting(CommandContext<CommandSourceStack> ctx, String configPath, String settingName) {
        Player player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        try {
            MainConfig config = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig();
            int currentValue = getIntegerFromConfig(config, configPath);
            sendConfigMessage(player, settingName + " is currently <yellow>" + currentValue + "</yellow>");
        } catch (Exception e) {
            sendErrorMessage(player, "Failed to get " + settingName + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static boolean getBooleanFromConfig(MainConfig config, String path) {
        return switch (path) {
            case "alertAllShinies" -> config.alertAllShinies();
            case "alertAllLegendaries" -> config.alertAllLegendaries();
            case "alertAllMythicals" -> config.alertAllMythicals();
            case "alertAllUltraBeasts" -> config.alertAllUltraBeasts();
            case "alertAllParadox" -> config.alertAllParadox();
            case "alertAllNotInDex" -> config.alertAllNotInDex();
            case "alertAllUncaught" -> config.alertAllUncaught();
            case "alertEverything" -> config.alertEverything();
            case "enableClickableGlow" -> config.enableClickableGlow();
            case "enableAutoGlow" -> config.enableAutoGlow();
            case "enableDebugOutput" -> config.enableDebugOutput();
            case "multiplayerWarning" -> config.multiplayerWarning();
            case "alwaysShowIVsInHover" -> config.alwaysShowIVsInHover();
            case "alwaysShowEVsInHover" -> config.alwaysShowEVsInHover();
            case "ivHunting.enabled" -> config.ivHunting().enabled();
            case "ivHunting.requireAllMinimumsMet" -> config.ivHunting().requireAllMinimumsMet();
            case "evHunting.enabled" -> config.evHunting().enabled();
            default -> throw new IllegalArgumentException("Unknown boolean config path: " + path);
        };
    }

    private static int getIntegerFromConfig(MainConfig config, String path) {
        return switch (path) {
            case "ivHunting.minPerfectIVs" -> config.ivHunting().minPerfectIVs();
            case "ivHunting.minHp" -> config.ivHunting().minHp();
            case "ivHunting.minAtk" -> config.ivHunting().minAtk();
            case "ivHunting.minDef" -> config.ivHunting().minDef();
            case "ivHunting.minSpAtk" -> config.ivHunting().minSpAtk();
            case "ivHunting.minSpDef" -> config.ivHunting().minSpDef();
            case "ivHunting.minSpeed" -> config.ivHunting().minSpeed();
            case "evHunting.minHp" -> config.evHunting().minHp();
            case "evHunting.minAtk" -> config.evHunting().minAtk();
            case "evHunting.minDef" -> config.evHunting().minDef();
            case "evHunting.minSpAtk" -> config.evHunting().minSpAtk();
            case "evHunting.minSpDef" -> config.evHunting().minSpDef();
            case "evHunting.minSpeed" -> config.evHunting().minSpeed();
            default -> throw new IllegalArgumentException("Unknown integer config path: " + path);
        };
    }
}
