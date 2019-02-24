package com.sjms.simply.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class QueueJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private JobCommon jc;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Size(max=2048)
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public JobCommon getJc() {
        return jc;
    }

    public void setJc(JobCommon jobCommon) {
        this.jc = jobCommon;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
