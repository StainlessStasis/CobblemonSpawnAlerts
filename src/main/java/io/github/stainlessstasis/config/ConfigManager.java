package io.github.stainlessstasis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(CobblemonSpawnAlertsClient.MOD_ID);
    private static final File MAIN_CONFIG_FILE = MOD_CONFIG_DIR.resolve("main.json").toFile();
    private static final File POKEMON_CONFIG_FILE = MOD_CONFIG_DIR.resolve("pokemon.json").toFile();
    private static final File MESSAGE_TEMPLATES_FILE = MOD_CONFIG_DIR.resolve("message_templates.json").toFile();
    private static MainConfig mainConfig;
    private static PokemonConfig pokemonConfig;
    private static MessageTemplates messageTemplates;
    private static boolean isReloading;

    public static void loadConfig() {
        isReloading = true;

        try {
            Files.createDirectories(MOD_CONFIG_DIR);
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to create mod config directory: " + MOD_CONFIG_DIR, e);
        }

        messageTemplates = loadConfigFile(MESSAGE_TEMPLATES_FILE, MessageTemplates.class);
        pokemonConfig = loadConfigFile(POKEMON_CONFIG_FILE, PokemonConfig.class);
        mainConfig = loadConfigFile(MAIN_CONFIG_FILE, MainConfig.class);

        isReloading = false;
    }

    private static <T> T loadConfigFile(File file, Class<T> config) {
        String fileName = file.getName();

        if (!file.exists()) {
            CobblemonSpawnAlertsClient.LOGGER.info("No config file `"+fileName+"` found, creating a new one.");
            try {
                Method method = config.getMethod("createDefault");
                T newConfig = (T) method.invoke(null);
                saveConfigFile(file, newConfig);
                return newConfig;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                CobblemonSpawnAlertsClient.LOGGER.error("Failed to create new config file for `"+fileName+"`: "+e);
                return null;
            }
        }

        try (FileReader reader = new FileReader(file)) {
            T newConfig;
            newConfig = GSON.fromJson(reader, config);
            if (newConfig == null) {
                CobblemonSpawnAlertsClient.LOGGER.warn("File `"+fileName+"` was empty or corrupted, loading default.");
                try {
                    Method method = config.getMethod("createDefault");
                    newConfig = (T) method.invoke(null);
                    saveConfigFile(file, newConfig);
                    return newConfig;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    CobblemonSpawnAlertsClient.LOGGER.error("Failed to create default config file for `"+fileName+"`: "+e);
                    return null;
                }
            } else {
                CobblemonSpawnAlertsClient.LOGGER.info("Config file `"+fileName+"` loaded successfully.");
                saveConfigFile(file, newConfig);
                return newConfig;
            }
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to load config file `"+fileName+"`: " + e.getMessage());
            return null;
        }
    }

    public static <T> void saveConfigFile(File file, T config) {
        String fileName = file.getName();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
            CobblemonSpawnAlertsClient.LOGGER.info("Config file `"+fileName+"` saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to save config file `"+fileName+"`: " + e.getMessage());
        }
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    public static PokemonConfig getPokemonConfig() {
        return pokemonConfig;
    }

    public static MessageTemplates getMessageTemplates() {
        return messageTemplates;
    }

    public static void reload() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        MessageUtils.sendTranslated(CobblemonSpawnAlertsClient.MOD_ID+".config_reloading");
        loadConfig();
        MessageUtils.sendTranslated(CobblemonSpawnAlertsClient.MOD_ID+".config_reloaded");
    }

    public static boolean isReloading() {
        return isReloading;
    }

    public static void openDirectory() {
        Util.getPlatform().openPath(MOD_CONFIG_DIR);
    }
}
