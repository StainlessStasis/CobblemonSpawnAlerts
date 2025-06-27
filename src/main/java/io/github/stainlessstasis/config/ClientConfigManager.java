package io.github.stainlessstasis.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.*;

@Environment(EnvType.CLIENT)
public class ClientConfigManager extends AbstractConfigManager {
    private final File MAIN_CONFIG_FILE = MOD_CONFIG_DIR.resolve("main.json").toFile();
    private final File POKEMON_CONFIG_FILE = MOD_CONFIG_DIR.resolve("pokemon.json").toFile();
    private final File MESSAGE_TEMPLATES_FILE = MOD_CONFIG_DIR.resolve("message_templates.json").toFile();
    private MainConfig mainConfig;
    private PokemonConfig pokemonConfig;
    private MessageTemplates messageTemplates;

    @Override
    void onConfigLoad() {
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
}
