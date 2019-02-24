package com.sjms.simply.controller;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sjms.simply.config.Config;
import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.sevice.JobFilterCriteria;
import com.sjms.simply.sevice.JobService;
import com.sjms.simply.sevice.QueueService;

@Controller
public class WebController {

    private static final Logger log = LoggerFactory
            .getLogger(WebController.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private Config config;

    private int defailtPageSize = 3;

    @PostConstruct
    public void init() {
        defailtPageSize = config.getPageSize();
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    public String getJobs(
            @RequestParam(name = "page", required = false) String page,
            @RequestParam(name = "pageSize", required = false) String pageSize,
            @RequestParam(name = "name", required = false) String nameFilter,
            @RequestParam(name = "description", required = false) String descriptionFilter,
            @RequestParam(name = "tags", required = false) String tagsFilter,
            Model model) {
        // process input parameters
        int p = ParameterUtils.parseIntDefault(page, 0);
        int ps = ParameterUtils.parseIntDefault(pageSize, defailtPageSize);

        // get jobs list
        JobFilterCriteria<Job> cr = new JobFilterCriteria<>(nameFilter,
                tagsFilter, descriptionFilter);
        List<Job> result = jobService.findByFilter(cr, p, ps);

        // process output parameters
        model.addAttribute("page", p);
        model.addAttribute("pageSize", ps);
        model.addAttribute("jobs", result);
        Map<String, String> allFilters = cr.getAllFilters();
        allFilters.entrySet().forEach(entry -> {
            model.addAttribute(entry.getKey(), entry.getValue());
        });

        return "/jobs";
    }

    @RequestMapping(value = "/jobs/details", method = RequestMethod.GET)
    public String getJobDetails(
            @RequestParam(name = "id", required = true) String jobId,
            Model model) {
        Long id = ParameterUtils.parseLongDefault(jobId, null);
        if (id == null) {
            model.addAttribute("error", "wrong job id: " + jobId);
            return "redirect:jobs";
        }
        Job job = jobService.getJob(id);
        if (job == null) {
            model.addAttribute("error", "job not found! - " + jobId);
            return "redirect:jobs";
        }
        model.addAttribute("job", job);

        return "jobdetails";
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.DELETE)
    public String jobDelete(
            @RequestParam(name = "idlist", required = false) String idlist,
            @RequestParam(name = "_method", required = false) String method,
            Model model) {
        deleteJobs(idlist);
        return "redirect:/jobs";
    }

    private void deleteJobs(String idList) {
        StringTokenizer t = new StringTokenizer(idList, ",");
        while (t.hasMoreElements()) {
            jobService.deleteJob(t.nextToken());
        }
    }

    @RequestMapping(value = "/queue", method = RequestMethod.POST)
    public String moveToQueue(
            @RequestParam(name = "idlist", required = false) String idList,
            Model model) {
        StringTokenizer t = new StringTokenizer(idList, ",");
        while (t.hasMoreElements()) {
            String id = t.nextToken();
            QueueJob qjob = queueService.moveJobToQueue(id);
            log.info(
                    "sent " + id + " job to the queue. New id=" + qjob.getId());
        }
        return "redirect:/queue";
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.PATCH)
    public String updateJob(Model model,
            @RequestParam(name = "id") String idStr,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "tags", required = false) String tags,
            @RequestParam(name = "parameters", required = false) String params,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "send", required = false) String send,
            @RequestParam(name = "cron", required = false) String cron) {
        Long id = ParameterUtils.parseLongDefault(idStr, null);
        if (id == null) {
            model.addAttribute("error", "Wrong id: " + idStr);
            return "redirect:/jobs";
        }
        Job job = jobService.getJob(id);
        if (name != null) {
            job.getJc().setName(name);
        }
        if (description != null) {
            job.getJc().setDescription(description);
        }
        if (params != null) {
            job.getJc().setParameters(params);
        }
        if (tags != null) {
            job.getJc().setTags(tags);
        }
        if (priority != null) {
            Integer p = ParameterUtils.parseIntDefault(priority, null);
            if (p != null) {
                job.getJc().setPriority(p);
            }
        }
        if (cron != null) {
            job.getJc().setCron(cron);
        }
        jobService.saveJob(job);
        if ("true".equals(send)) {
            return moveToQueue(idStr, model);
        } else {
            //return "redirect:/jobs/details?id=" + job.getId();
            return "redirect:/jobs";
        }
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.POST)
    public String createJob(Model model,
            @RequestParam(name = "actionId") String actionId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "parameters") String params,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "page", required = false) String page,
            @RequestParam(name = "pageSize", required = false) String pageSize) {
        log.info("create a job method was called. Name: " + name);
        if (priority == null) {
            priority = "100";
        }
        try {
            Job job = jobService.createJob(actionId, name, description, params,
                    priority);
            return "redirect:/jobs/details?id=" + job.getId();
        } catch (Throwable ex) {
            model.addAttribute("error",
                    "Unable to create a Job: " + ex.getMessage());
        }
        return "redirect:/jobs";
    }
}
