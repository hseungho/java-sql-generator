package com.navi.util.sql;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLGenTest {

    @Test
    void insert_succeed() {
        // given
        var size = 10;
        var models = givenModels(size);
        SQLGen generator = SQLGen.builder()
                .withSchema("test")
                .withTable("model")
                .withProperties("id", "name", "x", "y", "z")
                .withModels(models)
                .enableUpperCase()
                .build();
        // when
        SQLResult result = generator.insert();
        // then
        assertEquals(size, result.size());
    }

    @Test
    void updateById_succeed() {
        // given
        var size = 10;
        var models = givenModels(size);
        SQLGen generator = SQLGen.builder()
                .withTable("model")
                .withProperties("id", "name", "x", "y", "z")
                .withModels(models)
                .enableUpperCase()
                .build();
        // when
        SQLResult result = generator.updateBy("id", "name", "z");
        // then
        assertEquals(size, result.size());
    }

    @Test
    void givenNoTable_whenBuild_thenFailed() {
        // given
        var models = givenDefaultModels();
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withProperties("id", "name", "x", "y", "z")
                        .withModels(models)
                        .build()
        );
        assertTrue(ex.message().contains("table"));
    }

    @Test
    void givenNullProperties_whenBuild_thenFailed() {
        // given
        var models = givenDefaultModels();
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withTable("table")
                        .withModels(models)
                        .build()
        );
        assertTrue(ex.message().contains("properties"));
    }

    @Test
    void givenNullModels_whenBuild_thenFailed() {
        // given
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withTable("table")
                        .withProperties("id", "name", "x", "y", "z")
                        .build()
        );
        assertTrue(ex.message().contains("model list"));
    }

    @Test
    void givenEmptyProperties_whenBuild_thenFailed() {
        // given
        var models = givenDefaultModels();
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withTable("table")
                        .withProperties()
                        .withModels(models)
                        .build()
        );
        assertTrue(ex.message().contains("properties"));
    }

    @Test
    void givenEmptyModels_whenBuild_thenFailed() {
        // given
        var models = new ArrayList<>();
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withTable("table")
                        .withProperties("id", "name", "x", "y", "z")
                        .withModels(models)
                        .build()
        );
        assertTrue(ex.message().contains("model list"));
    }

    @Test
    void givenMismatchProperties_whenBuild_thenFailed() {
        // given
        var models = givenDefaultModels();
        // when
        // then
        var ex = assertThrows(
                SQLGenException.class,
                () -> SQLGen.builder()
                        .withTable("table")
                        .withProperties("id", "name", "wrong")
                        .withModels(models)
                        .build()
        );
        assertTrue(ex.message().contains("wrong"));
    }

    private List<Model> givenDefaultModels() {
        return givenModels(10);
    }

    private List<Model> givenModels(int size) {
        List<Model> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new Model(i, "name_"+i, "x"+i, "y"+i, new Random().nextDouble()));
        }
        return result;
    }

    private static class Model {
        int id;
        String name;
        String x;
        String y;
        Double z;

        Model(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Model(int id, String name, String x, String y, Double z) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "Model {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
