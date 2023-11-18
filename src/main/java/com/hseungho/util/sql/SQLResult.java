package com.hseungho.util.sql;

public interface SQLResult {

    int size();

    void addResult(String statement);

    void print();

}
