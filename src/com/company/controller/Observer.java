package com.company.controller;

import java.util.Map;

public interface Observer {
    void handleEvent(Object eventSource, Map<String, Object> params);
}
