package edu.yu.capstone;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Implementations of this class publish this task to the queue
 */

public interface TaskPublisher {
    // Publish task to queue
    public void publish(TranscodingTask task) throws IOException, TimeoutException;
}
