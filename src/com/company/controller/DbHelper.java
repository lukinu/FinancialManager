package com.company.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/*
* Class provides access to SQLite DB
* */
public class DbHelper {
    private static String databaseUrl = "jdbc:sqlite:findb.sqlite";
    private static Logger logger = LoggerFactory.getLogger(DbHelper.class);
    private static DbHelper ourInstance = new DbHelper();
    private static Connection connection = null;

    public static DbHelper getInstance() {
        return ourInstance;
    }

    private DbHelper() {
        try {
            Class.forName("org.sqlite.JDBC");
            logger.debug("Opening database: {}", databaseUrl);
            connection = DriverManager.getConnection(databaseUrl);
            if (!doTablesExist()) {
                Statement statement = connection.createStatement();
                String createSql = readResource(DbHelper.class, "../model/create.sql");
                statement.executeUpdate(createSql);
//                String insertSql = readResource(DbHelper.class, "../model/insert.sql");
//                statement.executeUpdate(insertSql);
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            closeConnection();
        }
    }

    public Connection getConn() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(databaseUrl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    boolean doTablesExist() throws Exception {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='USERS';");
        boolean result = true;
        int count = resultSet.getInt(1);
        if (count == 0) {
            result = false;
        }
        resultSet.close();
        statement.close();
        return result;
    }

    String readResource(Class cpHolder, String path) throws Exception {
        java.net.URL url = cpHolder.getResource(path);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
    }

    public static void closeResource(AutoCloseable res) {
        try {
            if (res != null) {
                res.close();
                res = null;
            }
        } catch (Exception e) {
            logger.warn("Failed to close resource: {}", res);
        }
    }

    public static void closeConnection() {
        closeResource(connection);
        connection = null;
    }

}