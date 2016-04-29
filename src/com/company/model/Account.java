package com.company.model;

import java.util.ArrayList;

/*
* Class represents a user's account
* */
public class Account {
    private String description;
    private int id;
    private int balance;
    private ArrayList<Record> records;

    public Account(int id, String description) {
        this.description = description;
        this.id = id;
        records = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void addRecord(Record record) {
        records.add(record);
    }
}
