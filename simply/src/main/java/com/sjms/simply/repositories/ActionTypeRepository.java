package com.sjms.simply.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjms.simply.domain.ActionType;

public interface ActionTypeRepository
        extends JpaRepository<ActionType, Integer> {

}
