package com.hseungho.util.sql;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleSQLResultTest {

    @Test
    void createFileTest() {
        // given
        var sqlResult = new SimpleSQLResult();
        for (int i = 0; i < 10; i++) {
            sqlResult.addResult("insert into <table> (id) values ('"+i+"');");
        }
        // when
        var success = sqlResult.createFile("./test.sql");
        // then
        assertTrue(success);

        var file = new File("./test.sql");
        var delete = file.delete();
        assertTrue(delete);
    }

}