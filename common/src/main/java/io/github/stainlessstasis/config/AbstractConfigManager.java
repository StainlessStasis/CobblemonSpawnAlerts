package io.github.stainlessstasis.config;

import com.google.gson.*;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfigManager {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final Path MOD_CONFIG_DIR = Services.PLATFORM.getConfigDir().resolve("cobblemon-spawn-alerts");
    protected boolean isReloading;

    public boolean loadConfig() {
        isReloading = true;

        try {
            Files.createDirectories(MOD_CONFIG_DIR);
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to create mod config directory `"+MOD_CONFIG_DIR+"`. Error: ", e);
            failedLoad(MOD_CONFIG_DIR);
            return false;
        }

        if (!onConfigLoad()) {
            return false;
        }


        isReloading = false;
        return true;
    }

    abstract boolean onConfigLoad();

    public void failedLoad(Path path) {
        MessageUtils.sendTranslated("cobblemon-spawn-alerts.config_load_failed", path);
        isReloading = false;
    }

    public <T> T loadConfigFile(File file, Class<T> config) {
        String fileName = file.getName();
        T finalConfig;

        try {
            Method createDefaultMethod = config.getMethod("createDefault");
            T defaultConfig = (T) createDefaultMethod.invoke(null);

            if (!file.exists()) {
                CobblemonSpawnAlerts.LOGGER.info("No config file `"+fileName+"` found, creating a new one with default settings.");
                saveConfigFile(file, defaultConfig);
                return defaultConfig;
            }

        JsonObject userConfigJson = null;
        try (FileReader reader = new FileReader(file)) {
            JsonElement json = JsonParser.parseReader(reader);
            if (json.isJsonObject()) {
                userConfigJson = json.getAsJsonObject();
            } else {
                CobblemonSpawnAlerts.LOGGER.warn("Config file `"+fileName+"` is not a valid JSON object. Overwriting with default.");
            }
        } catch (JsonSyntaxException e) {
            CobblemonSpawnAlerts.LOGGER.error("Config file `"+fileName+"` is corrupted or malformed JSON. Error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to read config file `"+fileName+"`. Error: " + e.getMessage());
            return null;
        }

        JsonObject mergedJson = GSON.toJsonTree(defaultConfig).getAsJsonObject();

        if (userConfigJson != null) {
            for (String key : userConfigJson.keySet()) {
                mergedJson.add(key, userConfigJson.get(key));
            }
        }

        finalConfig = GSON.fromJson(mergedJson, config);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            CobblemonSpawnAlerts.LOGGER.error("Something went VERY wrong while trying to load `"+fileName+"`. Error:", e);
            failedLoad(file.toPath());
            return null;
        }

        CobblemonSpawnAlerts.LOGGER.info("Config file `"+fileName+"` loaded successfully.");
        saveConfigFile(file, finalConfig);
        return finalConfig;
    }

    public <T> void saveConfigFile(File file, T config) {
        String fileName = file.getName();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
            CobblemonSpawnAlerts.LOGGER.info("Config file `"+fileName+"` saved successfully.");
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to save config file `"+fileName+"`. Error: " + e.getMessage());
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.config_save_failed", file.toPath());
            isReloading = false;
        }
    }

    public boolean isReloading() {
        return isReloading;
    }

    public static void openDirectory() {
        Util.getPlatform().openPath(MOD_CONFIG_DIR);
    }
}
