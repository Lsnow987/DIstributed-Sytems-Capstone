package org.example;

import edu.yu.capstone.*;
import java.util.*;

public class TaskSavingDemo {
    public static void main(String[] args) {
        /*TaskSaver ts = new TaskSaverImpl();

        Map<String, String> dummyMap = Map.of(
                "segmentA", "partitionB",
                "segmentB", "partitionB"
        );

        TranscodingTask t1 = new Task(1, 1, dummyMap, "sourceBucket", "destBucket", "HD", "name1", TaskStatus.PENDING, 0);
        TranscodingTask t2 = new Task(1, 2, dummyMap, "sourceBucket", "destBucket", "HD", "name2", TaskStatus.IN_PROGRESS, 0);
        TranscodingTask t3 = new Task(1, 3, dummyMap, "sourceBucket", "destBucket", "HD", "name", TaskStatus.COMPLETED, 0);
        TranscodingTask t4 = new Task(2, 4, dummyMap, "sourceBucket", "destBucket", "HD", "name", TaskStatus.FAILED, 0);
        TranscodingTask t5 = new Task(2, 5, dummyMap, "sourceBucket", "destBucket", "HD", "name", TaskStatus.PENDING, 0);

        ts.save(t1, EventType.STANDARD, EventName.TASK_SENT, "yo");
        ts.save(t2, EventType.STANDARD, EventName.VIDEO_PULLED, "hey");
        ts.save(t3, EventType.STANDARD, EventName.VIDEO_PUSHED, "woohoo");
        ts.save(t4, EventType.ERROR, EventName.FAILED_TO_PROCESS_VIDEO, "uh oh");
        ts.save(t5, EventType.STANDARD, EventName.TASK_SENT, "waiting");

        try {
            Thread.sleep(90_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ts.updateTask(1, TaskStatus.IN_PROGRESS);
        ts.updateTask(2, TaskStatus.COMPLETED);
        ts.updateTask(5, TaskStatus.COMPLETED);

        try {
            Thread.sleep(90_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ts.updateTask(1, TaskStatus.FAILED);*/
    }
}