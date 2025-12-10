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
import java.util.Map;

public abstract class AbstractConfigManager {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
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
            mergeJsonObjects(mergedJson, userConfigJson);
        }

        // Auto update config version
        if (mergedJson.has("configVersion")) {
            String currentVersion = mergedJson.get("configVersion").getAsString();
            if (!currentVersion.equals(CobblemonSpawnAlerts.MOD_VERSION)) {
                mergedJson.add("configVersion", new JsonPrimitive(CobblemonSpawnAlerts.MOD_VERSION));
            }
        }

        if (config.equals(PokemonConfig.class)) {
            applyPokemonConfigMerge(fileName, mergedJson, userConfigJson, (PokemonConfig) defaultConfig);
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

    private void mergeJsonObjects(JsonObject base, JsonObject overwrite) {
        for (Map.Entry<String, JsonElement> entry : overwrite.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (base.has(key) && base.get(key).isJsonObject() && value.isJsonObject()) {
                mergeJsonObjects(base.get(key).getAsJsonObject(), value.getAsJsonObject());
            } else {
                base.add(key, value);
            }
        }
    }

    private void applyPokemonConfigMerge(String fileName, JsonObject mergedJson, JsonObject userConfigJson, PokemonConfig defaultConfigs) {
        PokemonConfig.PokemonSpecificConfig defaultPokemonConfig =
                defaultConfigs.pokemonConfigs().get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME);

        if (defaultPokemonConfig == null) {
            CobblemonSpawnAlerts.LOGGER.error("Default config entry not found in PokemonConfig#createDefault. Skipping specific pokemon config merge.");
            return;
        }

        JsonObject mergedPokemonConfigs = mergedJson.has("pokemonConfigs") ?
                mergedJson.get("pokemonConfigs").getAsJsonObject() : new JsonObject();
        mergedJson.add("pokemonConfigs", mergedPokemonConfigs);

        JsonObject userPokemonConfigs = null;
        if (userConfigJson != null && userConfigJson.has("pokemonConfigs") && userConfigJson.get("pokemonConfigs").isJsonObject()) {
            userPokemonConfigs = userConfigJson.get("pokemonConfigs").getAsJsonObject();
        }

        JsonObject defaultSpecificConfig = GSON.toJsonTree(defaultPokemonConfig).getAsJsonObject();
        if (userPokemonConfigs != null && userPokemonConfigs.has(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME) &&
                userPokemonConfigs.get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME).isJsonObject()) {

            mergeJsonObjects(defaultSpecificConfig, userPokemonConfigs.get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME).getAsJsonObject());
        }
        mergedPokemonConfigs.add(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME, defaultSpecificConfig);

        if (userPokemonConfigs != null) {
            for (Map.Entry<String, JsonElement> entry : userPokemonConfigs.entrySet()) {
                String pokemonName = entry.getKey();

                if (pokemonName.equals(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME)) {
                    continue;
                }

                JsonElement userSpecificConfigElement = entry.getValue();
                if (userSpecificConfigElement.isJsonObject()) {
                    JsonObject userSpecificConfig = userSpecificConfigElement.getAsJsonObject();
                    JsonObject specificPokemonDefault = GSON.toJsonTree(PokemonConfig.PokemonSpecificConfig.createDefault()).getAsJsonObject();

                    mergeJsonObjects(specificPokemonDefault, userSpecificConfig);
                    mergedPokemonConfigs.add(pokemonName, specificPokemonDefault);
                } else {
                    CobblemonSpawnAlerts.LOGGER.warn("Invalid entry for Pokemon '"+pokemonName+"' in config file `"+fileName+"`. Skipping.");
                }
            }
        }
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
