package com.sjms.simply.controller;

import java.util.ArrayList;
import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sjms.simply.domain.CronJob;
import com.sjms.simply.sevice.QuartzSchedulerService;

@Controller
public class WebControllerCron {

    @Autowired
    private QuartzSchedulerService quartzService;
    
    @RequestMapping(value = "/cron", method = RequestMethod.GET)
    public String getCron(Model model) {
        String errors = "";
        try {
            model.addAttribute("started", quartzService.isStarted());
        } catch (SchedulerException e) {
            errors += "unable to determine is scheduler started: " + e.getMessage();
            model.addAttribute("started", "false");
        }
        List<CronJob> cronJobList = new ArrayList<>();
        try {
            cronJobList = quartzService.getCronJobs();
        } catch (SchedulerException e) {
            errors += "unable to retrieve cron jobs: " + e.getMessage();
        }

        model.addAttribute("cronjobs", cronJobList);
        
        if (!"".equals(errors)) {
            model.addAttribute("error", errors);
        }
        return "/cron";
    }

}
