/*
 * EventName enum represents the types of events that can occur during the processing of tasks.
 */
package edu.yu.capstone;

/**
 * Enum representing the types of events that can occur during the processing of tasks.
 */
public enum EventName {
    TASK_CREATED, // Event: Task object created
    TASK_SENT, // Event: Task successfully sent to the queue
    TASK_FALED_TO_SEND, // Event: Failure to send task to the queue
    TASK_RECEIVED, // Event: Task successfully received by a worker
    FAILED_TO_PULL_VIDEO, // Event: Failed to pull video for processing
    FAILED_TO_PROCESS_VIDEO, // Event: Failed to process video
    FAILED_TO_PUSH_VIDEO, // Event: Failed to push processed video
    VIDEO_PULLED, // Event: Video successfully pulled for processing
    VIDEO_PROCESSED, // Event: Video successfully processed
    VIDEO_PUSHED // Event: Processed video successfully pushed
}
