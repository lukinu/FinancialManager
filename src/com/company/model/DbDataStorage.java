package com.company.model;

import com.company.controller.DbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/*
* A class provides access to SQL DB by implementing interface
* */
public class DbDataStorage implements DataStore {

    private static Logger logger = LoggerFactory.getLogger(DbDataStorage.class);
    private DbHelper dbHelper = DbHelper.getInstance();

    @Override
    public User getUser(String name) {
        User user = null;
        String passwd = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = dbHelper.getConn().prepareStatement("SELECT PASSWORD FROM USERS WHERE NAME=?");
            statement.setString(1, name);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passwd = resultSet.getString("PASSWORD");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbHelper.closeResource(statement);
        }
        if (passwd != null) {
            user = new User(name, passwd);
        }
        logger.debug("getUser(): " + user);
        return user;
    }

    @Override
    public Set<String> getUserNames() {
        Set<String> userNames = new HashSet<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = dbHelper.getConn().createStatement();
            resultSet = statement.executeQuery("SELECT NAME FROM USERS");
            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                userNames.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbHelper.closeResource(statement);
        }
        logger.debug("getUserNames(): " + userNames);
        return userNames;
    }

    @Override
    public HashSet<Account> getAccounts(User owner) {
        HashSet<Account> accounts = new HashSet<>();
        PreparedStatement statement_1 = null;
        ResultSet resultSet = null;
        try {
            statement_1 = dbHelper.getConn().prepareStatement("SELECT DESCR, ID FROM ACCOUNTS WHERE USER_NAME = ?");
            statement_1.setString(1, owner.getName());
            resultSet = statement_1.executeQuery();
            while (resultSet.next()) {
                Account account = new Account(resultSet.getInt("ID"), resultSet.getString("DESCR"));
                PreparedStatement statement_2 = dbHelper.getConn().prepareStatement("SELECT * FROM RECORDS WHERE ACCOUNT_ID = ?");
                statement_2.setInt(1, resultSet.getInt("ID"));
                ResultSet resultSet_2 = statement_2.executeQuery();
                while ((resultSet_2.next())) {
                    account.addRecord(new Record(resultSet_2.getInt("ID"), resultSet_2.getString("DESCR"),
                            new Category(resultSet_2.getString("CATEGORY")), resultSet_2.getInt("AMOUNT"), resultSet_2.getDate("RECDATE")));
                }
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbHelper.closeResource(statement_1);
        }
        logger.debug("getAccounts(): " + accounts);
        return accounts;
    }

    @Override
    public ArrayList<Record> getRecords(Account account) {
        ArrayList<Record> records = new ArrayList<>();
        try {
            PreparedStatement statement_1 = dbHelper.getConn().prepareStatement("SELECT ID FROM ACCOUNTS WHERE DESCR = ?");
            statement_1.setString(1, account.getDescription());
            ResultSet resultSet = statement_1.executeQuery();
            int accountId = resultSet.getInt("ID");
            PreparedStatement statement_2 = dbHelper.getConn().prepareStatement("SELECT * FROM RECORDS WHERE ACCOUNT_ID = ?");
            statement_2.setInt(1, accountId);
            ResultSet resultSet_2 = statement_2.executeQuery();
            while ((resultSet_2.next())) {
                records.add(new Record(resultSet_2.getInt("ID"), resultSet_2.getString("DESCR"),
                        new Category(resultSet_2.getString("CATEGORY")), resultSet_2.getInt("AMOUNT"), resultSet_2.getDate("RECDATE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public Record getRecordById(int recId) {
        Record record = null;
        try {
            PreparedStatement statement = dbHelper.getConn().prepareStatement("SELECT * FROM RECORDS WHERE ID = ?");
            statement.setInt(1, recId);
            ResultSet resultSet_2 = statement.executeQuery();
            while ((resultSet_2.next())) {
                record = new Record(resultSet_2.getInt("ID"), resultSet_2.getString("DESCR"),
                        new Category(resultSet_2.getString("CATEGORY")), resultSet_2.getInt("AMOUNT"), resultSet_2.getDate("RECDATE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }

    @Override
    public boolean deleteRecordById(int recId) {
        try {
            PreparedStatement statement = dbHelper.getConn().prepareStatement("DELETE FROM RECORDS WHERE ID = ?");
            statement.setInt(1, recId);
            if (statement.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addUser(User user) {
        PreparedStatement statement = null;
        try {
            statement = dbHelper.getConn().prepareStatement("INSERT OR REPLACE INTO USERS (NAME, PASSWORD) VALUES" +
                    " (?, ?)");
            statement.setString(1, user.getName());
            statement.setString(2, user.getPasswd());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("addUser(): " + user);
    }

    @Override
    public int addAccount(User user, String desc) {
        int rowId = -1;
        try {
            PreparedStatement statement_1 = dbHelper.getConn().prepareStatement("INSERT INTO ACCOUNTS (DESCR, USER_NAME) " +
                    "VALUES (?,?)");
            statement_1.setString(1, desc);
            statement_1.setString(2, user.getName());
            if (statement_1.executeUpdate() != 0) {
                Statement statement = dbHelper.getConn().createStatement();
                ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM ACCOUNTS");
                rowId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return rowId;
        }
    }

    @Override
    public int addRecord(Account account, Record record) {
        int rowId = -1;
        try {
            PreparedStatement statement_1 = dbHelper.getConn().prepareStatement("INSERT INTO RECORDS (DESCR, AMOUNT, " +
                    "CATEGORY, RECDATE, ACCOUNT_ID) VALUES (?,?,?,?,?)");
            statement_1.setString(1, record.getDescription());
            statement_1.setInt(2, record.getAmount());
            statement_1.setString(3, record.getCategory().getCategoryName());
            statement_1.setDate(4, (Date) record.getDate());
            statement_1.setInt(5, account.getId());
            if (statement_1.executeUpdate() != 0) {
                Statement statement = dbHelper.getConn().createStatement();
                ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM RECORDS");
                rowId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return rowId;
        }
    }

    @Override
    public User removeUser(String name) {
        return null;
    }

    @Override
    public Account removeAccount(User owner, Account account) {
        return null;
    }

    @Override
    public Record removeRecord(Account from, Record record) {
        return null;
    }

    @Override
    public boolean updateRecord(Account account, int recordId, Record newRecordVal) {
        if (account == null || newRecordVal == null) {
            return false;
        }
        try {
            PreparedStatement statement_1 = dbHelper.getConn().prepareStatement("UPDATE RECORDS SET DESCR = ?, AMOUNT = ?, " +
                    "CATEGORY = ?, RECDATE = ? WHERE ACCOUNT_ID = ? AND ID = ?");
            statement_1.setString(1, newRecordVal.getDescription());
            statement_1.setInt(2, newRecordVal.getAmount());
            statement_1.setString(3, newRecordVal.getCategory().getCategoryName());
            statement_1.setDate(4, newRecordVal.getDate());
            statement_1.setInt(5, account.getId());
            statement_1.setInt(6, recordId);
            if (statement_1.executeUpdate() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Account getAccountById(int recId) {
        Account account = null;
        try {
            PreparedStatement statement = dbHelper.getConn().prepareStatement("SELECT * FROM ACCOUNTS WHERE ID = ?");
            statement.setInt(1, recId);
            ResultSet resultSet_2 = statement.executeQuery();
            while ((resultSet_2.next())) {
                account = new Account(resultSet_2.getInt("ID"), resultSet_2.getString("DESCR"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
}