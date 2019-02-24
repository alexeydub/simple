package com.sjms.simply.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjms.simply.config.Config;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.sevice.JobFilterCriteria;
import com.sjms.simply.sevice.QueueService;

@RestController
public class QueueController {

    private static final int DEFAULT_LIMIT = 20;

    @Autowired
    private Config config;

    private int defailtPageSize = DEFAULT_LIMIT;

    @PostConstruct
    public void init() {
        defailtPageSize = config.getPageSize();
    }

    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "/api/jobs/queue", method = RequestMethod.GET)
    public List<QueueJob> getJobs(
            @RequestParam(name = "offset", required = false) String offset,
            @RequestParam(name = "limit", required = false) String limit,
            @RequestParam(name = "name", required = false) String nameFilter,
            @RequestParam(name = "description", required = false) String descriptionFilter,
            @RequestParam(name = "tags", required = false) String tagsFilter,
            Model model) {
        int p = ParameterUtils.parseIntDefault(offset, 0);
        int ps = ParameterUtils.parseIntDefault(limit, defailtPageSize);
        JobFilterCriteria<QueueJob> cr = new JobFilterCriteria<>(nameFilter,
                tagsFilter, descriptionFilter);
        return queueService.findByFilter(cr, p, ps);
    }

}
