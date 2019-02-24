package com.sjms.simply.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * Type of action performing by a {@link Job}.
 * 
 */
@Entity
public class ActionType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Size(max=250, min=1)
    @Column(unique=true)
    private String className;
    
    private String paramDescription;

    public ActionType() {
    }

    public ActionType(String className) {
        this.setClassName(className);
    }

    /**
     * @return action type id.
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return action class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return description for action parameters
     */
    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

}
