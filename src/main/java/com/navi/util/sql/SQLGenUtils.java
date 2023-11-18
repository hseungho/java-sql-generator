package com.navi.util.sql;

class SQLGenUtils {

    private SQLGenUtils() {}

    static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    static boolean isNotBlank(String value) {
        return !isNullOrBlank(value);
    }

    static String[] upperCases(String[] values) {
        String[] upperCases = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            upperCases[i] = values[i].toUpperCase();
        }
        return upperCases.clone();
    }

    static boolean isNan(Object obj) {
        if (obj instanceof Number) {
            double value = ((Number) obj).doubleValue();
            return Double.isNaN(value) || Double.isInfinite(value);
        }
        return true;
    }
}
