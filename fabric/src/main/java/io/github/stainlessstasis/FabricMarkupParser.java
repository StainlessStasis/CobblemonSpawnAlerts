package io.github.stainlessstasis;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.tysontheember.emberstextapi.immersivemessages.api.MarkupParser;
import net.tysontheember.emberstextapi.immersivemessages.api.TextSpan;
import net.tysontheember.emberstextapi.util.StyleUtil;

import java.util.List;

/// Client only
public class FabricMarkupParser {
    public static MutableComponent parseMarkup(String markup) {
        List<TextSpan> spans = MarkupParser.parse(markup);
        MutableComponent result = Component.empty();
        for (TextSpan span : spans) {
            // applyTextSpanFormatting handles bold/italic/effects but intentionally skips color
            Style style = StyleUtil.applyTextSpanFormatting(Style.EMPTY, span);
            if (span.getColor() != null) {
                style = style.withColor(span.getColor());
            }
            result.append(Component.literal(span.getContent()).withStyle(style));
        }
        return result;
    }
}
