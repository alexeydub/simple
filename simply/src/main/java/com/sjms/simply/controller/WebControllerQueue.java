package com.sjms.simply.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sjms.simply.config.Config;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.sevice.JobFilterCriteria;
import com.sjms.simply.sevice.QueueService;

@Controller
public class WebControllerQueue {

    @Autowired
    private QueueService queueService;

    @Autowired
    private Config config;
    
    private int defailtPageSize = 3;
    
    @PostConstruct
    public void init() {
        defailtPageSize = config.getPageSize();
    }

    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public String getJobInQueue(
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
        JobFilterCriteria<QueueJob> cr = new JobFilterCriteria<>(nameFilter,
                tagsFilter, descriptionFilter);
        List<QueueJob> result = queueService.findByFilter(cr, p, ps);

        // process output parameters
        model.addAttribute("page", p);
        model.addAttribute("pageSize", ps);
        model.addAttribute("jobs", result);
        Map<String, String> allFilters = cr.getAllFilters();
        allFilters.entrySet().forEach(entry -> {
            model.addAttribute(entry.getKey(), entry.getValue());
        });

        return "/queue";
    }

}
