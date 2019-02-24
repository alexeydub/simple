package com.sjms.simply;

import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.domain.Status;

/**
 * Process an {@link Action}.
 * 
 */
public class ActionProcessor implements Runnable {

    /**
     * Action to proceed.
     */
    private Action action;

    /**
     * Job executor.
     */
    private JobExecutor jobExecutor;

    /**
     * Job to execute.
     */
    private QueueJob queueJob;

    /**
     * Action processor.
     *
     * @param jobExecutor Executor
     * @param job         Job to execute
     * @param action      Action for the job
     */
    public ActionProcessor(JobExecutor jobExecutor, QueueJob job,
            Action action) {
        this.action = action;
        this.queueJob = job;
        this.jobExecutor = jobExecutor;
    }

    @Override
    public void run() {
        // action is not complete yet
        boolean actionComplete = false;
        try {
            // call execute
            actionComplete = action.execute();
        } catch (Throwable error) {
            // exception during execution: report
            queueJob.setErrorMessage(error.getMessage());
        }

        if (!actionComplete) { // Failed?
            try { // try to compensate
                boolean rolledBack = action.compenstate();
                if (rolledBack) { // compensated
                    queueJob.setStatus(Status.FAILED);
                    queueJob.setErrorMessage(null);
                    jobExecutor.jobFinished(queueJob);
                } else { // job executed partially, compensation failed
                    queueJob.setStatus(Status.FAILED);
                    queueJob.setErrorMessage("unable to compensate");
                    jobExecutor.jobFinished(queueJob);
                }
            } catch (Throwable fatal) {
                // exception during compensation, - report
                queueJob.setStatus(Status.FAILED);
                queueJob.setErrorMessage(
                        "unable to compensate: " + fatal.getMessage());
                jobExecutor.jobFinished(queueJob);
            }
        } else { // Success
            // report
            queueJob.setStatus(Status.SUCCESS);
            queueJob.setErrorMessage(null);
            jobExecutor.jobFinished(queueJob);
        }
    }
}
