package com.sjms.simply.sevice;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sjms.simply.Action;
import com.sjms.simply.domain.CronJob;
import com.sjms.simply.domain.QueueJob;

@Component
public class QuartzSchedulerService {

    private static final Logger log = LoggerFactory
            .getLogger(QuartzSchedulerService.class);

    @Autowired
    private Scheduler scheduler;

    /**
     * Get all jobs.
     *
     * @return job list
     * @throws SchedulerException
     */
    public List<CronJob> getCronJobs() throws SchedulerException {
        List<CronJob> cronJobList = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler
                    .getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                CronJob cronJob = new CronJob();
                String jobName = jobKey.getName();
                cronJob.setJobName(jobName);
                String jobGroup = jobKey.getGroup();
                cronJob.setGroupName(jobGroup);
                JobDetail details = scheduler.getJobDetail(jobKey);
                cronJob.setDescription(details.getDescription());
                cronJob.setDataMap(details.getJobDataMap().toString());
                List<? extends Trigger> triggers = scheduler
                        .getTriggersOfJob(jobKey);
                if (!triggers.isEmpty()) {
                    Trigger trigger = triggers.get(0);
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        cronJob.setCronExpression(
                                cronTrigger.getCronExpression());
                    }
                }
                cronJobList.add(cronJob);
            }
        }
        return cronJobList;
    }

    /**
     * @return is scheduler started
     * @throws SchedulerException
     */
    public boolean isStarted() throws SchedulerException {
        return scheduler.isStarted();
    }

    /**
     * Start/standby scheduler.
     *
     * @param running true to start
     * @throws SchedulerException
     */
    public void setRunning(boolean running) throws SchedulerException {
        if (!running) {
            if (scheduler.isStarted()) {
                scheduler.standby();
            }
        } else {
            if (scheduler.isShutdown() || scheduler.isInStandbyMode()) {
                scheduler.start();
            }
        }
    }

    /**
     * Post job on the schedule.
     *
     * @param queueJob job from queue
     * @param action action to execute
     * @return
     */
    public boolean postJob(QueueJob queueJob, Action action) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobId", queueJob.getId());
        jobDataMap.put("className",
                queueJob.getJc().getActionType().getClassName());
        jobDataMap.put("params", queueJob.getJc().getParameters());

        JobDetail jobDetail = JobBuilder.newJob(SimpleJob.class)
                .usingJobData(jobDataMap)
                .withIdentity("id=" + queueJob.getId() + ":"
                        + queueJob.getJc().getName())
                .withDescription(queueJob.getJc().getDescription()).build();

        CronTrigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .usingJobData(jobDataMap).withSchedule(CronScheduleBuilder
                        .cronSchedule(queueJob.getJc().getCron()))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            queueJob.setErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Quartz job to execute SJMS job.
     */
    private static class SimpleJob implements Job {

        @Autowired
        private ActionService actionService;

        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            boolean success = false;
            String jobId = context.getJobDetail().getKey().getName();
            String className = context.getTrigger().getJobDataMap()
                    .getString("className");
            String params = context.getTrigger().getJobDataMap()
                    .getString("params");
            try {
                Action action = actionService.getAction(className, params);
                try {
                    success = action.execute();
                    if (!success) {
                        success = compensate(jobId, action);
                    } else {
                        log.info("Cron job '" + jobId
                                + "' was executed with success");
                    }
                } catch (Throwable ex) {
                    log.error("Cron job '" + jobId
                            + "' error during execution: " + ex.getMessage());
                }
            } catch (IllegalArgumentException fatal) {
                log.error(fatal.getMessage());
            }
            context.setResult(success);
        }

        private boolean compensate(String jobId, Action action) {
            boolean success = false;
            try {
                success = action.compenstate();
                if (success) {
                    log.error("Cron job '" + jobId
                            + "' failed but was compensated");
                } else {
                    log.error("Cron job '" + jobId
                            + "' failed and not compensated");
                }
            } catch (Throwable ex) {
                log.error("Cron job '" + jobId + "' error during compensation: "
                        + ex.getMessage());
            }
            return success;
        }

    }

}
