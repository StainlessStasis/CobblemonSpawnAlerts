package io.github.stainlessstasis.config.common;

import com.n1netails.n1netails.discord.model.EmbedBuilder;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.discord.model.WebhookMessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class DiscordWebhookWrappers {
    private static String DEFAULT_CONTENT = "No message provided. This cannot be blank, unless your webhook has an embed(s).";

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
    }

    public record Author(
            String name,
            String url,
            String iconURL
    ) {
        public static Author createDefault() {
            return new Author("", "", "");
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
    }

    public record Footer(
            String text,
            String iconURL
    ) {
        public static Footer createDefault() {
            return new Footer("", "");
        }
    }
}
