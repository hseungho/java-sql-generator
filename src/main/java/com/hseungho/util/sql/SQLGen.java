package com.hseungho.util.sql;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import static com.hseungho.util.sql.SQLGenUtils.isNan;
import static com.hseungho.util.sql.SQLGenUtils.isNotBlank;
import static com.hseungho.util.sql.SQLGenValidator.requireNotBlank;
import static com.hseungho.util.sql.SQLGenValidator.requireNotEmpty;
import static com.hseungho.util.sql.SQLGenValidator.requireNotNull;

public class SQLGen {

    private String schema;
    private String table;
    private String[] properties;
    private Collection<Object> models;
    private boolean isUpperCase = false;

    private SQLGen() {}

    private void setSchema(String schema) {
        this.schema = schema;
    }

    private void setTable(String table) {
        this.table = table;
    }

    private void setProperties(String[] properties) {
        this.properties = properties.clone();
    }

    private <T> void setModels(Collection<T> models) {
        this.models = Collections.unmodifiableCollection(models);
    }

    private void setUpperCase(boolean isUpperCase) {
        this.isUpperCase = isUpperCase;
    }

    private SQLGen build() {
        checkProperties();

        if (this.isUpperCase) {
            if (isNotBlank(this.schema)) {
                this.schema = this.schema.toUpperCase();
            }
            this.table = this.table.toUpperCase();
        }
        return this;
    }

    private String getTableName() {
        return schema == null || schema.isBlank() ? this.table : this.schema+"."+this.table;
    }

    private void requireAttributes() {
        requireNotBlank("table", this.table);
        requireNotNull("properties", this.properties);
        requireNotNull("model list", this.models);
        requireNotEmpty("properties", this.properties);
        requireNotEmpty("model list", this.models.toArray());
    }

    private void checkProperties() {
        requireAttributes();

        this.models.forEach(model -> {
            List<String> fieldNames =
                    Arrays.stream(model.getClass().getDeclaredFields()).map(Field::getName).toList();
            Arrays.stream(this.properties).forEach( property -> {
                if (!fieldNames.contains(property.toLowerCase())) {
                    throw new SQLGenException(String.format("%s property is not defined inside the %s class.",
                            property, model.getClass().getSimpleName()));
                }
            });
        });
    }

    public SQLResult insert() {
        SQLResult result = new SimpleSQLResult();

        String insert = this.isUpperCase ?
                "INSERT INTO %s (%s) VALUES (%s);" :
                "insert into %s (%s) values (%s);";
        String tableValue = this.getTableName();
        String propertiesValue = String.join(", ", this.properties);

        this.models.forEach(model -> {
            StringJoiner modelValue = new StringJoiner(", ");
            Arrays.stream(this.properties).forEach(property -> {
                String value = this.getAttrValueAsString(model, property);
                modelValue.add(value);
            });
            result.addResult(String.format(insert, tableValue, propertiesValue, modelValue));
        });

        return result;
    }

    public SQLResult updateBy(String by, String... targetProperties) {
        SQLResult result = new SimpleSQLResult();

        String update = this.isUpperCase ?
                "UPDATE %s SET %s WHERE %s;" :
                "update %s set %s where %s;";
        String tableValue = this.getTableName();

        this.models.forEach(model -> {
            String setForm = "%s = %s";
            StringJoiner setValue = new StringJoiner(", ");

            Arrays.stream(targetProperties).forEach(property -> {
                String value = this.getAttrValueAsString(model, property);
                setValue.add(String.format(setForm, property, value));
            });

            String conditionFrom = "%s = %s";
            String value = this.getAttrValueAsString(model, by);
            String condition = String.format(conditionFrom, by, value);

            result.addResult(String.format(update, tableValue, setValue, condition));
        });

        return result;
    }

    private <T> String getAttrValueAsString(T clazz, String attrName) {
        try {
            Field field = clazz.getClass().getDeclaredField(attrName);
            Object obj = field.get(clazz);
            return isNan(obj) ? "'" + obj + "'" : obj.toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SQLGenException(e);
        }
    }

    public static SQLGenBuilder builder() {
        return new SQLGenBuilder();
    }

    public static class SQLGenBuilder {

        private final SQLGen generator;

        private SQLGenBuilder() {
            generator = new SQLGen();
        }

        public SQLGen build() {
            return generator.build();
        }

        public SQLGenBuilder withSchema(String schema) {
            generator.setSchema(schema);
            return this;
        }

        public SQLGenBuilder withTable(String table) {
            generator.setTable(table);
            return this;
        }

        public SQLGenBuilder withProperties(String... properties) {
            generator.setProperties(properties);
            return this;
        }

        public <T> SQLGenBuilder withModels(List<T> models) {
            generator.setModels(models);
            return this;
        }

        public SQLGenBuilder enableUpperCase() {
            generator.setUpperCase(true);
            return this;
        }
    }
}
