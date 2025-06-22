package io.github.stainlessstasis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(CobblemonSpawnAlertsClient.MOD_ID);
    private static final File CONFIG_FILE = MOD_CONFIG_DIR.resolve("main.json").toFile();
    private static final File DEFAULT_MESSAGE_FILE = MOD_CONFIG_DIR.resolve("default_spawn_message.txt").toFile();
    private static Config config;
    private static boolean isReloading;

    public static void loadConfig() {
        isReloading = true;

        try {
            Files.createDirectories(MOD_CONFIG_DIR);
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to create mod config directory: " + MOD_CONFIG_DIR, e);
        }

        if (!DEFAULT_MESSAGE_FILE.exists()) {
            CobblemonSpawnAlertsClient.LOGGER.info("No default spawn message file found, creating a new one.");
            try {
                String spawnMessage = "cobblemon-spawn-alerts.default_spawn_message";
                Files.writeString(DEFAULT_MESSAGE_FILE.toPath(), spawnMessage, StandardOpenOption.CREATE_NEW);
                CobblemonSpawnAlertsClient.LOGGER.info("Created default spawn message file: " + DEFAULT_MESSAGE_FILE.getAbsolutePath());
            } catch (IOException e) {
                CobblemonSpawnAlertsClient.LOGGER.error("Failed to create default spawn message file: " + DEFAULT_MESSAGE_FILE.getAbsolutePath(), e);
            }
        }

        if (!CONFIG_FILE.exists()) {
            CobblemonSpawnAlertsClient.LOGGER.info("No config file found, creating a new one.");
            config = new Config(true);
            saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            config = GSON.fromJson(reader, Config.class);
            if (config == null) {
                CobblemonSpawnAlertsClient.LOGGER.warn("Config file was empty or corrupted, loading default.");
                config = new Config(true);
                saveConfig();
            }
            CobblemonSpawnAlertsClient.LOGGER.info("Config loaded successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to load config file: " + e.getMessage());
            config = new Config(true);
            saveConfig();
        }

        isReloading = false;
    }

    public static void saveConfig() {
        isReloading = true;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            CobblemonSpawnAlertsClient.LOGGER.info("Config saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to save config file: " + e.getMessage());
        }

        isReloading = false;
    }

    public static void reload() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        MessageUtils.sendTranslated(CobblemonSpawnAlertsClient.MOD_ID+".config_reloading");

        // TODO: add this back if i ever add a way to edit config in game
//        saveConfig();
        loadConfig();

        MessageUtils.sendTranslated(CobblemonSpawnAlertsClient.MOD_ID+".config_reloaded");
    }

    public static String getDefaultSpawnMessage() {
        try {
            return Files.readString(DEFAULT_MESSAGE_FILE.toPath());
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Could not read default spawn message from file", e);
            return "Something spawned nearby but the file could not be read so it broke!";
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean isReloading() {
        return isReloading;
    }
}
