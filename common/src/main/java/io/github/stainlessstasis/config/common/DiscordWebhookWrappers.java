package io.github.stainlessstasis.config.common;

import com.n1netails.n1netails.discord.model.EmbedBuilder;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.discord.model.WebhookMessageBuilder;
import io.github.stainlessstasis.alert.DynamicReplacements;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.platform.Services;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DiscordWebhookWrappers {
    private static String parseDynamicReplacements(String message, @Nullable PokemonConfig.PokemonSpecificConfig pokemonSpecificConfig, AlertDataPacket alertData) {
        return Services.PLATFORM.parseMarkupAsString(DynamicReplacements.applyDynamicReplacements(message, pokemonSpecificConfig, alertData, new StringBuilder()));
    }

    public record WebhookContent(
            String content,
            String username,
            String avatarURL,
            boolean tts,
            List<Embed> embeds
    ) {
        public static WebhookContent createDefault() {
            return new WebhookContent(
                    "No message provided. This cannot be blank, unless your webhook has an embed(s).",
                    "", "", false, List.of(Embed.createDefault())
            );
        }

        public WebhookContent applyDynamicReplacements(AlertDataPacket alertData, @Nullable PokemonConfig.PokemonSpecificConfig pokemonConfig) {
            String newContent = parseDynamicReplacements(this.content, pokemonConfig, alertData);
            String newUsername = parseDynamicReplacements(this.username, pokemonConfig, alertData);
            String newAvatar = parseDynamicReplacements(this.avatarURL, pokemonConfig, alertData);

            List<Embed> newEmbeds = this.embeds.stream()
                    .map(e -> e.applyDynamicReplacements(alertData, pokemonConfig))
                    .toList();

            return new WebhookContent(newContent, newUsername, newAvatar, this.tts, newEmbeds);
        }

        public WebhookMessage convert() {
            List<com.n1netails.n1netails.discord.model.Embed> embeds = new ArrayList<>();

            for (Embed embed : this.embeds()) {
                if (!embed.enabled) continue;

                var embedBuilder = new EmbedBuilder()
                        .withTitle(embed.title().isEmpty() ? null : embed.title())
                        .withDescription(embed.description().isEmpty() ? null : embed.description())
                        .withUrl(embed.url().isEmpty() ? null : embed.url());

                if (!embed.imageURL().isEmpty()) {
                    var img = new com.n1netails.n1netails.discord.model.Embed.Image();
                    img.setUrl(embed.imageURL());
                    embedBuilder.withImage(img);
                }

                if (!embed.thumbnailURL().isEmpty()) {
                    var thumbnail = new com.n1netails.n1netails.discord.model.Embed.Thumbnail();
                    thumbnail.setUrl(embed.thumbnailURL());
                    embedBuilder.withThumbnail(thumbnail);
                }

                if (embed.timestamp()) {
                    embedBuilder.withTimestamp(java.time.Instant.now().toString());
                }

                if (!embed.author().name().isEmpty()) {
                    var author = new com.n1netails.n1netails.discord.model.Embed.Author();
                    author.setName(embed.author().name());
                    author.setUrl(embed.author().url().isEmpty() ? null : embed.author().url());
                    author.setIcon_url(embed.author().iconURL().isEmpty() ? null : embed.author().iconURL());
                    embedBuilder.withAuthor(author);
                }

                List<com.n1netails.n1netails.discord.model.Embed.EmbedField> fields = new ArrayList<>();
                for (EmbedField f : embed.fields()) {
                    if (f.name().isEmpty() || f.value().isEmpty()) continue;

                    var field = new com.n1netails.n1netails.discord.model.Embed.EmbedField();
                    field.setName(f.name());
                    field.setValue(f.value());
                    field.setInline(f.inline());
                    fields.add(field);
                }
                if (!fields.isEmpty()) embedBuilder.withFields(fields);

                if (!embed.footer().text().isEmpty()) {
                    var footer = new com.n1netails.n1netails.discord.model.Embed.Footer();
                    footer.setText(embed.footer().text());
                    footer.setIcon_url(embed.footer().iconURL().isEmpty() ? null : embed.footer().iconURL());
                    embedBuilder.withFooter(footer);
                }

                if (!embed.color().isEmpty()) {
                    embedBuilder.withColor(embed.color());
                }

                embeds.add(embedBuilder.build());
            }

            var builder = new WebhookMessageBuilder()
                    .withContent(this.content().isEmpty() ? null : this.content())
                    .withAvatarUrl(this.avatarURL().isEmpty() ? null : this.avatarURL())
                    .withTts(this.tts());

            if (!this.username().isEmpty()) builder.withUsername(this.username());
            if (!embeds.isEmpty()) builder.withEmbeds(embeds);

            return builder.build();
        }

    }

    public record Embed(
            boolean enabled,
            String title,
            String description,
            String color,
            String url,
            String imageURL,
            String thumbnailURL,
            boolean timestamp,
            Author author,
            List<EmbedField> fields,
            Footer footer
    ) {
        public static Embed createDefault() {
            return new Embed(
                true, "**A {shiny_unformatted}{legendary_unformatted}{HA_unformatted}{bucket_unformatted}{name} spawned in a {biome_unformatted} biome!**",
                    "**Dex Number**: #{dex_unformatted}\n**Level**: {level_unformatted}\n**IVs**: {ivs_unformatted}\n**EVs**: {evs_unformatted}\n**Nature**: {nature_unformatted}\n**Ability**: {ability_unformatted}\n**Gender**: {gender_unformatted}\n**Coordinates**: {coords_unformatted}\n**Nearest Player**: {nearest_player_unformatted}",
                    "", "", "",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{dex_unformatted}.png",
                    true,
                Author.createDefault(),
                List.of(EmbedField.createDefault()),
                Footer.createDefault()
            );
        }

        public Embed applyDynamicReplacements(AlertDataPacket alertData, @Nullable PokemonConfig.PokemonSpecificConfig config) {
            return new Embed(
                    this.enabled,
                    parseDynamicReplacements(this.title, config, alertData),
                    parseDynamicReplacements(this.description, config, alertData),
                    this.color,
                    parseDynamicReplacements(this.url, config, alertData),
                    parseDynamicReplacements(this.imageURL, config, alertData),
                    parseDynamicReplacements(this.thumbnailURL, config, alertData),
                    this.timestamp,
                    this.author.applyDynamicReplacements(alertData, config),
                    this.fields.stream().map(f -> f.applyDynamicReplacements(alertData, config)).toList(),
                    this.footer.applyDynamicReplacements(alertData, config)
            );
        }
    }

    public record Author(
            String name,
            String url,
            String iconURL
    ) {
        public static Author createDefault() {
            return new Author("", "", "");
        }

        public Author applyDynamicReplacements(AlertDataPacket alertData, @Nullable PokemonConfig.PokemonSpecificConfig config) {
            return new Author(
                    parseDynamicReplacements(this.name, config, alertData),
                    parseDynamicReplacements(this.url, config, alertData),
                    parseDynamicReplacements(this.iconURL, config, alertData)
            );
        }
    }

    public record EmbedField(
            String name,
            String value,
            boolean inline
    ) {
        public static EmbedField createDefault() {
            return new EmbedField("", "", false);
        }

        public EmbedField applyDynamicReplacements(AlertDataPacket alertData, @Nullable PokemonConfig.PokemonSpecificConfig config) {
            return new EmbedField(
                    parseDynamicReplacements(this.name, config, alertData),
                    parseDynamicReplacements(this.value, config, alertData),
                    this.inline
            );
        }
    }

    public record Footer(
            String text,
            String iconURL
    ) {
        public static Footer createDefault() {
            return new Footer("", "");
        }

        public Footer applyDynamicReplacements(AlertDataPacket alertData, @Nullable PokemonConfig.PokemonSpecificConfig config) {
            return new Footer(
                    parseDynamicReplacements(this.text, config, alertData),
                    parseDynamicReplacements(this.iconURL, config, alertData)
            );
        }
    }
}
