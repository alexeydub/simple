package com.sjms.simply.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sjms.simply.config.Config;
import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.sevice.JobFilterCriteria;
import com.sjms.simply.sevice.JobService;
import com.sjms.simply.sevice.QueueService;

@RestController
public class JobController {

    private static final int DEFAULT_LIMIT = 20;

    @Autowired
    private JobService jobService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private Config config;

    private int defailtPageSize = DEFAULT_LIMIT;

    @PostConstruct
    public void init() {
        defailtPageSize = config.getPageSize();
    }

    @RequestMapping(value = "/api/jobs", method = RequestMethod.GET)
    public List<Job> getJobs(
            @RequestParam(name = "offset", required = false) String offset,
            @RequestParam(name = "limit", required = false) String limit,
            @RequestParam(name = "name", required = false) String nameFilter,
            @RequestParam(name = "description", required = false) String descriptionFilter,
            @RequestParam(name = "tags", required = false) String tagsFilter) {
        int p = ParameterUtils.parseIntDefault(offset, 0);
        int ps = ParameterUtils.parseIntDefault(limit, defailtPageSize);
        JobFilterCriteria<Job> cr = new JobFilterCriteria<>(nameFilter,
                tagsFilter, descriptionFilter);
        return jobService.findByFilter(cr, p, ps);
    }

    @RequestMapping(value = "/api/jobs/{id}", method = RequestMethod.GET)
    public Job getJobById(
            @PathVariable(name = "id", required = true) String jobId) {
        Long id = ParameterUtils.parseLongDefault(jobId, null);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "entity not found");
        }
        Job job = jobService.getJob(id);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "entity not found");
        }
        return job;
    }

    @RequestMapping(value = "/api/jobs/{id}", method = RequestMethod.DELETE)
    public void jobDelete(
            @PathVariable(name = "id", required = true) String jobId) {
        jobService.deleteJob(jobId);
        return;
    }

    @RequestMapping(value = "/api/jobs/{id}/queue", method = RequestMethod.POST)
    public QueueJob moveToQueue(
            @PathVariable(name = "id", required = true) String jobId) {
        return queueService.moveJobToQueue(jobId);
    }

    @RequestMapping(value = "/api/jobs/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Job> updateJob(
            @PathVariable(name = "id", required = true) String idStr,
            @RequestBody Job update) {
        Long id = ParameterUtils.parseLongDefault(idStr, null);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wrong id: '" + idStr + "'");
        }
        Job job = jobService.getJob(id);
        if (update.getJc().getName() != null) {
            job.getJc().setName(update.getJc().getName());
        }
        if (update.getJc().getDescription() != null) {
            job.getJc().setDescription(update.getJc().getDescription());
        }
        if (update.getJc().getParameters() != null) {
            job.getJc().setParameters(update.getJc().getParameters());
        }
        if (update.getJc().getTags() != null) {
            job.getJc().setTags(update.getJc().getTags());
        }
        if (update.getJc().getPriority() != null) {
            job.getJc().setPriority(update.getJc().getPriority());
        }
        if (update.getJc().getCron() != null) {
            job.getJc().setCron(update.getJc().getCron());
        }
        return new ResponseEntity<Job>(job, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/jobs", method = RequestMethod.POST)
    public Job createJob(@RequestBody Job aJob) {
        return jobService.saveJob(aJob);
    }

}
