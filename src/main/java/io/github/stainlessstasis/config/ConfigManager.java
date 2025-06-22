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
    private static final File MESSAGE_TEMPLATES_FILE = MOD_CONFIG_DIR.resolve("message_templates.json").toFile();
    private static Config config;
    private static MessageTemplates messageTemplates;
    private static boolean isReloading;

    public static void loadConfig() {
        isReloading = true;

        // Create config directory
        try {
            Files.createDirectories(MOD_CONFIG_DIR);
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to create mod config directory: " + MOD_CONFIG_DIR, e);
        }

        loadMessageTemplates();
        loadMainConfig();

        isReloading = false;
    }

    private static void loadMessageTemplates() {
        if (!MESSAGE_TEMPLATES_FILE.exists()) {
            CobblemonSpawnAlertsClient.LOGGER.info("No message templates file found, creating a new one.");
            messageTemplates = new MessageTemplates(true);
            saveMessageTemplates();
            return;
        }

        try (FileReader reader = new FileReader(MESSAGE_TEMPLATES_FILE)) {
            messageTemplates = GSON.fromJson(reader, MessageTemplates.class);
            if (messageTemplates == null) {
                CobblemonSpawnAlertsClient.LOGGER.warn("Message templates file was empty or corrupted, loading default.");
                messageTemplates = new MessageTemplates(true);
                saveMessageTemplates();
            }
            CobblemonSpawnAlertsClient.LOGGER.info("Message templates loaded successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to load message templates file: " + e.getMessage());
            messageTemplates = new MessageTemplates(true);
            saveMessageTemplates();
        }
    }

    private static void loadMainConfig() {
        if (!CONFIG_FILE.exists()) {
            CobblemonSpawnAlertsClient.LOGGER.info("No config file found, creating a new one.");
            config = new Config(true);
            saveMainConfig();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            config = GSON.fromJson(reader, Config.class);
            if (config == null) {
                CobblemonSpawnAlertsClient.LOGGER.warn("Config file was empty or corrupted, loading default.");
                config = new Config(true);
                saveMainConfig();
            }
            CobblemonSpawnAlertsClient.LOGGER.info("Config loaded successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to load config file: " + e.getMessage());
            config = new Config(true);
            saveMainConfig();
        }
    }

    public static void saveMessageTemplates() {
        try (FileWriter writer = new FileWriter(MESSAGE_TEMPLATES_FILE)) {
            GSON.toJson(messageTemplates, writer);
            CobblemonSpawnAlertsClient.LOGGER.info("Message templates saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to save message templates file: " + e.getMessage());
        }
    }

    public static void saveMainConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            CobblemonSpawnAlertsClient.LOGGER.info("Config saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlertsClient.LOGGER.error("Failed to save config file: " + e.getMessage());
        }
    }

    public static Config getConfig() {
        return config;
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

    public static String getDefaultSpawnMessage() {
        return messageTemplates.get("fullSpawnMessage");
    }

    public static String getShinyMessage() {
        return messageTemplates.get("shiny");
    }

    public static String getLevelMessage() {
        return messageTemplates.get("level");
    }

    public static String getLevelMessageHover() {
        return messageTemplates.get("level_hover");
    }

    public static String getIVsMessage() {
        return messageTemplates.get("ivs");
    }

    public static String getIVsMessageHover() {
        return messageTemplates.get("ivs_hover");
    }

    public static String getNatureMessage() {
        return messageTemplates.get("nature");
    }

    public static String getNatureMessageHover() {
        return messageTemplates.get("nature_hover");
    }

    public static String getCoordsMessage() {
        return messageTemplates.get("coords");
    }

    public static String getCoordsMessageHover() {
        return messageTemplates.get("coords_hover");
    }
}
