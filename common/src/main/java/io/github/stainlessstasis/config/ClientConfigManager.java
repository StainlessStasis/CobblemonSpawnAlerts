package io.github.stainlessstasis.config;

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
