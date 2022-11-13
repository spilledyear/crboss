package com.lxy.tools.crboss.utils;

import java.nio.charset.StandardCharsets;

public class StringUtil {
    public static String utf8(String str) {
        return new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public static boolean isNotBlank(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }
}
