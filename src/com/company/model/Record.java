package com.company.model;

import java.sql.Date;
/*
* Class represents a financial record
* */
public class Record {
    private int id;
    private String description;
    private Category category;
    private int amount;
    private Date date;

    public Record(int id, String description, Category category, int amount, Date date) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Record() {
        this.id = 0;
        this.description = "";
        this.category = new Category("");
        this.amount = 0;
        this.date = new Date(0L);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
