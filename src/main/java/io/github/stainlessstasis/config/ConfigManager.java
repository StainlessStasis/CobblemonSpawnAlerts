package io.github.stainlessstasis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.stainlessstasis.CobblemonSpawnAlerts;
import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.sound.Sound;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(CobblemonSpawnAlerts.MOD_ID);
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
            CobblemonSpawnAlerts.LOGGER.error("Failed to create mod config directory: " + MOD_CONFIG_DIR, e);
            failedLoad(MOD_CONFIG_DIR);
            return;
        }

        messageTemplates = loadConfigFile(MESSAGE_TEMPLATES_FILE, MessageTemplates.class);
        if (messageTemplates == null) {
            failedLoad(MESSAGE_TEMPLATES_FILE.toPath());
            return;
        }
        pokemonConfig = loadConfigFile(POKEMON_CONFIG_FILE, PokemonConfig.class);
        if (pokemonConfig == null) {
            failedLoad(POKEMON_CONFIG_FILE.toPath());
            return;
        }
        mainConfig = loadConfigFile(MAIN_CONFIG_FILE, MainConfig.class);
        if (mainConfig == null) {
            failedLoad(MAIN_CONFIG_FILE.toPath());
            return;
        }

        isReloading = false;
    }

    private static void failedLoad(Path path) {
        MessageUtils.sendTranslated("cobblemon-spawn-alerts.config_load_failed", path);
        isReloading = false;
    }

    private static <T> T loadConfigFile(File file, Class<T> config) {
        String fileName = file.getName();

        if (!file.exists()) {
            CobblemonSpawnAlerts.LOGGER.info("No config file `"+fileName+"` found, creating a new one.");
            try {
                Method method = config.getMethod("createDefault");
                T newConfig = (T) method.invoke(null);
                saveConfigFile(file, newConfig);
                return newConfig;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                CobblemonSpawnAlerts.LOGGER.error("Failed to create new config file for `"+fileName+"`: "+e);
                return null;
            }
        }

        try (FileReader reader = new FileReader(file)) {
            T newConfig;
            newConfig = GSON.fromJson(reader, config);
            if (newConfig == null) {
                CobblemonSpawnAlerts.LOGGER.warn("File `"+fileName+"` was empty or corrupted, loading default.");
                try {
                    Method method = config.getMethod("createDefault");
                    newConfig = (T) method.invoke(null);
                    saveConfigFile(file, newConfig);
                    return newConfig;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    CobblemonSpawnAlerts.LOGGER.error("Failed to create default config file for `"+fileName+"`: "+e);
                    return null;
                }
            } else {
                CobblemonSpawnAlerts.LOGGER.info("Config file `"+fileName+"` loaded successfully.");
                saveConfigFile(file, newConfig);
                return newConfig;
            }
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to load config file `"+fileName+"`: " + e.getMessage());
            return null;
        }
    }

    private static <T> void saveConfigFile(File file, T config) {
        String fileName = file.getName();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
            CobblemonSpawnAlerts.LOGGER.info("Config file `"+fileName+"` saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to save config file `"+fileName+"`: " + e.getMessage());
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.config_save_failed", file.toPath());
            isReloading = false;
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

        MessageUtils.sendTranslated(CobblemonSpawnAlerts.MOD_ID+".config_reloading");
        loadConfig();
        MessageUtils.sendTranslated(CobblemonSpawnAlerts.MOD_ID+".config_reloaded");
    }

    public static boolean isReloading() {
        return isReloading;
    }

    public static void openDirectory() {
        Util.getPlatform().openPath(MOD_CONFIG_DIR);
    }
}
