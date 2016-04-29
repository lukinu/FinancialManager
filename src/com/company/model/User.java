package com.company.model;

import java.util.ArrayList;
/*
* Class represents a user
* */
public class User {
    private String name;
    private String passwd;
    private ArrayList<Account> accounts;

    public User(String name, String passwordHash) {
        this.name = name;
        this.passwd = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
