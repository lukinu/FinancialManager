package com.company.model;

import java.util.ArrayList;
import java.util.Set;

/*
* Data access interface
* */
public interface DataStore {
    // return null if no such user
    User getUser(String name);

    // If no users, return empty collection (not null)
    Set<String> getUserNames();

    // If no accounts, return empty collection (not null)
    Set<Account> getAccounts(User owner);

    // If no records, return empty collection (not null)
    ArrayList<Record> getRecords(Account account);

    // get a record by its ID
    Record getRecordById(int recId);

    // delete a record by its ID
    boolean deleteRecordById(int recId);

    void addUser(User user);

    int addAccount(User user, String desc);

    int addRecord(Account account, Record record);

    // return removed User or null if no such user
    User removeUser(String name);

    // return null if no such account
    Account removeAccount(User owner, Account account);

    // return null if no such record
    Record removeRecord(Account from, Record record);

    // update a record
    boolean updateRecord(Account account, int recordId, Record newRecordVal);

    Account getAccountById(int recId);
}
