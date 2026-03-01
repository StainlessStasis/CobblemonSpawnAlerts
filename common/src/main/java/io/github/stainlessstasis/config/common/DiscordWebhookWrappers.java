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
    private static final String DEFAULT_CONTENT = "No message provided. This cannot be blank, unless your webhook has an embed(s).";

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
                    DEFAULT_CONTENT, "", "", false, List.of(Embed.createDefault())
            );
        }

        public WebhookContent applyDynamicReplacements(AlertDataPacket alertData) {
            return applyDynamicReplacements(alertData, null);
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
                        .withTitle(embed.title().isEmpty() ? null : addTimestamp(embed.title()))
                        .withDescription(embed.description().isEmpty() ? null : addTimestamp(embed.description()))
                        .withUrl(embed.url().isEmpty() ? null : addTimestamp(embed.url()));

                if (!embed.imageURL().isEmpty()) {
                    var img = new com.n1netails.n1netails.discord.model.Embed.Image();
                    img.setUrl(addTimestamp(embed.imageURL()));
                    embedBuilder.withImage(img);
                }

                if (!embed.thumbnailURL().isEmpty()) {
                    var thumbnail = new com.n1netails.n1netails.discord.model.Embed.Thumbnail();
                    thumbnail.setUrl(addTimestamp(embed.thumbnailURL()));
                    embedBuilder.withThumbnail(thumbnail);
                }

                if (embed.timestamp()) {
                    embedBuilder.withTimestamp(java.time.Instant.now().toString());
                }

                if (!embed.author().name().isEmpty()) {
                    var author = new com.n1netails.n1netails.discord.model.Embed.Author();
                    author.setName(addTimestamp(embed.author().name()));
                    author.setUrl(embed.author().url().isEmpty() ? null : addTimestamp(embed.author().url()));
                    author.setIcon_url(embed.author().iconURL().isEmpty() ? null : addTimestamp(embed.author().iconURL()));
                    embedBuilder.withAuthor(author);
                }

                List<com.n1netails.n1netails.discord.model.Embed.EmbedField> fields = new ArrayList<>();
                for (EmbedField f : embed.fields()) {
                    if (f.name().isEmpty() || f.value().isEmpty()) continue;

                    var field = new com.n1netails.n1netails.discord.model.Embed.EmbedField();
                    field.setName(addTimestamp(f.name()));
                    field.setValue(addTimestamp(f.value()));
                    field.setInline(f.inline());
                    fields.add(field);
                }
                if (!fields.isEmpty()) embedBuilder.withFields(fields);

                if (!embed.footer().text().isEmpty()) {
                    var footer = new com.n1netails.n1netails.discord.model.Embed.Footer();
                    footer.setText(addTimestamp(embed.footer().text()));
                    footer.setIcon_url(embed.footer().iconURL().isEmpty() ? null : addTimestamp(embed.footer().iconURL()));
                    embedBuilder.withFooter(footer);
                }

                if (!embed.color().isEmpty()) {
                    embedBuilder.withColor(embed.color());
                }

                embeds.add(embedBuilder.build());
            }

            var builder = new WebhookMessageBuilder()
                    .withContent(this.content().isEmpty() ? null : addTimestamp(this.content()))
                    .withAvatarUrl(this.avatarURL().isEmpty() ? null : addTimestamp(this.avatarURL()))
                    .withTts(this.tts());

            if (!this.username().isEmpty()) builder.withUsername(addTimestamp(this.username()));
            if (!embeds.isEmpty()) builder.withEmbeds(embeds);

            return builder.build();
        }

        private static String addTimestamp(String string) {
            return string.replace("{timestamp}", String.valueOf(System.currentTimeMillis()/1000));
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
                false, "", "", "", "", "", "", false,
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
