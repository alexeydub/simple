package com.sjms.simply;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.domain.Status;
import com.sjms.simply.repositories.QueueJobRepository;
import com.sjms.simply.sevice.ActionService;
import com.sjms.simply.sevice.QuartzSchedulerService;
import com.sjms.simply.sevice.QueueService;

/**
 * Job executor. Executes {@link QueueJob} from a queue according to priority.
 */
@Component
public class JobExecutor implements Runnable {

    private static final Logger log = LoggerFactory
            .getLogger(JobExecutor.class);

    @Autowired
    @Qualifier("executor")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private QueueJobRepository queueRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private QuartzSchedulerService quartzService;

    private static final int MAX = 5;

    private boolean running = false;

    /**
     *
     * @return true, if Job executor is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop Job executor.
     */
    public synchronized void shutDown() {
        this.running = false;
    }

    /**
     * Start Job executor.
     */
    public synchronized void start() {
        if (!running) { // not running yet?
            executor.execute(this);
        }
    }

    /**
     * call back to report about job.
     * 
     * @param queueJob finished job
     */
    public void jobFinished(QueueJob queueJob) {
        queueRepository.save(queueJob);
    }

    /**
     * @return available threads count
     */
    public synchronized int getAvailableThreadsCount() {
        return executor.getCorePoolSize() - executor.getActiveCount();
    }

    @Override
    public void run() {
        // mark we are running
        synchronized (this) {
            if (running) { // already?
                return;
            }
            this.running = true;
        }
        log.info("Job executor starts");

        while (true) {
            synchronized (this) {
                if (!running) { // stop?
                    break;
                }
            }
            // while thread is available
            while (getAvailableThreadsCount() > 0) {
                synchronized (this) {
                    if (!running) { // stop?
                        break;
                    }
                }
                // get MAX top jobs
                List<QueueJob> jobs = queueService.topJobByPriotity(MAX);
                if (!jobs.isEmpty()) {
                    // try to execute one of them
                    for (QueueJob queueJob : jobs) {
                        if (executeJob(queueJob)) {
                            break;
                        }
                    }
                } else {
                    // no tasks, enjoy your time!
                    sleep();
                }
            }
        }
    }

    private boolean executeJob(QueueJob queueJob) {
        // try to update into running state
        boolean updated = tryToUpdate(queueJob);
        if (updated) {
            // execute job
            execute(queueJob);
            return true;
        }
        return false;
    }

    private void sleep() {
        try {
            // sleep for some time
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // thread was interrupted
            synchronized (this) {
                running = false;
            }
        }
    }

    private void execute(QueueJob queueJob) {
        // retrieve action
        Action action = null;
        try {
            action = actionService.getAction(
                    queueJob.getJc().getActionType().getClassName(),
                    queueJob.getJc().getParameters());
        } catch (Throwable ex) {
            queueJob.setStatus(Status.FAILED);
            queueJob.setErrorMessage(ex.getMessage());
            return;
        }
        if (queueJob.getJc().getCron() != null) {
            // cron job
            if (quartzService.postJob(queueJob, action)) {
                queueJob.setStatus(Status.SUCCESS);
                queueJob.setErrorMessage(null);
            } else {
                queueJob.setStatus(Status.FAILED);
            }
            // job finished
            jobFinished(queueJob);
        } else {
            // regular job
            ActionProcessor processor = new ActionProcessor(this, queueJob,
                    action);
            // job is executed by another thread
            executor.execute(processor);
        }
    }

    private boolean tryToUpdate(QueueJob job) {
        try {
            job.setErrorMessage(null);
            return queueService.tryUpdateToRun(job);
        } catch (Throwable fail) {
            return false;
        }
    }

}
