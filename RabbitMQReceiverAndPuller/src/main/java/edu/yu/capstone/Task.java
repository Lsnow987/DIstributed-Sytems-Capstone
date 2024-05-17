/*
 * Task class represents the transcoding task to be sent to the RabbitMQ queue for processing.
 */
package edu.yu.capstone;

import java.util.Map;

/**
 * Represents the transcoding task to be sent to the RabbitMQ queue for processing.
 */
public class Task implements TranscodingTask {
    private String jobId;
    private String taskId;
    private Map<String, String> segmentsToPartition;
    private String bucketPull;
    private String bucketPush;
    private String resolution;
    private String uploadName;
    private TaskStatus status;
    private int requeueNumber;

    /**
     * Constructs a new Task object with the specified parameters.
     *
     * @param jobId The ID of the job
     * @param taskId The ID of the task
     * @param segmentsToPartition The map of segment names to partition names
     * @param bucketPull The bucket from which to pull the video segments
     * @param bucketPush The bucket to which to push the processed video
     * @param resolution The resolution of the video
     * @param uploadName The name of the uploaded video
     * @param status The status of the task
     * @param requeueNumber The number of times to put this task back onto the queue if it fails
     */
    public Task(String jobId, String taskId, Map<String, String> segmentsToPartition, String bucketPull, String bucketPush, String resolution, String uploadName, TaskStatus status, int requeueNumber) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.segmentsToPartition = segmentsToPartition;
        this.bucketPull = bucketPull;
        this.bucketPush = bucketPush;
        this.resolution = resolution;
        this.uploadName = uploadName;
        this.status = status;
        this.requeueNumber = requeueNumber;
    }

    @Override
    public String getJobId() {
        return this.jobId;
    }

    @Override
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    public Map getMap() {
        return this.segmentsToPartition;
    }

    @Override
    public String getBucketPull() {
        return bucketPull;
    }

    @Override
    public String getBucketPush() {
        return this.bucketPush;
    }

    @Override
    public String getResolution() {
        return this.resolution;
    }

    @Override
    public String getUploadName() {
        return this.uploadName;
    }

    @Override
    public TaskStatus getStatus() {
        return this.status;
    }

    @Override
    public int getRequeueNumber() {
        return this.requeueNumber;
    }

    @Override
    public String toString() {
        return "Task{" +
                "jobId=" + jobId +
                ", taskId=" + taskId +
                ", segmentsToPartition=" + segmentsToPartition +
                ", bucketPull='" + bucketPull + '\'' +
                ", bucketPush='" + bucketPush + '\'' +
                ", resolution='" + resolution + '\'' +
                ", uploadName='" + uploadName + '\'' +
                ", status=" + status +
                ", requeueNumber=" + requeueNumber +
                '}';
    }
}
