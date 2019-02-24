package com.sjms.simply.sevice;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.sjms.simply.Action;
import com.sjms.simply.domain.ActionType;
import com.sjms.simply.repositories.ActionTypeRepository;

@Service
public class ActionService {

    public static final String MUST_IMPLEMENTS_ACTION = "must implements action!";

    @Autowired
    private ActionTypeRepository actionRepo;

    /**
     * Create a new Action type.
     * 
     * @param className   class represents an {@link Action}
     * @param description action description
     * @return new Action type
     */
    public ActionType createActionType(String className, String description) {
        Object object = createObject(className);
        if (!(object instanceof Action)) {
            throw new IllegalArgumentException(
                    className + " " + MUST_IMPLEMENTS_ACTION);
        }
        Action action = (Action) object;
        if (description == null || description.trim().isEmpty()) {
            description = action.getDescription();
        }
        ActionType actionType = new ActionType(className);
        actionType.setParamDescription(description);
        actionType = actionRepo.save(actionType);
        actionRepo.flush();
        return actionType;
    }

    /**
     * Update Action type.
     * 
     * @param actionType
     * @return saved Action type
     */
    public ActionType updateActionType(ActionType actionType) {
        Object obj = createObject(actionType.getClassName());
        if (!(obj instanceof Action)) {
            throw new IllegalArgumentException(
                    actionType.getClassName() + " " + MUST_IMPLEMENTS_ACTION);
        }
        Action action = (Action) obj;
        String description = actionType.getParamDescription();
        if (description == null || description.trim().isEmpty()) {
            description = action.getDescription();
        }
        actionType.setParamDescription(description);
        actionType = actionRepo.save(actionType);
        actionRepo.flush();
        return actionType;
    }

    /**
     * Find action type by id.
     * 
     * @param id id
     * @return action type
     */
    public Optional<ActionType> findById(Integer id) {
        return actionRepo.findById(id);
    }

    /**
     * Get all action types.
     * 
     * @return all action types
     */
    public List<ActionType> findAll() {
        return actionRepo.findAll();
    }

    /**
     * Create action.
     *
     * @param className action class name
     * @param params action parameters
     * @return action or throws {@link IllegalArgumentException}
     */
    public Action getAction(String className, String params) {
        try {
            Class<?> actionClass = Class.forName(className);
            if (!Action.class.isAssignableFrom(actionClass)) {
                throw new IllegalArgumentException("Class " + className + " is not an Action");
            }
            Action action = null;
            try {
                action = (Action) actionClass.getConstructor().newInstance();
            } catch (Exception ex) {
                throw new IllegalArgumentException(className + " can't be instantiated! "
                        + ex.getMessage());
            }
            try {
                action.setParams(params);
            } catch (Throwable ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
            return action;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Action class " + className + " is not found!");
        }
    }


    /**
     * Create an Object from its class name.
     * 
     * @param className class name
     * @return class instance
     */
    private Object createObject(String className) {
        try {
            Class<?> actionClass = Class.forName(className);
            Constructor<?> constructor = ClassUtils
                    .getConstructorIfAvailable(actionClass);
            return constructor.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

}
