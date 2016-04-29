package com.company.controller;

import com.company.model.DbDataStorage;
/*
* Simply runs application
* */
public class Main {
    public static void main(String[] args) {
        final Interacting guiController = new GuiController(new DbDataStorage());
        guiController.run();
    }
}
