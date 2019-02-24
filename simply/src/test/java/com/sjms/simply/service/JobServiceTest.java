package com.sjms.simply.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.sjms.simply.domain.ActionType;
import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.JobCommon;
import com.sjms.simply.repositories.JobRepository;
import com.sjms.simply.sevice.ActionService;
import com.sjms.simply.sevice.JobFilterCriteria;
import com.sjms.simply.sevice.JobService;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JobServiceTest {

    @Autowired
    private JobRepository jobRepo;

    @TestConfiguration
    static class JobServiceTestConfig {

        @Bean
        public JobService jobService() {
            return new JobService();
        }

        @Bean
        public ActionService actionService() {
            return new ActionService();
        }
    }

    @Autowired
    private JobService jobService;

    @Autowired
    private ActionService actionService;

    private String testActionId;
    private String testJobId;

    @Before
    public void init() {
        ActionType action = actionService.createActionType(
                "com.sjms.simply.service.TestAction", "must have JUnit");
        testActionId = "" + action.getId();

        Job job = new Job();
        JobCommon jc = new JobCommon();
        jc.setActionType(action);
        jc.setName("job1");
        jc.setDescription("description1");
        jc.setParameters("params1");
        jc.setPriority(1);
        job = saveNewJob(action, job, jc);
        testJobId = "" + job.getId();
        job = new Job();
        jc = new JobCommon();
        jc.setActionType(action);
        jc.setName("job2");
        jc.setDescription("description2");
        jc.setParameters("params2");
        saveNewJob(action, job, jc);
        job = new Job();
        jc = new JobCommon();
        jc.setActionType(action);
        jc.setName("name");
        jc.setDescription("description");
        jc.setTags("tag");
        saveNewJob(action, job, jc);
    }

    private Job saveNewJob(ActionType action, Job job, JobCommon jc) {
        job.setJc(jc);
        job = jobRepo.save(job);
        jobRepo.flush();
        return job;
    }

    @Test
    public void findByFilter() {
        JobFilterCriteria<Job> filter = new JobFilterCriteria<Job>("job", null,
                null);
        List<Job> jobList = jobService.findByFilter(filter, 0, 3);
        assertEquals(jobList.size(), 2);
        filter = new JobFilterCriteria<Job>(null, null, "desc");
        jobList = jobService.findByFilter(filter, 0, 3);
        assertEquals(jobList.size(), 3);
        filter = new JobFilterCriteria<Job>(null, "tag", null);
        jobList = jobService.findByFilter(filter, 0, 3);
        assertEquals(jobList.size(), 1);

    }

    @Test
    public void createJob() {
        Job job = jobService.createJob("" + testActionId, "job1", "description",
                "params", "1");
        assertEquals("job1", job.getJc().getName());
    }

    @Test
    public void deleteJob() {
        jobService.deleteJob(testJobId);
    }

    @Test
    public void deleteJobFail() {
        jobService.deleteJob(testJobId);
        try {
            jobService.deleteJob(testJobId);
            fail("Empty result is expected!");
        } catch (Throwable ex) {
            // do nothing
        }
    }

    @Test
    public void updateJob() {
        Long id = Long.parseLong(testJobId);
        Job job = jobService.getJob(id);
        job.getJc().setParameters("message=hello\nterminate=true");
        jobService.saveJob(job);
        job = jobService.getJob(id);
        assertEquals("message=hello\nterminate=true",
                job.getJc().getParameters());
    }

}
