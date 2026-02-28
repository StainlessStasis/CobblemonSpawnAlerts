package io.github.stainlessstasis.config.common;

import com.n1netails.n1netails.discord.model.EmbedBuilder;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.discord.model.WebhookMessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class DiscordWebhookWrappers {
    public record WebhookContent(
            String content,
            String username,
            String avatarURL,
            boolean tts,
            List<Embed> embeds
    ) {
        public static WebhookContent createDefault() {
            return new WebhookContent(
                    "", "", "", false, List.of(Embed.createDefault())
            );
        }

        public WebhookMessage convert() {
            List<com.n1netails.n1netails.discord.model.Embed> embeds = new ArrayList<>();
            this.embeds().forEach(embed -> {
                var image = new com.n1netails.n1netails.discord.model.Embed.Image();
                image.setUrl(embed.imageURL());

                var thumbnail = new com.n1netails.n1netails.discord.model.Embed.Thumbnail();
                thumbnail.setUrl(embed.thumbnailURL());

                String timestamp = embed.timestamp();
                timestamp = timestamp.replace("{timestamp}", String.valueOf(System.currentTimeMillis()));

                var author = new com.n1netails.n1netails.discord.model.Embed.Author();
                author.setName(embed.author().name());
                author.setUrl(embed.author().url());
                author.setIcon_url(embed.author().iconURL());

                List<com.n1netails.n1netails.discord.model.Embed.EmbedField> fields = new ArrayList<>();
                embed.fields().forEach(embedField -> {
                    var field = new com.n1netails.n1netails.discord.model.Embed.EmbedField();
                    field.setName(embedField.name());
                    field.setValue(embedField.value());
                    field.setInline(embedField.inline());
                    fields.addLast(field);
                });

                var footer = new com.n1netails.n1netails.discord.model.Embed.Footer();
                footer.setText(embed.footer().text());
                footer.setIcon_url(embed.footer().iconURL());

                var embedBuilder = new EmbedBuilder()
                        .withTitle(embed.title())
                        .withDescription(embed.description)
                        .withUrl(embed.url())
                        .withColor(embed.color())
                        .withImage(image)
                        .withThumbnail(thumbnail)
                        .withTimestamp(timestamp)
                        .withAuthor(author)
                        .withFields(fields)
                        .withFooter(footer);

                embeds.addLast(embedBuilder.build());
            });

            var builder = new WebhookMessageBuilder()
                    .withContent(this.content())
                    .withUsername(this.username())
                    .withAvatarUrl(this.avatarURL())
                    .withTts(this.tts())
                    .withEmbeds(embeds);
            return builder.build();
        }
    }

    public record Embed(
            String title,
            String description,
            String color,
            String url,
            String imageURL,
            String thumbnailURL,
            String timestamp,
            Author author,
            List<EmbedField> fields,
            Footer footer
    ) {
        public static Embed createDefault() {
            return new Embed(
                "", "", "", "", "", "", "",
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
