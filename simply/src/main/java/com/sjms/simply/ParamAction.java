package com.sjms.simply;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * Test action with parameters.
 */
public class ParamAction implements Action {

    final static private String MESSAGE = "message";

    private Properties props = new Properties();

    @Override
    public boolean execute() {
        System.err.println(props.getProperty("message"));
        return true;
    }

    @Override
    public boolean compenstate() {
        // nothing to compensate...
        return true;
    }

    @Override
    public void setParams(String param) throws IOException {
        StringReader reader = new StringReader(param);
        props.load(reader);
        if (!props.containsKey(MESSAGE)) {
            throw new IllegalArgumentException(
                    "Missed a required property: " + MESSAGE);
        }
    }

    @Override
    public String getName() {
        return "Test action 2";
    }

    @Override
    public String getDescription() {
        return "Test action."
                + "\nJust wait for 1 sec, log a message and finish."
                + "\nTake properties as parameters."
                + "\nHas mandatory property named 'message'"
                + "\nParameters example:" + "\nmessage=Hello, world!";
    }

}
