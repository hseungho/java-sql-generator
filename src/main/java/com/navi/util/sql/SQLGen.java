package com.navi.util.sql;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import static com.navi.util.sql.SQLGenValidator.requireNotBlank;
import static com.navi.util.sql.SQLGenValidator.requireNotEmpty;
import static com.navi.util.sql.SQLGenValidator.requireNotNull;
import static com.navi.util.sql.SQLGenUtils.isNan;
import static com.navi.util.sql.SQLGenUtils.isNotBlank;
import static com.navi.util.sql.SQLGenUtils.upperCases;

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
            this.properties = upperCases(this.properties);
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

        String form = "insert into %s (%s) values (%s);";
        String insert = this.isUpperCase ? form.toUpperCase() : form;
        String tableValue = this.getTableName();
        String propertiesValue = String.join(", ", this.properties);

        this.models.forEach(model -> {
            StringJoiner modelValue = new StringJoiner(", ");
            Arrays.stream(this.properties).forEach(_property -> {
                try {
                    String property = _property.toLowerCase();
                    Field field = model.getClass().getDeclaredField(property);
                    Object obj = field.get(model);
                    String value = isNan(obj) ? "'" + obj + "'" : obj.toString();
                    modelValue.add(value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new SQLGenException(e);
                }
            });
            result.addResult(String.format(insert, tableValue, propertiesValue, modelValue));
        });

        return result;
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
