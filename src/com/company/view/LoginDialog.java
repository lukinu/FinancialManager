package com.company.view;

import com.company.controller.Observable;
import com.company.controller.Observer;
import com.company.model.Manager;

import javax.swing.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginDialog extends JDialog implements Observable {
    private JPanel contentPane;
    private JButton btnOK;
    private JButton btnCancel;
    private JTextField tfName;
    private JPasswordField tfPasswd;
    private JLabel lbPassword;
    private JLabel lbName;
    private JLabel lbMessage;
    private JButton btnCreateNewUser;
    private List<Observer> observers = new ArrayList<Observer>();
    private HashMap<String, Object> params = new HashMap<>();

    public LoginDialog() {
        setName("login_dialog");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnOK);
        btnOK.setName("btnLoginOK");
        btnCreateNewUser.setName("btnCreateNewUser");

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(e.getSource());
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel(e.getSource());
            }
        });

        btnCreateNewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateNewUser(e.getSource());
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel(e.getSource());
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel(e.getSource());
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCreateNewUser(Object eventSource) {
        params.clear();
        params.put("create_user", " ");
        notifyObservers(eventSource, params);
    }

    public void setLbMessage(String text) {
        this.lbMessage.setText(text);
    }

    private void onOK(Object eventSource) {
        params.clear();
        params.put("name", tfName.getText());
        try {
            params.put("passwd", new String(Manager.getSHA256Hash(new String(tfPasswd.getPassword())), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        notifyObservers(eventSource, params);
    }

    private void onCancel(Object eventSource) {
        dispose();
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
}