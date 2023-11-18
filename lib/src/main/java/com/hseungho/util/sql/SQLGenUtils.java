package com.hseungho.util.sql;

class SQLGenUtils {

    private SQLGenUtils() {}

    static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    static boolean isNotBlank(String value) {
        return !isNullOrBlank(value);
    }

    static boolean isNan(Object obj) {
        if (obj instanceof Number) {
            double value = ((Number) obj).doubleValue();
            return Double.isNaN(value) || Double.isInfinite(value);
        }
        return true;
    }
}
