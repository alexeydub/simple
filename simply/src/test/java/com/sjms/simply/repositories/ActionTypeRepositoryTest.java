package com.sjms.simply.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sjms.simply.domain.ActionType;
import com.sjms.simply.repositories.ActionTypeRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ActionTypeRepositoryTest {

    @Autowired
    private ActionTypeRepository actionRepo;
    
    @Test
    public void testGetMethods() {
        // given
        ActionType action = new ActionType();
        action.setClassName("com.example.testjpa.TestAction");
        action.setParamDescription("do description");
        int id = actionRepo.save(action).getId();
        action = new ActionType();
        action.setClassName("com.example.testjpa.ParamAction");
        action.setParamDescription("must have message property");
        actionRepo.save(action);
     
        // when
        List<ActionType> list = actionRepo.findAll();
        Optional<ActionType> actionOpt = actionRepo.findById(id);
        
        // then
        assertEquals(2, list.size());
        assertTrue(actionOpt.isPresent());
        assertEquals("com.example.testjpa.TestAction", actionOpt.get().getClassName());
    }
}
