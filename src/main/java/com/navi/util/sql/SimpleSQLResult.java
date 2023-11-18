package com.navi.util.sql;

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

}
