package com.xin.sql.util;

/**
 * @author 497668869@qq.com
 * @since 1.0
 */
public class StringUtils {
    public static boolean isNotEmpty(String st) {
        return st != null && !st.isEmpty();
    }

    public static boolean isEmpty(String st) {
        return !isNotEmpty(st);
    }

    public static boolean isNotBlank(String st) {
        return isNotEmpty(st) && isNotEmpty(st.trim());
    }

    public static boolean isBlank(String st) {
        return !isNotBlank(st);
    }

    public static String repeat(String rep, int time) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < time; i++) {
            s.append(rep);
        }
        return s.toString();
    }
}
