package io.github.stainlessstasis.compat;

import com.n1netails.n1netails.discord.api.DiscordWebhookClient;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.discord.service.WebhookService;
import io.github.stainlessstasis.config.common.DiscordWebhookConfig;
import io.github.stainlessstasis.config.common.DiscordWebhookWrappers;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.AlertDataPacket;

import java.util.concurrent.CompletableFuture;

public class DiscordWebhookService {
    private final DiscordWebhookClient webhookClient;

    public DiscordWebhookService() {
        this.webhookClient = new DiscordWebhookClientImpl(new WebhookService());
    }

    public void sendWebhook(AlertDataPacket alertData) {
        CompletableFuture.runAsync(() -> {
            DiscordWebhookConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getDiscordConfig();
            String url = config.webhookURL();
            var webhook = config.webhookContent().convert();

            // replace with your discord webhook url
            try {
                webhookClient.sendMessage(url, webhook);
            } catch (DiscordWebhookException e) {
                CobblemonSpawnAlerts.LOGGER.error("Cannot send Discord webhook: {}\n{}", e.getMessage(), e.getCause());
            }
        });
    }
}
