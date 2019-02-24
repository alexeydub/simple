package com.sjms.simply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test action. Do nothing but log param and wait for 1 sec
 *
 */
public class TestAction implements Action {

    private static final Logger log = LoggerFactory.getLogger(TestAction.class);

    private String param;

    @Override
    public boolean execute() {
        log.info("executing " + param);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        log.info("executed " + param);
        return true;
    }

    @Override
    public boolean compenstate() {
        // nothing to compensate
        return true;
    }

    @Override
    public void setParams(String params) {
        this.param = params;
    }

    @Override
    public String getName() {
        return "test action 1";
    }

    @Override
    public String getDescription() {
        return "Test action." + "\nJust wait 1 sec and finishing."
                + "\nDon't need any parameters";
    }

}
