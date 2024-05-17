package edu.yu.capstone;

public class TaskUpdate {
    //int taskId, EventType eventType, EventName eventName, TaskStatus status, Integer requeueNum

    private String taskId;
    private EventType eventType;
    private EventName eventName;
    private TaskStatus status;
    private Integer requeueNum;

    public TaskUpdate(String taskId, EventType eventType, EventName eventName, TaskStatus status, Integer requeueNum) {
        this.taskId = taskId;
        this.eventType = eventType;
        this.eventName = eventName;
        this.status = status;
        this.requeueNum = requeueNum;
    }

    public String getTaskId() {
        return taskId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventName getEventName() {
        return eventName;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Integer getRequeueNum() {
        return requeueNum;
    }
}
