package com.hseungho.util.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class SimpleSQLResult implements SQLResult {

    private final List<String> results;

    public SimpleSQLResult() {
        results = new ArrayList<>();
    }

    @Override
    public int size() {
        return results.size();
    }

    @Override
    public void addResult(String statement) {
        this.results.add(statement);
    }

    @Override
    public void print() {
        StringJoiner sj = new StringJoiner("\n");
        this.results.forEach(sj::add);
        System.out.println(sj);
    }

    @Override
    public boolean createFile(String path) {
        try {
            File file = new File(path);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false), 8 * 1024);

            String result = String.join("\n", this.results);
            bw.append(result);
            bw.flush();
            bw.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
