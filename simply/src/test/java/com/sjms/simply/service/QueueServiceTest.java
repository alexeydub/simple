package com.sjms.simply.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.sjms.simply.domain.JobCommon;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.domain.Status;
import com.sjms.simply.repositories.JobRepository;
import com.sjms.simply.repositories.QueueJobRepository;
import com.sjms.simply.sevice.ActionService;
import com.sjms.simply.sevice.JobService;
import com.sjms.simply.sevice.QueueService;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QueueServiceTest {

    @Autowired
    private QueueJobRepository queueRepo;
    
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
        
        @Bean
        public QueueService queueService() {
            return new QueueService();
        }
    }
    
    @Autowired
    private JobService jobService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private QueueService queueService;
    
    private Long aJobId;

    @Before
    public void init() {
        
        ActionType action = actionService.createActionType(
                "com.sjms.simply.service.TestAction", "must have JUnit");
        String actionId = "" + action.getId();

        aJobId = jobService.createJob("" + actionId, "fromJob", "", "", "30").getId();
        
        QueueJob job = new QueueJob();
        job.setStatus(Status.QUEUED);
        JobCommon jc = new JobCommon();
        jc.setName("job1");
        jc.setDescription("description1");
        jc.setParameters("params1");
        jc.setPriority(1);
        job = saveQueueNewJob(action, job, jc);
        job = new QueueJob();
        job.setStatus(Status.QUEUED);
        jc = new JobCommon();
        jc.setName("job2");
        jc.setDescription("description2");
        jc.setParameters("params2");
        jc.setPriority(20);
        saveQueueNewJob(action, job, jc);
        job = new QueueJob();
        job.setStatus(Status.QUEUED);
        jc = new JobCommon();
        jc.setName("name");
        jc.setDescription("description");
        jc.setTags("tag");
        saveQueueNewJob(action, job, jc);
    }
    
    private QueueJob saveQueueNewJob(ActionType action, QueueJob job, JobCommon jc) {
        jc.setActionType(action);
        job.setJc(jc);
        job = queueRepo.save(job);
        queueRepo.flush();
        return job;
    }
    
    @Test
    public void topJobByPriotity() {
        List<QueueJob> list = queueService.topJobByPriotity(1);
        assertEquals("job2", list.get(0).getJc().getName());
    }
    
    
    @Test
    public void moveJobToQueue() {
        jobRepo.flush();
        queueRepo.flush();
        QueueJob job = queueService.moveJobToQueue("" + aJobId);
        assertEquals("fromJob", job.getJc().getName());
        List<QueueJob> list = queueService.topJobByPriotity(1);
        assertEquals(job.getId(), list.get(0).getId());
    }

    
    @Test
    public void tryUpdateToRun() {
        List<QueueJob> list = queueService.topJobByPriotity(1);
        QueueJob job = list.get(0);
        boolean result = queueService.tryUpdateToRun(job);
        assertTrue(result);
        result = queueService.tryUpdateToRun(job);
        assertFalse(result);
    }
}
