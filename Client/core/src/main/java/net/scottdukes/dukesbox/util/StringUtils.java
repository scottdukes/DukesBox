package net.scottdukes.dukesbox.util;

public class StringUtils {
    public static String after(String value, String prefix) {
        int index = value.indexOf(prefix);

        if (index == -1) {
            return value;
        }

        return value.substring(index + prefix.length());
    }
}
