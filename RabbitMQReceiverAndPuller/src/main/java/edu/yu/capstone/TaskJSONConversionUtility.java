/*
 * TaskJSONConversionUtility interface defines methods for converting between JSON and TranscodingTask objects.
 */
package edu.yu.capstone;

/**
 * Implementations of this interface streamline the conversion between JSON and TranscodingTask objects.
 */
public interface TaskJSONConversionUtility {
    /**
     * Converts a TranscodingTask object to a JSON string.
     *
     * @param task The TranscodingTask object to be converted
     * @return The JSON string representing the TranscodingTask object
     */
    String taskToJSON(TranscodingTask task);

    /**
     * Converts JSON representing a task to a TranscodingTask object.
     *
     * @param taskJSON The JSON string representing the task
     * @return The TranscodingTask object parsed from the JSON
     */
    TranscodingTask jsonTask(String taskJSON);
}
