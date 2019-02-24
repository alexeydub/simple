package com.sjms.simply.service;

import com.sjms.simply.Action;

public class TestAction implements Action {
    
    @Override
    public boolean execute() {
        return true;
    }

    @Override
    public boolean compenstate() {
        return true;
    }

    @Override
    public void setParams(String params) {
    }

    @Override
    public String getName() {
        return "JUnit test action";
    }

    @Override
    public String getDescription() {
        return "JUnit action desctiprion";
    }

}
