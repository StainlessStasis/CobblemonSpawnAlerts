package io.github.stainlessstasis.config.common;


import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

public record DiscordWebhookConfig(
        String configVersion,
        String[] comment,
        String webhookURL,
        DiscordWebhookWrappers.WebhookContent webhookContent
) {

    public static DiscordWebhookConfig createDefault() {
        return new DiscordWebhookConfig(
            CobblemonSpawnAlerts.MOD_VERSION,
            new String[]{
                    "This config is common between server and client. It creates Discord webhooks for alerts.",
                    "Servers only reference this config for global alerts. Clients use this for every alert, if webhooks are enabled for the alert.",
                    "For details on using the config, please see the docs.",
                    "https://stainlessstasis.github.io/CSA-Docs/config/"
            },
            "YOUR WEBHOOK URL HERE",
            DiscordWebhookWrappers.WebhookContent.createDefault()
        );
    }
}
