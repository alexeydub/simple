package com.sjms.simply.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sjms.simply.domain.QueueJob;

public interface QueueJobRepository extends JpaRepository<QueueJob, Long>,
        JpaSpecificationExecutor<QueueJob> {
}
