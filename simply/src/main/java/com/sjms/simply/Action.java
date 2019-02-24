package com.sjms.simply;

import com.sjms.simply.JobExecutor;

/**
 * Job execution based on Action. Action could have name, description and
 * params. All job must be done within execute method. Action must be atomic.
 * So, if action fail then {@link JobExecutor} will call compensate method.
 *
 */
public interface Action {

    /**
     * @return name
     */
    String getName();

    /**
     * @return description
     */
    String getDescription();

    /**
     * Execute action.
     * 
     * @return true, if action succeed
     */
    boolean execute();

    /**
     * Called by executor if action failed to compensate possible side effects.
     *
     * @return true, if succeed
     */
    boolean compenstate();

    /**
     * Set action parameters if there are any.
     *
     * @param param action parameters
     *
     * @throws Exception throws if wrong parameters was set. Exception message
     *                   should explain why it is wrong
     */
    void setParams(String param) throws Exception;
}
