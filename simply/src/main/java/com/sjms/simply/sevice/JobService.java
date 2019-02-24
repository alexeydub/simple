package com.sjms.simply.sevice;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.sjms.simply.Action;
import com.sjms.simply.domain.ActionType;
import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.JobCommon;
import com.sjms.simply.repositories.ActionTypeRepository;
import com.sjms.simply.repositories.JobRepository;

/**
 * Service for {@link Job}
 */
@Service
public class JobService {

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private ActionTypeRepository actionRepo;

    public JobService() {

    }

    /**
     * Return page from filtered job list.
     * 
     * @param criteria filters
     * @param page     page
     * @param pageSize page size
     * @return job list
     */
    public List<Job> findByFilter(JobFilterCriteria<Job> criteria, int page,
            int pageSize) {
        Page<Job> pageJobs = null;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        List<Specification<Job>> filters = criteria.getAllSpecs();
        if (!filters.isEmpty()) {
            // create specification based on filters
            Specification<Job> spec = null;
            for (Specification<Job> specification : filters) {
                if (spec == null) {
                    spec = Specification.where(specification);
                } else {
                    spec = spec.and(specification);
                }
            }
            pageJobs = jobRepo.findAll(spec, pageRequest);
        } else {
            pageJobs = jobRepo.findAll(pageRequest);
        }
        // convert to list
        List<Job> result = pageJobs.stream().collect(Collectors.toList());
        return result;
    }

    /**
     * Create a new {@link Job}. Create, validate and save Job.
     * 
     * @param actionId    job action
     * @param name        job name
     * @param description job description
     * @param params      job params
     * @param priority    job priority
     * @return Job
     */
    public Job createJob(String actionId, String name, String description,
            String params, String priority) {

        Integer id = parseInt(actionId, "action id");
        Integer pr = parseInt(priority, "priority");
        ActionType actionType = getActionTypeById(id);
        createAction(params, actionType);

        Job job = new Job();
        JobCommon jc = new JobCommon();
        job.setJc(jc);
        jc.setActionType(actionType);
        jc.setName(name);
        jc.setDescription(description);
        jc.setParameters(params);
        jc.setPriority(pr);

        job = jobRepo.save(job);
        jobRepo.flush();
        return job;
    }

    /**
     * Delete job.
     * 
     * @param id job id to delete
     */
    public void deleteJob(String id) {
        jobRepo.deleteById(Long.parseLong(id));
        jobRepo.flush();
    }

    /**
     * Get Job by id.
     * 
     * @param id job id
     * @return job
     */
    public Job getJob(Long id) {
        return jobRepo.findById(id).orElse(null);
    }

    /**
     * Save job.
     * 
     * @param job job to save
     * @return saved job
     */
    public Job saveJob(Job job) {
        return jobRepo.save(job);
    }

    private Action createAction(String params, ActionType actionType) {
        Action action = null;
        try {
            action = (Action) createObject(actionType.getClassName());
        } catch (Throwable t) {
            throw new IllegalArgumentException(t.getMessage());
        }
        try {
            action.setParams(params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return action;
    }

    private ActionType getActionTypeById(Integer id) {
        ActionType actionType = null;
        try {
            actionType = actionRepo.getOne(id);
        } catch (EntityNotFoundException ex) {
            throw new IllegalArgumentException("action type is not found");
        }
        return actionType;
    }

    private Integer parseInt(String actionId, String name) {
        try {
            return Integer.parseInt(actionId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    name + " parameter is invalid: " + ex.getMessage());
        }
    }

    private Object createObject(String className) {
        try {
            Class<?> actionClass = Class.forName(className);
            Constructor<?> constructor = ClassUtils
                    .getConstructorIfAvailable(actionClass);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


}
