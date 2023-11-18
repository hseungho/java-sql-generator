package com.hseungho.util.sql;

class SQLGenValidator {

    private SQLGenValidator() {}

    static void requireNotNull(String name, Object any) {
        if (any == null) {
            throw new SQLGenException(String.format("%s cannot be null.", name));
        }
    }

    static void requireNotEmpty(String name, Object[] arr) {
        if (arr.length == 0) {
            throw new SQLGenException(String.format("%s cannot be empty.", name));
        }
    }

    static void requireNotBlank(String name, String value) {
        requireNotNull(name, value);
        if (value.isBlank()) {
            throw new SQLGenException(String.format("%s cannot be blank.", name));
        }
    }
}
