package io.github.stainlessstasis.config;

import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.client.Minecraft;

import java.io.File;

public class ClientConfigManager extends AbstractConfigManager {
    private final File MAIN_CONFIG_FILE = MOD_CONFIG_DIR.resolve("main.json").toFile();
    private final File POKEMON_CONFIG_FILE = MOD_CONFIG_DIR.resolve("pokemon.json").toFile();
    private final File MESSAGE_TEMPLATES_FILE = MOD_CONFIG_DIR.resolve("message_templates.json").toFile();
    private MainConfig mainConfig;
    private PokemonConfig pokemonConfig;
    private MessageTemplates messageTemplates;

    @Override
    boolean onConfigLoad() {
        messageTemplates = loadConfigFile(MESSAGE_TEMPLATES_FILE, MessageTemplates.class);
        if (messageTemplates == null) {
            failedLoad(MESSAGE_TEMPLATES_FILE.toPath());
            return false;
        }
        pokemonConfig = loadConfigFile(POKEMON_CONFIG_FILE, PokemonConfig.class);
        if (pokemonConfig == null) {
            failedLoad(POKEMON_CONFIG_FILE.toPath());
            return false;
        }
        mainConfig = loadConfigFile(MAIN_CONFIG_FILE, MainConfig.class);
        if (mainConfig == null) {
            failedLoad(MAIN_CONFIG_FILE.toPath());
            return false;
        }
        return true;
    }

    public void reload() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        MessageUtils.sendTranslated("cobblemon-spawn-alerts.client_config_reloading");
        if (loadConfig()) {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.client_config_reloaded");
        } else {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.client_config_reload_failed");
        }
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public PokemonConfig getPokemonConfig() {
        return pokemonConfig;
    }

    public MessageTemplates getMessageTemplates() {
        return messageTemplates;
    }

    /**
     * Resets the main config to default values and saves it
     */
    public void resetToDefaults() {
        mainConfig = MainConfig.createDefault();
        saveConfigFile(MAIN_CONFIG_FILE, mainConfig);
        
        if (Minecraft.getInstance().player != null) {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.config_reset_success");
        }
    }

    /**
     * Updates a boolean setting in the main config
     */
    public void updateBooleanSetting(String configPath, boolean value) {
        MainConfig updated = updateMainConfigBoolean(mainConfig, configPath, value);
        if (updated != null) {
            mainConfig = updated;
            saveConfigFile(MAIN_CONFIG_FILE, mainConfig);
        } else {
            throw new IllegalArgumentException("Invalid config path: " + configPath);
        }
    }

    /**
     * Updates an integer setting in the main config
     */
    public void updateIntegerSetting(String configPath, int value) {
        MainConfig updated = updateMainConfigInteger(mainConfig, configPath, value);
        if (updated != null) {
            mainConfig = updated;
            saveConfigFile(MAIN_CONFIG_FILE, mainConfig);
        } else {
            throw new IllegalArgumentException("Invalid config path: " + configPath);
        }
    }

    private MainConfig updateMainConfigBoolean(MainConfig config, String path, boolean value) {
        switch (path) {
            case "alertAllShinies":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), value, 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllLegendaries":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    value, config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllMythicals":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), value, config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllUltraBeasts":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), value,
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllParadox":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    value, config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllNotInDex":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), value, config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertAllUncaught":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), value,
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alertEverything":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    value, config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "enableClickableGlow":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), value, config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "enableDebugOutput":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), value,
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "enableAutoGlow":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    value, config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "multiplayerWarning":
                return new MainConfig(config.configVersion(), value, config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alwaysShowIVsInHover":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), value, config.alwaysShowEVsInHover(),
                    config.ivHunting(), config.evHunting());
            case "alwaysShowEVsInHover":
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), value,
                    config.ivHunting(), config.evHunting());
            case "ivHunting.enabled":
                MainConfig.IVHunting newIVHunting = new MainConfig.IVHunting(value, config.ivHunting().requireAllMinimumsMet(),
                    config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), config.ivHunting().minAtk(),
                    config.ivHunting().minDef(), config.ivHunting().minSpAtk(), config.ivHunting().minSpDef(),
                    config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting, config.evHunting());
            case "ivHunting.requireAllMinimumsMet":
                MainConfig.IVHunting newIVHunting2 = new MainConfig.IVHunting(config.ivHunting().enabled(), value,
                    config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), config.ivHunting().minAtk(),
                    config.ivHunting().minDef(), config.ivHunting().minSpAtk(), config.ivHunting().minSpDef(),
                    config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting2, config.evHunting());
            case "evHunting.enabled":
                MainConfig.EVHunting newEVHunting = new MainConfig.EVHunting(value, config.evHunting().minHp(),
                    config.evHunting().minAtk(), config.evHunting().minDef(), config.evHunting().minSpAtk(),
                    config.evHunting().minSpDef(), config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting);
            default:
                return null;
        }
    }

    private MainConfig updateMainConfigInteger(MainConfig config, String path, int value) {
        switch (path) {
            case "ivHunting.minPerfectIVs":
                MainConfig.IVHunting newIVHunting = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), value, config.ivHunting().minHp(), 
                    config.ivHunting().minAtk(), config.ivHunting().minDef(), config.ivHunting().minSpAtk(), 
                    config.ivHunting().minSpDef(), config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting, config.evHunting());
            case "ivHunting.minHp":
                MainConfig.IVHunting newIVHunting2 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), value, 
                    config.ivHunting().minAtk(), config.ivHunting().minDef(), config.ivHunting().minSpAtk(), 
                    config.ivHunting().minSpDef(), config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting2, config.evHunting());
            case "ivHunting.minAtk":
                MainConfig.IVHunting newIVHunting3 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), 
                    value, config.ivHunting().minDef(), config.ivHunting().minSpAtk(), 
                    config.ivHunting().minSpDef(), config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting3, config.evHunting());
            case "ivHunting.minDef":
                MainConfig.IVHunting newIVHunting4 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), 
                    config.ivHunting().minAtk(), value, config.ivHunting().minSpAtk(), 
                    config.ivHunting().minSpDef(), config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting4, config.evHunting());
            case "ivHunting.minSpAtk":
                MainConfig.IVHunting newIVHunting5 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), 
                    config.ivHunting().minAtk(), config.ivHunting().minDef(), value, 
                    config.ivHunting().minSpDef(), config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting5, config.evHunting());
            case "ivHunting.minSpDef":
                MainConfig.IVHunting newIVHunting6 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), 
                    config.ivHunting().minAtk(), config.ivHunting().minDef(), config.ivHunting().minSpAtk(), 
                    value, config.ivHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting6, config.evHunting());
            case "ivHunting.minSpeed":
                MainConfig.IVHunting newIVHunting7 = new MainConfig.IVHunting(config.ivHunting().enabled(), 
                    config.ivHunting().requireAllMinimumsMet(), config.ivHunting().minPerfectIVs(), config.ivHunting().minHp(), 
                    config.ivHunting().minAtk(), config.ivHunting().minDef(), config.ivHunting().minSpAtk(), 
                    config.ivHunting().minSpDef(), value);
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    newIVHunting7, config.evHunting());
            case "evHunting.minHp":
                MainConfig.EVHunting newEVHunting = new MainConfig.EVHunting(config.evHunting().enabled(), value,
                    config.evHunting().minAtk(), config.evHunting().minDef(), config.evHunting().minSpAtk(),
                    config.evHunting().minSpDef(), config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting);
            case "evHunting.minAtk":
                MainConfig.EVHunting newEVHunting2 = new MainConfig.EVHunting(config.evHunting().enabled(), config.evHunting().minHp(),
                    value, config.evHunting().minDef(), config.evHunting().minSpAtk(),
                    config.evHunting().minSpDef(), config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting2);
            case "evHunting.minDef":
                MainConfig.EVHunting newEVHunting3 = new MainConfig.EVHunting(config.evHunting().enabled(), config.evHunting().minHp(),
                    config.evHunting().minAtk(), value, config.evHunting().minSpAtk(),
                    config.evHunting().minSpDef(), config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting3);
            case "evHunting.minSpAtk":
                MainConfig.EVHunting newEVHunting4 = new MainConfig.EVHunting(config.evHunting().enabled(), config.evHunting().minHp(),
                    config.evHunting().minAtk(), config.evHunting().minDef(), value,
                    config.evHunting().minSpDef(), config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting4);
            case "evHunting.minSpDef":
                MainConfig.EVHunting newEVHunting5 = new MainConfig.EVHunting(config.evHunting().enabled(), config.evHunting().minHp(),
                    config.evHunting().minAtk(), config.evHunting().minDef(), config.evHunting().minSpAtk(),
                    value, config.evHunting().minSpeed());
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting5);
            case "evHunting.minSpeed":
                MainConfig.EVHunting newEVHunting6 = new MainConfig.EVHunting(config.evHunting().enabled(), config.evHunting().minHp(),
                    config.evHunting().minAtk(), config.evHunting().minDef(), config.evHunting().minSpAtk(),
                    config.evHunting().minSpDef(), value);
                return new MainConfig(config.configVersion(), config.multiplayerWarning(), config.alertAllShinies(), 
                    config.alertAllLegendaries(), config.alertAllMythicals(), config.alertAllUltraBeasts(),
                    config.alertAllParadox(), config.alertAllNotInDex(), config.alertAllUncaught(),
                    config.alertEverything(), config.enableClickableGlow(), config.enableDebugOutput(),
                    config.enableAutoGlow(), config.alwaysShowIVsInHover(), config.alwaysShowEVsInHover(),
                    config.ivHunting(), newEVHunting6);
            default:
                return null;
        }
    }
}
