package io.github.stainlessstasis.util;

public class StringUtil {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
