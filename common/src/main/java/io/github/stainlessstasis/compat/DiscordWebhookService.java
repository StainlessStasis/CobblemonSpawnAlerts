package io.github.stainlessstasis.compat;

import com.n1netails.n1netails.discord.api.DiscordWebhookClient;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.service.WebhookService;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.config.common.DiscordWebhookConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.AlertDataPacket;

import java.util.concurrent.CompletableFuture;

public class DiscordWebhookService {
    private final DiscordWebhookClient webhookClient;

    public DiscordWebhookService() {
        this.webhookClient = new DiscordWebhookClientImpl(new WebhookService());
    }

    public void sendClientsideWebhook(AlertDataPacket alertData, PokemonConfig.PokemonSpecificConfig pokemonConfig) {
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

    public void sendServersideWebhook(AlertDataPacket alertData) {
        System.out.println("SERVER SENDING WEBHOOK");
        CompletableFuture.runAsync(() -> {
            DiscordWebhookConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getDiscordConfig();
            System.out.println("WEBHOOK CONFIG: "+config);
            String url = config.webhookURL();
            System.out.println("URL: "+url);
            var unconvertedWebhook = config.webhookContent();
            System.out.println("CONTENT BEFORE CONVERT: "+unconvertedWebhook);
            unconvertedWebhook = unconvertedWebhook.applyDynamicReplacements(alertData);
            System.out.println("CONTENT AFTER DRs: "+unconvertedWebhook);
            var webhook = unconvertedWebhook.convert();
            System.out.println("FINAL WEBHOOK");

            try {
                webhookClient.sendMessage(url, webhook);
            } catch (DiscordWebhookException e) {
                CobblemonSpawnAlerts.LOGGER.error("Cannot send Discord webhook: {}\n{}", e.getMessage(), e.getCause());
            }
        });
    }
}
