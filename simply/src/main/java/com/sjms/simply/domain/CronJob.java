package com.sjms.simply.domain;

public class CronJob {
    
    private String groupName;
    private String jobName;
    private String description;
    private String dataMap;
    private String cronExpression;
    
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDataMap() {
        return dataMap;
    }
    public void setDataMap(String dataMap) {
        this.dataMap = dataMap;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
