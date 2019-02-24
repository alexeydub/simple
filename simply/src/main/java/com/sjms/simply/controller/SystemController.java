package com.sjms.simply.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjms.simply.JobExecutor;
import com.sjms.simply.sevice.QuartzSchedulerService;

@RestController
public class SystemController {

    @Autowired
    private QuartzSchedulerService quartzService;

    @Autowired
    private JobExecutor executor;

    @RequestMapping(value = "/api/systems/executor", method = RequestMethod.GET)
    public boolean getExecutorStatus() {
        return executor.isRunning();
    }

    @RequestMapping(value = "/api/systems/executors/1", method = RequestMethod.PUT)
    public boolean setExecutorStatus(
            @RequestParam(name = "state", required = true) boolean running) {
        if (running) {
            if (!executor.isRunning()) {
                executor.start();
            }
            return true;
        } else {
            if (executor.isRunning()) {
                executor.shutDown();
            }
            return false;
        }
    }

    @RequestMapping(value = "/api/systems/schedulers/1", method = RequestMethod.PUT)
    public boolean setSchedulerStatus(
            @RequestParam(name = "state", required = true) boolean running) {
        try {
            quartzService.setRunning(running);
            return quartzService.isStarted();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
