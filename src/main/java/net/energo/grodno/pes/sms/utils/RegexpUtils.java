package net.energo.grodno.pes.sms.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {
    private RegexpUtils() {
    }

    public static String findFirstMatch(String text, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        }
        return "";
    }
}
