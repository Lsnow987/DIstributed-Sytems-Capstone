/*
 * TaskSaver interface defines a method for updating the database with the latest data on a given task.
 */
package edu.yu.capstone;

/**
 * Implementations of this interface update the database with the latest data on the given task.
 */
public interface TaskSaver {
    /**
     * Saves the task to the database with the specified event type, event name, and message.
     *
     * @param task The TranscodingTask to be saved
     */
    void save(TranscodingTask task);

    /**
     * Updates the metadata of a task in the database.
     *
     * @param taskId      The ID of the task being updated
     * @param eventType   The new event type of the task
     * @param eventName   The new event name of the task
     * @param status      The new status of the task (optional, can be null to skip updating)
     * @param requeueNum  The new requeue number of the task (optional, can be null to skip updating)
     */
    void updateTask(int taskId, EventType eventType, EventName eventName, TaskStatus status, Integer requeueNum);
}
