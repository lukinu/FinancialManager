package com.company.view;

import com.company.controller.Observable;
import com.company.controller.Observer;
import com.company.model.Account;
import com.company.model.Category;
import com.company.model.Manager;
import com.company.model.Record;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class represents data model to display
* */
public class ExpensesTableModel extends DefaultTableModel implements Observable {

    private Account account = null;
    private List<Observer> observers = new ArrayList<Observer>();

    private ExpensesTableModel(Account account, Object[][] data, Object[] columnHeaders) {
        super(data, columnHeaders);
        this.account = account;
    }

    public static ExpensesTableModel getNewInstance(Account account, ArrayList<Record> dataList, ArrayList<String> columnHeadersList) {
        Object[][] data = new Object[dataList.size()][6];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i).getId();
            data[i][1] = dataList.get(i).getDescription();
            data[i][2] = dataList.get(i).getAmount();
            data[i][3] = dataList.get(i).getCategory().getCategoryName();
            data[i][4] = dataList.get(i).getDate();
            data[i][5] = new JButton();
        }
        return new ExpensesTableModel(account, data, columnHeadersList.toArray());
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {
        switch (i1) {
            case 0:
                return;
            case 2:
                try {
                    Integer.parseInt((String) o);
                } catch (NumberFormatException e) {
                    Manager.logger.debug(e.toString());
                    return;
                }
                break;
            case 4:
                Date date = null;
                try {
                    date = java.sql.Date.valueOf((String) o);
                } catch (IllegalArgumentException e) {
                    Manager.logger.debug(e.toString());
                    return;
                }
                break;
            default:
                break;
        }
        super.setValueAt(o, i, i1);
        if (this.getValueAt(i, 0) != null) {
            HashMap<String, Object> params = new HashMap<>();
            String val = this.getValueAt(i, i1).toString();
            params.put("isNew", false);
            params.put("acc", account.getDescription());
            params.put("id", Integer.parseInt(this.getValueAt(i, 0).toString()));
            params.put("col", i1);
            params.put("val", val);
            notifyObservers(this, params);
        } else {
            if (this.getValueAt(i, 1) != null && this.getValueAt(i, 2) != null &&
                    this.getValueAt(i, 3) != null && this.getValueAt(i, 4) != null) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("isNew", true);
                params.put("row_num", i);
                params.put("acc", account);
                params.put("desc", this.getValueAt(i, 1));
                params.put("amount", Integer.parseInt(this.getValueAt(i, 2).toString()));
                params.put("cat", new Category(this.getValueAt(i, 3).toString()));
                params.put("date", Date.valueOf(this.getValueAt(i, 4).toString()));
                notifyObservers(this, params);
            }
        }
    }

    @Override
    public void notifyObservers(Object eventSource, Map<String, Object> params) {
        for (Observer observer : observers) {
            observer.handleEvent(eventSource, params);
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public Account getAccount() {
        return account;
    }
}