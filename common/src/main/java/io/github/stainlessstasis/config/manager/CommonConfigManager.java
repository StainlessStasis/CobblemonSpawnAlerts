package io.github.stainlessstasis.config.manager;

import io.github.stainlessstasis.config.common.DiscordWebhookConfig;
import io.github.stainlessstasis.config.common.RaritiesConfig;
import io.github.stainlessstasis.config.common.ServerConfig;

import java.io.File;

public class CommonConfigManager extends AbstractConfigManager {
    private final File SERVER_CONFIG_FILE = MOD_CONFIG_DIR.resolve("server.json").toFile();
    private final File RARITIES_CONFIG_FILE = MOD_CONFIG_DIR.resolve("rarities.json").toFile();
    private final File DISCORD_CONFIG_FILE = MOD_CONFIG_DIR.resolve("webhooks.json").toFile();
    private ServerConfig serverConfig;
    private RaritiesConfig raritiesConfig;
    private DiscordWebhookConfig discordConfig;

    @Override
    boolean onConfigLoad() {
        serverConfig = loadConfigFile(SERVER_CONFIG_FILE, ServerConfig.class);
        if (serverConfig == null) {
            failedLoad(SERVER_CONFIG_FILE.toPath());
            return false;
        }
        raritiesConfig = loadConfigFile(RARITIES_CONFIG_FILE, RaritiesConfig.class);
        if (raritiesConfig == null) {
            failedLoad(RARITIES_CONFIG_FILE.toPath());
            return false;
        }
        discordConfig = loadConfigFile(DISCORD_CONFIG_FILE, DiscordWebhookConfig.class);
        if (discordConfig == null) {
            failedLoad(DISCORD_CONFIG_FILE.toPath());
            return false;
        }
        return true;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
    public RaritiesConfig getRaritiesConfig() {
        return raritiesConfig;
    }
    public DiscordWebhookConfig getDiscordConfig() {
        return discordConfig;
    }
}
