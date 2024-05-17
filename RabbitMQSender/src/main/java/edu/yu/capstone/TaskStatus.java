/*
 * TaskStatus enum represents the possible status of a transcoding task.
 */
package edu.yu.capstone;

/**
 * Enum representing the possible status of a transcoding task.
 */
public enum TaskStatus {
    /**
     * Task is pending and has not yet started.
     */
    PENDING,
    /**
     * Task is currently in progress.
     */
    IN_PROGRESS,
    /**
     * Task has been completed successfully.
     */
    COMPLETED,
    /**
     * Task has failed to complete.
     */
    FAILED
}
