/*
 * TaskJSONConverter class provides implementations for converting TranscodingTask objects to JSON strings
 * and vice versa using the Gson library.
 */
package edu.yu.capstone;

import com.google.gson.Gson;

/**
 * Provides implementations for converting TranscodingTask objects to JSON strings and vice versa using the Gson library.
 */
public class UpdateTaskJSONConverter {

    private final Gson gson;

    public UpdateTaskJSONConverter() {
        this.gson = new Gson();
    }

    public String taskUpdateToJSON(TaskUpdate taskUpdate) {
        return gson.toJson(taskUpdate);
    }

    public TaskUpdate jsonTask(String taskJSON) {
        return gson.fromJson(taskJSON, TaskUpdate.class);
    }
}
