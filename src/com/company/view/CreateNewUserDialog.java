package com.company.view;

import com.company.controller.Observable;
import com.company.controller.Observer;
import com.company.model.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewUserDialog extends JDialog implements Observable {
    private JPanel contentPane;
    private JButton btnCreateOK;
    private JButton btnCancel;
    private JTextField tfUserName;
    private JPasswordField pfPasswd;
    private JPasswordField pfPasswdCfm;
    private JLabel lbMessage;
    private List<Observer> observers = new ArrayList<Observer>();

    public void setLbMessage(String test) {
        this.lbMessage.setText(test);
    }

    public CreateNewUserDialog() {
        setName("create_user");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnCreateOK);
        btnCreateOK.setName("btnCreateOK");

        btnCreateOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(e.getSource());
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel(e.getSource());
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

    private void onOK(Object eventSource) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", tfUserName.getText());
        String passwd = new String(pfPasswd.getPassword());
        String passwdCfm = new String(pfPasswdCfm.getPassword());
        if (passwd.equals(passwdCfm)) {
            try {
                params.put("passwd", new String(Manager.getSHA256Hash(new String(pfPasswd.getPassword())), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            notifyObservers(eventSource, params);
        } else {
            lbMessage.setText("password doesn't match");
            pfPasswdCfm.setBackground(Color.RED);
        }
    }

    private void onCancel(Object eventSource) {
// add your code here if necessary
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