package com.sjms.simply.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjms.simply.domain.ActionType;
import com.sjms.simply.sevice.ActionService;

@RestController
public class ActionController {

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = "/api/actions/{id}", method = RequestMethod.GET)
    public ResponseEntity<ActionType> getActionDetails(
            @PathVariable(name = "id", required = true) String actionId) {
        Integer id = ParameterUtils.parseIntDefault(actionId, null);
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<ActionType> action = actionService.findById(id);
        if (!action.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(action.get());
    }

    @RequestMapping(value = "/api/actions", method = RequestMethod.POST)
    public ResponseEntity<ActionType> createAction(
            @RequestParam(value = "className") String className,
            @RequestParam(value = "desc", required = false) String desc) {
        ActionType actionType = actionService.createActionType(className, desc);
        return ResponseEntity.ok(actionType);
    }

    @RequestMapping(value = "/api/actions", method = RequestMethod.GET)
    public List<ActionType> getActions() {
        return actionService.findAll();
    }

    @RequestMapping(value = "/api/actions/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ActionType> updateAction(
            @PathVariable(name = "id", required = true) String actionId,
            @RequestParam(name = "description", required = false) String description) {
        Integer id = ParameterUtils.parseIntDefault(actionId, null);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<ActionType> atype = actionService.findById(id);
        if (!atype.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ActionType act = atype.get();
        act.setParamDescription(description);
        act = actionService.updateActionType(act);
        return ResponseEntity.ok(act);
    }

}
