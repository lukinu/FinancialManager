package com.company.view;

import com.company.controller.Observable;
import com.company.controller.Observer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class represents main GUI window
* */
public class MainWindow extends JFrame implements Observable, TableModelListener, MouseListener, ActionListener, ChangeListener {
    private JPanel jpMainPanel;
    private JTabbedPane tpAccounts;
    private JButton btnAddRecord;
    private JTextField tfInputAccountName = new JTextField();
    private ArrayList<JTable> tablesArray = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private static final String AC_TAB_HEADER = "Account: ";

    public MainWindow(ArrayList<ExpensesTableModel> tableModels)
            throws HeadlessException {
        setName("main_win");
        setTitle("yourMoney");
        initMainWindow(tableModels);
    }

    // read input data, setup GUI elements
    private void initMainWindow(ArrayList<ExpensesTableModel> tableModels) {
        jpMainPanel.setPreferredSize(new Dimension(450, 400));
        final MainWindow mainWindow = this;
        tpAccounts.addChangeListener(this);
        btnAddRecord.addActionListener(this);
        /*
         * Add new empty tab used as 'add account' control
        */
        tpAccounts.add(new JScrollPane());
        tfInputAccountName.setName("btnAddAcc");
        tfInputAccountName.setVisible(false);
        tfInputAccountName.setColumns(5);
        tfInputAccountName.addActionListener(actionEvent1 -> {
            String newAccountName = tfInputAccountName.getText();
            if (!newAccountName.equals("")) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("desc", newAccountName);
                notifyObservers(actionEvent1.getSource(), params);
                tfInputAccountName.setVisible(false);
                mainWindow.repaint();
            }
        });
        JButton btnAddAccount = new JButton("+");
        btnAddAccount.setOpaque(false);
        btnAddAccount.setContentAreaFilled(false);
        btnAddAccount.setBorderPainted(false);
        btnAddAccount.addActionListener(actionEvent -> {
            tfInputAccountName.setVisible(true);
            mainWindow.repaint();
        });
        JPanel tabPanel = new JPanel(new FlowLayout());
        tabPanel.add(tfInputAccountName);
        tabPanel.add(btnAddAccount);
        tabPanel.setToolTipText("add new account");
        tpAccounts.setTabComponentAt(tpAccounts.getTabCount() - 1, tabPanel);
        tpAccounts.setEnabledAt(tpAccounts.getTabCount() - 1, false);
        btnAddRecord.setEnabled(false);
        /*
        * populate tabs with model's data
        */
        if (!tableModels.isEmpty()) {
            tableModels.forEach(this::addTab);
        }
        // finally compose GUI
        tpAccounts.setSelectedIndex(0);
        setContentPane(jpMainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainWindow.pack();
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

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
    }

    @Override
    // process double right-click
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3 && mouseEvent.getClickCount() == 2) {
            HashMap<String, Object> params = new HashMap<>();
            JTable table = (JTable) mouseEvent.getSource();
            int rowNum = table.getSelectedRow();
            if (rowNum == -1) {
                return;
            }
            params.put("id", table.getValueAt(rowNum, 0));
            params.put("row_num", rowNum);
            notifyObservers(table, params);
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    // process button clicks
    public void actionPerformed(ActionEvent e) {
        notifyObservers(e.getSource(), null);
    }

    public JTable getActiveTable() {
        return (JTable) ((JScrollPane) tpAccounts.getSelectedComponent()).getViewport().getView();
    }

    // hides new account input text field on switching to another tab
    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (tfInputAccountName.isVisible()) {
            tfInputAccountName.setVisible(false);
        }
        if (tpAccounts.getSelectedIndex() == tpAccounts.getTabCount() - 1) {
            btnAddRecord.setEnabled(false);
        } else {
            btnAddRecord.setEnabled(true);
        }
        repaint();
    }

    public void addTab(ExpensesTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.getModel().addTableModelListener(this);
        table.addMouseListener(this);
        table.setToolTipText("click to edit, double right-click to delete");
        // insert new tab at next to last position
        tpAccounts.insertTab(AC_TAB_HEADER + tableModel.getAccount().getDescription(), null, new JScrollPane(table),
                null, tpAccounts.getTabCount() - 1);
        tablesArray.add(table);
        tpAccounts.setSelectedIndex(tpAccounts.getTabCount() - 2);
    }
}