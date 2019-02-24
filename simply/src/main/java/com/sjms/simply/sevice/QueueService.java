package com.sjms.simply.sevice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.JobCommon;
import com.sjms.simply.domain.QueueJob;
import com.sjms.simply.domain.Status;
import com.sjms.simply.repositories.JobRepository;
import com.sjms.simply.repositories.QueueJobRepository;

@Service
public class QueueService {

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private QueueJobRepository queueRepo;

    /**
     * return first top jobs ordered by priority.
     * 
     * @param count jobs count
     * @return job list
     */
    public List<QueueJob> topJobByPriotity(int count) {
        PageRequest pageRequest = PageRequest.of(0, count,
                Sort.by("jc.priority").descending());
        Page<QueueJob> pageJobs = null;
        Specification<QueueJob> specQueued = (root, query, cb) -> {
            return cb.equal(root.get("status"), Status.QUEUED);
        };
        pageJobs = queueRepo.findAll(specQueued, pageRequest);
        List<QueueJob> result = pageJobs.stream().collect(Collectors.toList());
        return result;
    }
    
    /**
     * Move job into the queue.
     *
     * @param jobId job id
     * @return job in the queue
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QueueJob moveJobToQueue(String jobId) {
        long id = Long.parseLong(jobId);
        Job job = jobRepo.findById(id).get();
        JobCommon jobCommon = job.getJc();
        
        QueueJob queueJob = new QueueJob();
        JobCommon q = new JobCommon();
        q.setActionType(jobCommon.getActionType());
        q.setDescription(jobCommon.getDescription());
        q.setName(jobCommon.getName());
        q.setParameters(jobCommon.getParameters());
        q.setPriority(jobCommon.getPriority());
        q.setTags(jobCommon.getTags());
        q.setCron(jobCommon.getCron());
        queueJob.setJc(q);
        queueJob.setStatus(Status.QUEUED);
        queueJob = queueRepo.save(queueJob);
        jobRepo.deleteById(id);
        return queueJob;
    }


    /**
     * Update job status from queued to running.
     *
     * @param job job
     * @return updated job
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean tryUpdateToRun(QueueJob job) {
        QueueJob dbJob = queueRepo.getOne(job.getId());
        if (!(Status.QUEUED == dbJob.getStatus())) {
            return false;
        }
        job.setStatus(Status.RUNNING);
        queueRepo.save(job);
        return true;
    }

    /**
     * QueueJob pagination request with filters.
     * 
     * @param criteria filters
     * @param page     page num
     * @param pageSize page size
     * @return selected page from filtered queue job list
     */
    public List<QueueJob> findByFilter(JobFilterCriteria<QueueJob> criteria,
            int page, int pageSize) {
        // result
        Page<QueueJob> pageJobs = null;
        // page request
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        // collect filters
        List<Specification<QueueJob>> filters = criteria.getAllSpecs();
        if (!filters.isEmpty()) {
            // create specification based on filters
            Specification<QueueJob> spec = null;
            for (Specification<QueueJob> specification : filters) {
                if (spec == null) {
                    spec = Specification.where(specification);
                } else {
                    spec = spec.and(specification);
                }
            }
            pageJobs = queueRepo.findAll(spec, pageRequest);
        } else {
            pageJobs = queueRepo.findAll(pageRequest);
        }
        // convert to list
        List<QueueJob> result = pageJobs.stream().collect(Collectors.toList());
        return result;
    }

    /**
     * Get job by id.
     *
     * @param id id
     * @return job
     */
    public QueueJob findJobById(Long id) {
        return queueRepo.getOne(id);
    }
}
