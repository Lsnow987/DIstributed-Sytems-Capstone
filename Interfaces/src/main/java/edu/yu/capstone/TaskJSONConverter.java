/*
 * TaskJSONConverter class provides implementations for converting TranscodingTask objects to JSON strings
 * and vice versa using the Gson library.
 */
package edu.yu.capstone;

import com.google.gson.Gson;

/**
 * Provides implementations for converting TranscodingTask objects to JSON strings and vice versa using the Gson library.
 */
public class TaskJSONConverter implements TaskJSONConversionUtility {

    private final Gson gson;

    /**
     * Constructs a new TaskJSONConverter with a Gson instance.
     */
    public TaskJSONConverter() {
        this.gson = new Gson();
    }

    /**
     * Converts a TranscodingTask object to a JSON string.
     *
     * @param task The TranscodingTask object to be converted
     * @return The JSON string representing the TranscodingTask object
     */
    @Override
    public String taskToJSON(TranscodingTask task) {
        return gson.toJson(task);
    }

    /**
     * Converts JSON representing a task to a TranscodingTask object.
     *
     * @param taskJSON The JSON string representing the task
     * @return The TranscodingTask object parsed from the JSON
     */
    @Override
    public TranscodingTask jsonTask(String taskJSON) {
        return gson.fromJson(taskJSON, Task.class);
    }
}
