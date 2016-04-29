package com.company.controller;

import java.util.Map;

public interface Observable {
    void notifyObservers(Object eventSource, Map<String, Object> params);

    void addObserver(Observer observer);

    void removeObserver(Observer observer);
}
