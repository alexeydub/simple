package com.sjms.simply.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import com.sjms.simply.repositories.ActionTypeRepository;
import com.sjms.simply.sevice.ActionService;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ActionServiceTest {

    @Autowired
    private ActionTypeRepository actionRepo;

    @TestConfiguration
    static class ActionServiceTestConfig {

        @Bean
        public ActionService actionService() {
            return new ActionService();
        }
        
    }

    @Autowired
    private ActionService actionService;

    private Integer testActionId;

    @Before
    public void init() {
        ActionType action = new ActionType();
        action.setClassName("com.sjms.simply.TestAction");
        action.setParamDescription("no description");
        testActionId = actionRepo.save(action).getId();
        action = new ActionType();
        action.setClassName("com.sjms.simply.ParamAction");
        action.setParamDescription("must have message property");
        actionRepo.save(action);
    }

    @Test
    public void createActionFailed() {
        try {
            actionService.createActionType("com.test.Test", "simple 1");
            fail("Illegal argument exception is expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(ClassNotFoundException.class,
                    ex.getCause().getClass());
        }
        try {
            actionService.createActionType(Object.class.getCanonicalName(),
                    "simple 1");
            fail("Illegal argument exception is expected");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage()
                    .contains(ActionService.MUST_IMPLEMENTS_ACTION));
        }

        try {
            actionService.createActionType("com.example.testjpa.TestAction",
                    "");
            fail("DataIntegrity violation is expected");
        } catch (Throwable ex) {
            // do nothing
        }

    }

    @Test
    public void createActionType() {
        ActionType action = actionService.createActionType(
                "com.sjms.simply.service.TestAction", "must have JUnit");

        assertNotNull(action);
        assertEquals("must have JUnit", action.getParamDescription());
    }

    @Test
    public void updateActionType() {
        ActionType action = actionService.findById(testActionId).get();
        action.setParamDescription("new desc");
        action = actionService.updateActionType(action);
        assertEquals("new desc", action.getParamDescription());
    }
    
    @Test
    public void findAll() {
        List<ActionType> all = actionService.findAll();
        assertEquals(2, all.size());
    }
}
