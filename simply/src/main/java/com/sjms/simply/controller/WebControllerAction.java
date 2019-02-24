package com.sjms.simply.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sjms.simply.domain.ActionType;
import com.sjms.simply.sevice.ActionService;

@Controller
public class WebControllerAction {

    private static final Logger log = LoggerFactory
            .getLogger(WebController.class);

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = "/actions/details", method = RequestMethod.GET)
    public String getActionDetails(
            @RequestParam(name = "id", required = true) String actionId,
            Model model) {
        Integer id = ParameterUtils.parseIntDefault(actionId, null);
        if (id == null) {
            model.addAttribute("error", "wrong action id: " + actionId);
            return "actions";
        }
        Optional<ActionType> action = actionService.findById(id);
        if (!action.isPresent()) {
            model.addAttribute("error", "action not found! - " + actionId);
            return "actions";
        }
        model.addAttribute("action", action.get());
        return "actiondetails";
    }

    @RequestMapping(value = "/actions", method = RequestMethod.POST)
    public String createAction(
            @RequestParam(value = "className") String className,
            @RequestParam(value = "desc", required = false) String desc,
            Model model) {
        log.info("creating an action. className: " + className
                + " description: " + desc);
        try {
            ActionType actionType = actionService.createActionType(className,
                    desc);
            model.addAttribute("action", actionType);
            model.addAttribute("error", null);
        } catch (Throwable ex) {
            model.addAttribute("error",
                    "Unable to create action: " + ex.getMessage());
        }
        model.addAttribute("actions", actionService.findAll());
        return "actions";
    }

    @RequestMapping(value = "/actions", method = RequestMethod.GET)
    public String getActions(Model model) {
        model.addAttribute("actions", actionService.findAll());
        return "actions";
    }

    @RequestMapping(value = "/actions/details", method = RequestMethod.PATCH)
    public String updateAction(Model model,
            @RequestParam(name = "actionId", required = true) String actionId,
            @RequestParam(name = "description", required = false) String description) {
        Integer id = ParameterUtils.parseIntDefault(actionId, null);
        if (id == null) {
            model.addAttribute("error", "Wrong id: " + actionId);
            return "redirect:actions";
        }
        Optional<ActionType> ac = actionService.findById(id);
        if (!ac.isPresent()) {
            model.addAttribute("error",
                    "Action with " + actionId + " not found");
            return "redirect:actions";
        }
        ActionType act = ac.get();
        act.setParamDescription(description);
        act = actionService.updateActionType(act);
        model.addAttribute("action", act);
        return "actiondetails";
    }

}
