package io.github.stainlessstasis.cobblemon_spawn_alerts.compat;

import com.n1netails.n1netails.discord.api.DiscordWebhookClient;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.service.WebhookService;
import io.github.stainlessstasis.cobblemon_spawn_alerts.config.client.PokemonConfig;
import io.github.stainlessstasis.cobblemon_spawn_alerts.config.common.DiscordWebhookConfig;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.AlertDataPacket;

import java.util.concurrent.CompletableFuture;

public class DiscordWebhookService {
    private final DiscordWebhookClient webhookClient;

    public DiscordWebhookService() {
        this.webhookClient = new DiscordWebhookClientImpl(new WebhookService());
    }

    public void sendWebhook(AlertDataPacket alertData, PokemonConfig.PokemonSpecificConfig pokemonConfig) {
        CompletableFuture.runAsync(() -> {
            DiscordWebhookConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getDiscordConfig();
            String url = config.webhookURL();
            var unconvertedWebhook = config.webhookContent();
            unconvertedWebhook = unconvertedWebhook.applyDynamicReplacements(alertData, pokemonConfig);
            var webhook = unconvertedWebhook.convert();

            try {
                webhookClient.sendMessage(url, webhook);
            } catch (DiscordWebhookException e) {
                CobblemonSpawnAlerts.LOGGER.error("Cannot send Discord webhook: {}\n{}", e.getMessage(), e.getCause());
            }
        });
    }
}
