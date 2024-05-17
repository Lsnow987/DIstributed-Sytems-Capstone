/*
 * TranscodingTask interface represents a transcoding task.
 */
package edu.yu.capstone;

import java.util.Map;

/**
 * Represents a transcoding task.
 */
public interface TranscodingTask {

    /**
     * Gets the job ID of this task.
     *
     * @return The job ID of this task.
     */
    public String getJobId();

    /**
     * Gets the task ID of this task.
     *
     * @return The task ID of this task.
     */
    public String getTaskId();

    /**
     * Gets the map associated with this task.
     *
     * @return The map associated with this task.
     */
    public Map getMap();

    /**
     * Gets the bucket from which the resources will be pulled for this task.
     *
     * @return The bucket from which the resources will be pulled for this task.
     */
    public String getBucketPull();

    /**
     * Gets the bucket to which the resources will be pushed for this task.
     *
     * @return The bucket to which the resources will be pushed for this task.
     */
    public String getBucketPush();

    /**
     * Gets the resolution of the transcoding task.
     *
     * @return The resolution of the transcoding task.
     */
    public String getResolution();

    /**
     * Gets the upload name of the transcoding task.
     *
     * @return The upload name of the transcoding task.
     */
    public String getUploadName();

    /**
     * Gets the status of the transcoding task.
     *
     * @return The status of the transcoding task.
     */
    public TaskStatus getStatus();

    /**
     * Gets the number of times the task will be requeued if it fails.
     *
     * @return The number of times the task will be requeued if it fails.
     */
    public int getRequeueNumber();
}
