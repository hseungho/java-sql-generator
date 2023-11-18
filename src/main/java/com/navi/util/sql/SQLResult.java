package com.navi.util.sql;

public interface SQLResult {

    int size();

    void addResult(String statement);

    void print();

}
