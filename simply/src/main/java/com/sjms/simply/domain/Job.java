package com.sjms.simply.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Job. Each Job perform an Action. Job has one of the 4 states: QUEUED,
 * RUNNING, SUCCESS, FAILED. QUEUED - Job in a queue to be executed. RUNNING -
 * job is executing now, SUCCESS - Job is executed, action complete. FAILED -
 * Job is executed, action is rolled back (Each Job should either complete
 * successfully or perform no action at all)
 */
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private JobCommon jc;
    
    public Long getId() {
        return id;
    }

    public JobCommon getJc() {
        return jc;
    }

    public void setJc(JobCommon jobCommon) {
        this.jc = jobCommon;
    }

}
