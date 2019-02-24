package com.sjms.simply;

import java.io.StringReader;
import java.util.Properties;

/**
 * Example {@link Action} for debugging.
 */
public class DebugAction implements Action {

    /**
     * Action properties
     */
	private Properties props = new Properties();

	@Override
	public String getName() {
		return "DebugAction";
	}

	@Override
	public String getDescription() {
		return "Action for debug. Accept properties: execute.delay,"
		        + "\nexecute.exception, execute.false, compensate.delay,"
		        + "\ncompensate.exception, compensate.false"
		        + "\nExample (action executes 20 seconds without success. Compensation takes 2 seconds:"
		        + "\nexecute.delay=20000"
		        + "\nexecute.false=any"
		        + "\ncompensate.delay=2000";
	}

	@Override
	public boolean execute() {
		if (props.containsKey("execute.delay")) {
			try {
				Thread.sleep(Integer.parseInt(props.getProperty("execute.delay")));
			} catch (NumberFormatException | InterruptedException e) {
			}
		}
		if (props.containsKey("execute.exception")) {
			throw new RuntimeException(props.getProperty("execute.exception"));
		}
		if (props.containsKey("execute.false")) {
			return false;
		}
		return true;
	}

	@Override
	public boolean compenstate() {
		if (props.containsKey("compenstate.delay")) {
			try {
				Thread.sleep(Integer.parseInt(props.getProperty("compenstate.delay")));
			} catch (NumberFormatException | InterruptedException e) {
			}
		}
		if (props.containsKey("compenstate.exception")) {
			throw new RuntimeException(props.getProperty("compenstate.exception"));
		}
		if (props.containsKey("compenstate.false")) {
			return false;
		}
		return true;
	}

	@Override
	public void setParams(String param) throws Exception {
		StringReader reader = new StringReader(param);
		props.load(reader);
	}

}
