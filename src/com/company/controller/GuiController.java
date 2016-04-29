package com.company.controller;

import com.company.model.Account;
import com.company.model.DataStore;
import com.company.model.Manager;
import com.company.model.Record;
import com.company.view.CreateNewUserDialog;
import com.company.view.ExpensesTableModel;
import com.company.view.LoginDialog;
import com.company.view.MainWindow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/*
* Class is an abstraction layer between business logic (Manager) and GUI elements
* */
public class GuiController implements Interacting, Observer {

    private Manager manager;
    private LoginDialog loginDialog;
    private CreateNewUserDialog createNewUserDialog;
    private MainWindow mainWindow;

    public GuiController(DataStore dbDataStorage) {
        this.manager = new Manager(dbDataStorage);
    }

    @Override
    public void createLoginDialog() {
        final GuiController controller = this;
        SwingUtilities.invokeLater(() -> {
            loginDialog = new LoginDialog();
            loginDialog.addObserver(controller);
            loginDialog.pack();
            loginDialog.setVisible(true);
        });
    }

    public void run() {
        createLoginDialog();
    }

    @Override
    public void handleEvent(Object eventSource, Map<String, Object> params) {
        if (eventSource instanceof JButton) {
            switch (((JButton) eventSource).getName()) {
                case "btnLoginOK":
                    loginUser(params);
                    break;
                case "btnCreateOK":
                    createNewUser(params);
                    break;
                case "btnCreateNewUser":
                    runCreateNewUserDialog();
                    break;
                case "btnAddRec":
                    addEmptyRow();
                    break;
                default:
                    break;
            }
        }
        if (eventSource instanceof JTextField) {
            switch (((JTextField) eventSource).getName()) {
                case "btnAddAcc":
                    addAccount(params);
                    break;
                default:
                    break;
            }
        }
        if (eventSource instanceof ExpensesTableModel) {
            if (!(boolean) params.get("isNew")) {
                updateCellValue(params);
            } else {
                addNewRecord(params);
            }
        }
        if (eventSource instanceof JTable) {
            deleteRecord(params);
        }
    }

    private void addAccount(Map<String, Object> params) {
        int newAccountId = manager.addAccount(params);
        if (newAccountId > 0) {
            Account newAccount = manager.getAccountById(newAccountId);
            ArrayList<String> columnHeaders = new ArrayList<>();
            columnHeaders.add(Manager.COLUMN1_TITLE);
            columnHeaders.add(Manager.COLUMN2_TITLE);
            columnHeaders.add(Manager.COLUMN3_TITLE);
            columnHeaders.add(Manager.COLUMN4_TITLE);
            columnHeaders.add(Manager.COLUMN5_TITLE);
            ArrayList<Record> data = newAccount.getRecords();
            ExpensesTableModel tableModel = ExpensesTableModel.getNewInstance(newAccount, data, columnHeaders);
            tableModel.addObserver(this);
            mainWindow.addTab(tableModel);
        }
    }

    private boolean addNewRecord(Map<String, Object> params) {
        ((ExpensesTableModel) (mainWindow.getActiveTable().getModel())).removeRow((int) params.get("row_num"));
        int recId = manager.addRecord(params);
        if (recId > 0) {
            Record newRec = manager.getRecordById(recId);
            Object dataArray[] = {newRec.getId(), newRec.getDescription(), newRec.getAmount(),
                    newRec.getCategory().getCategoryName(), newRec.getDate()};
            ((ExpensesTableModel) (mainWindow.getActiveTable().getModel())).addRow(dataArray);
            //todo: referring to active table may produce a bug. replace with table_id
            return true;
        }
        return false;
    }

    private void addEmptyRow() {
        if (mainWindow == null) {
            return;
        }
        ((ExpensesTableModel) (mainWindow.getActiveTable().getModel())).addRow((Object[]) null);
    }

    public void deleteRecord(Map<String, Object> params) {
        if (!manager.deleteRecord(params)) {
            Manager.logger.info("failed to delete record " + params.get("id"));
            return;
        }
        ((ExpensesTableModel) (mainWindow.getActiveTable().getModel())).removeRow((int) params.get("row_num"));
    }

    public void updateCellValue(Map<String, Object> params) {
        if (!manager.updateRecord(params)) {
            Manager.logger.info("failed to update data");
        }
    }

    @Override
    public void runCreateNewUserDialog() {
        final GuiController controller = this;
        SwingUtilities.invokeLater(() -> {
            createNewUserDialog = new CreateNewUserDialog();
            createNewUserDialog.addObserver(controller);
            createNewUserDialog.pack();
            createNewUserDialog.setVisible(true);
        });
    }

    private void createNewUser(Map<String, Object> params) {
        if (createNewUserDialog == null) {
            return;
        }
        if ((((String) params.get("name")).trim()).equals("")) {
            createNewUserDialog.setLbMessage("user and password cannot be empty");
            return;
        }
        if (manager.getUser((String) params.get("name")) == null) {
            if (!manager.createNewUser((String) params.get("name"), (String) params.get("passwd"))) {
                createNewUserDialog.setLbMessage("Failed to create user");
            } else {
                createNewUserDialog.dispose();
            }
        } else {
            createNewUserDialog.setLbMessage("user name already exists");
        }
    }

    private void loginUser(Map<String, Object> params) {
        if (!manager.login((String) params.get("name"), (String) params.get("passwd"))) {
            loginDialog.setLbMessage("wrong name or password");
        } else {
            loginDialog.dispose();
            createMainWindow();
        }
    }

    @Override
    public void createMainWindow() {
        final GuiController controller = this;
        ArrayList<ExpensesTableModel> tableModels = new ArrayList<>();
        ArrayList<String> columnHeaders = new ArrayList<>();
        columnHeaders.add(Manager.COLUMN1_TITLE);
        columnHeaders.add(Manager.COLUMN2_TITLE);
        columnHeaders.add(Manager.COLUMN3_TITLE);
        columnHeaders.add(Manager.COLUMN4_TITLE);
        columnHeaders.add(Manager.COLUMN5_TITLE);
        HashSet<Account> accounts = new HashSet<>(manager.getAccountList());
        if (!accounts.isEmpty()) {
            for (Account account : accounts) {
                ArrayList<Record> data = account.getRecords();
                ExpensesTableModel tableModel = ExpensesTableModel.getNewInstance(account, data, columnHeaders);
                tableModel.addObserver(controller);
                tableModels.add(tableModel);
            }
        }
        SwingUtilities.invokeLater(() -> {
            mainWindow = new MainWindow(tableModels);
            mainWindow.addObserver(controller);
            mainWindow.pack();
            mainWindow.setVisible(true);
        });
    }
}