package com.sjms.simply.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sjms.simply.JobExecutor;
import com.sjms.simply.sevice.QuartzSchedulerService;

@Controller
public class WebControllerHome {

    private static final String EXECUTOR = "executor";
    private static final String CRON = "cron";

    @Autowired
    private JobExecutor executor;

    @Autowired
    private QuartzSchedulerService quartzService;

    @RequestMapping(value = { "/", "/index.html" }, method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("running", executor.isRunning());
        model.addAttribute("threads", executor.getAvailableThreadsCount());
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String startStop(Model model,
            @RequestParam(name = "system") String system) {
        switch (system) {
        case EXECUTOR: {
            if (executor.isRunning()) {
                executor.shutDown();
            } else {
                executor.start();
            }
            break;
        }
        case CRON: {
            try {
                quartzService.setRunning(!quartzService.isStarted());
            } catch (SchedulerException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        }
        try {
            model.addAttribute("cronrunning", quartzService.isStarted());
        } catch (SchedulerException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cronrunning", "false");
        }
        model.addAttribute("running", executor.isRunning());
        model.addAttribute("threads", executor.getAvailableThreadsCount());
        return "index";
    }

}
