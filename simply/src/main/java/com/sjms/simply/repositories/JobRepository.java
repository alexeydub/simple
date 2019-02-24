package com.sjms.simply.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sjms.simply.domain.Job;

public interface JobRepository
        extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

}
