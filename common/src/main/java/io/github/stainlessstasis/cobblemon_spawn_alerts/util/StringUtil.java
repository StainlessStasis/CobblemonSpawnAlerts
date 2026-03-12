package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    // yeah i hardcoded it to use underscore but idgaf
    public static String capitalizeEachWord(String string) {
        String[] words = string.split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(capitalize(word));
            }

            if (i < words.length - 1) {
                result.append("_");
            }
        }
        return result.toString();
    }

    public static String makeBeautiful(String string) {
        string = capitalizeEachWord(string);
        string = string.replace("_", " ");
        string = string.replace("-", " ");
        return string;
    }

    public static String[] splitIdentifier(String identifier) {
        if (!identifier.contains(":")) {
            return new String[]{"NO NAMESPACE", ""};
        }
        int i = identifier.indexOf(":");
        String namespace = identifier.substring(0, i);
        String path = identifier.substring(i+1);
        return new String[]{namespace, path};
    }

    public static List<String> splitTranslationKey(String key) {
        String[] split = key.split("\\.");
        return Arrays.stream(split).toList();
    }
}
