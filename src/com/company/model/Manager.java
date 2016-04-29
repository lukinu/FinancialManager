package com.company.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
* Class implements application's logic by interacting with DB and with GUI controller
* */
public class Manager {

    public static final String COLUMN1_TITLE = "ID";
    public static final String COLUMN2_TITLE = "Description";
    public static final String COLUMN3_TITLE = "Amount";
    public static final String COLUMN4_TITLE = "Category";
    public static final String COLUMN5_TITLE = "Date";
    private static final int COL_ID = 1;
    private static final int COLD_AMOUNT = 2;
    private static final int COL_CAT = 3;

    private static final int COL_DATE = 4;
    public static final Logger logger = LoggerFactory.getLogger(DbDataStorage.class);
    private DataStore dbDataStorage = null;
    private User currentUser;

    public Manager(DataStore dbDataStorage) {
        this.dbDataStorage = dbDataStorage;
    }

    public boolean login(String userName, String passwordHash) {
        String storedHash;
        if (dbDataStorage.getUserNames().contains(userName)) {
            storedHash = dbDataStorage.getUser(userName).getPasswd();
            if (passwordHash.equals(storedHash)) {
                currentUser = new User(userName, passwordHash);
                logger.info("login successful");
                return true;
            } else {
                logger.info("login failed: wrong password");
                return false;
            }
        } else {
            logger.info("login failed: user doesn't exist");
            return false;
        }
    }

    public boolean createNewUser(String userName, String passwordHash) {
        dbDataStorage.addUser(new User(userName, passwordHash));
        logger.info("user " + userName + " created");
        return true;
    }

    public static byte[] getSHA256Hash(String string) {
        MessageDigest sha256 = null;
        byte[] stringBytes = new byte[0];
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            stringBytes = string.getBytes("UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] hash = sha256.digest(stringBytes);
        StringBuilder hashAsString = new StringBuilder();
        for (int i = 0; i < hash.length - 1; i++) {
            hashAsString.append(String.format("(byte)0x%X, ", hash[i]));
        }
        logger.debug(hashAsString.toString());
        return hash;
    }

    public Set<Account> getAccountList() {
        return dbDataStorage.getAccounts(currentUser);
    }

    public User getUser(String name) {
        return dbDataStorage.getUser(name);
    }

    public ArrayList<Record> getAccountRecords(String accountDesc) {
        return dbDataStorage.getRecords(getAccountByDesc(accountDesc));
    }

    public Account getAccountByDesc(String accountDesc) {
        Account account = null;
        Iterator<Account> it = dbDataStorage.getAccounts(currentUser).iterator();
        while (it.hasNext()) {
            account = it.next();
            if (account.getDescription().equals(accountDesc)) {
                return account;
            }
        }
        return account;
    }

    public boolean updateRecord(Map<String, Object> params) {
        int recId = (int) params.get("id");
        int col = (int) params.get("col");
        String newVal = (String) params.get("val");
        Account account = getAccountByDesc((String) params.get("acc"));
        Record oldRecord = dbDataStorage.getRecordById(recId);
        Record newRecord = null;
        switch (col) {
            case COL_ID:
                newRecord = new Record(recId, newVal, oldRecord.getCategory(), oldRecord.getAmount(), oldRecord.getDate());
                break;
            case COLD_AMOUNT:
                int intVal;
                try {
                    intVal = Integer.parseInt(newVal);
                } catch (NumberFormatException e) {
                    Manager.logger.debug(e.toString());
                    return false;
                }
                newRecord = new Record(recId, oldRecord.getDescription(), oldRecord.getCategory(), intVal, oldRecord.getDate());
                break;
            case COL_CAT:
                newRecord = new Record(recId, oldRecord.getDescription(), new Category(newVal), oldRecord.getAmount(), oldRecord.getDate());
                break;
            case COL_DATE:
                Date date;
                try {
                    date = Date.valueOf(newVal);
                } catch (IllegalArgumentException e) {
                    Manager.logger.debug(e.toString());
                    return false;
                }
                newRecord = new Record(recId, oldRecord.getDescription(), oldRecord.getCategory(), oldRecord.getAmount(), date);
                break;
            default:
        }
        return dbDataStorage.updateRecord(account, recId, newRecord);
    }

    public boolean deleteRecord(Map<String, Object> params) {
        return dbDataStorage.deleteRecordById((int) params.get("id"));
    }

    // returns an added row id
    public int addRecord(Map<String, Object> params) {
        return dbDataStorage.addRecord((Account) params.get("acc"), new Record(0, (String) params.get("desc"),
                (Category) params.get("cat"), (int) params.get("amount"), (Date) params.get("date")));
    }

    public Record getRecordById(int recId) {
        return dbDataStorage.getRecordById(recId);
    }

    // returns an added account id
    public int addAccount(Map<String, Object> params) {
        return dbDataStorage.addAccount(currentUser, (String) params.get("desc"));
    }

    public Account getAccountById(int accountId) {
        return dbDataStorage.getAccountById(accountId);
    }
}