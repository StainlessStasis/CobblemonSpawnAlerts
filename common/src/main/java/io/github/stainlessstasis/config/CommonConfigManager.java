package io.github.stainlessstasis.config;

import java.io.File;

public class CommonConfigManager extends AbstractConfigManager {
    private final File SERVER_CONFIG_FILE = MOD_CONFIG_DIR.resolve("server.json").toFile();
    private ServerConfig serverConfig;

    @Override
    boolean onConfigLoad() {
        serverConfig = loadConfigFile(SERVER_CONFIG_FILE, ServerConfig.class);
        if (serverConfig == null) {
            failedLoad(SERVER_CONFIG_FILE.toPath());
            return false;
        }
        return true;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
