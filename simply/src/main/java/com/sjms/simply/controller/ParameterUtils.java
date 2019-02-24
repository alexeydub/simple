package com.sjms.simply.controller;

/**
 * Utils.
 */
public class ParameterUtils {

    /**
     * Check if str is empty.
     * 
     * @param str string to check
     * @return true if str is null or empty
     */
    public static boolean isEmpty(String str) {
        return (str == null) || str.trim().isEmpty();
    }

    /**
     * Parse string to int.
     *
     * @param str        string to parse
     * @param defaultVal default value for non-numeric str
     * @return parsed string or default value
     */
    public static Integer parseIntDefault(String str, Integer defaultVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignore) {
            // return default
        }
        return defaultVal;
    }

    /**
     * Parse string to long.
     *
     * @param str        string to parse
     * @param defaultVal default value for non-numeric str
     * @return parsed string or default value
     */
    public static Long parseLongDefault(String str, Long defaultVal) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ignore) {
            // return default
        }
        return defaultVal;
    }

}
