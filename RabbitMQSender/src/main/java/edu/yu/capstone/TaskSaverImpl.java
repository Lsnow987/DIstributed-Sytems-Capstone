/*
 * TaskSaverImpl class provides implementations for saving TranscodingTask objects to a database.
 */
package edu.yu.capstone;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;


/**
 * Provides implementations for saving TranscodingTask objects to a database.
 */
public class TaskSaverImpl implements TaskSaver{
    Channel channel;
    String QUEUE_NAME;

    public void setUp(Connection connection)  throws Exception{
//        String rabbitHost = System.getenv("RABBITMQ_HOST");
//        String port = System.getenv("RABBITMQ_PORT");
//        String username = System.getenv("RABBITMQ_USERNAME");
//        String password = System.getenv("RABBITMQ_PASSWORD");
        QUEUE_NAME = System.getenv("RABBITMQ_LOGGER_QUEUE_NAME");

//        // RabbitMQ connection parameters
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(rabbitHost);
//        factory.setPort(Integer.parseInt(port));
//        factory.setUsername(username);
//        factory.setPassword(password);

//        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        // Declare the queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    /**
     * Saves the TranscodingTask to the database with the specified event type, event name, and message.
     *
     * @param task The TranscodingTask to be saved
     */
    @Override
    public void save(TranscodingTask task) {
        String isDBSetup = System.getenv("IS_DB_SETUP");
        boolean isDBSetupBool = Boolean.parseBoolean(isDBSetup);
        if (!isDBSetupBool) {
            System.out.println("Database is not setup. Defaulting to NoOpTaskSaver.");
            return;
        }
        String host = System.getenv("MONGODB_HOST");
        final String BASE_URL = "http://" + host + ":8080"; // Base URL of the API

        RestTemplate restTemplate = new RestTemplate(); // Create a RestTemplate instance

        // Configure MappingJackson2HttpMessageConverter for JSON conversion
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TranscodingTask> request = new HttpEntity<>(task, headers);
        // Make POST request to create a task
        ResponseEntity<TranscodingTask> responseEntity = restTemplate.postForEntity(BASE_URL + "/tasks", request, TranscodingTask.class);
        TranscodingTask createdTask = responseEntity.getBody();


        TaskJSONConversionUtility converter= new TaskJSONConverter();
        String json= converter.taskToJSON(task);

        // Publish the JSON message to the RabbitMQ queue
        try {
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Created Task: " + createdTask);
    }

    public record TaskUpdateRequest(EventType eventType, EventName eventName, TaskStatus status, Integer requeueNum) {}

    // They need an endpoint that supports this

    /**
     * Updates the metadata of a task in the database.
     *
     * @param taskId      The ID of the task being updated
     * @param eventType   The new event type of the task
     * @param eventName   The new event name of the task
     * @param status      The new status of the task (optional, can be null to skip updating)
     * @param requeueNum  The new requeue number of the task (optional, can be null to skip updating)
     */
    @Override
    public void updateTask(int taskId, EventType eventType, EventName eventName, TaskStatus status, Integer requeueNum) {
        String isDBSetup = System.getenv("IS_DB_SETUP");
        boolean isDBSetupBool = Boolean.parseBoolean(isDBSetup);
        if (!isDBSetupBool) {
            System.out.println("Database is not setup. Defaulting to NoOpTaskSaver.");
            return;
        }
        String host = System.getenv("MONGODB_HOST");
        final String BASE_URL = "http://" + host + ":8080"; // Base URL of the API

        RestTemplate restTemplate = new RestTemplate(); // Create a RestTemplate instance

        // Configure MappingJackson2HttpMessageConverter for JSON conversion
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a DTO object representing the update request
        TaskUpdateRequest updateRequest = new TaskUpdateRequest(eventType, eventName, status, requeueNum);

        // Create the request entity with the update request object
        HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateRequest, headers);

        // Make PUT request to update the task metadata
        restTemplate.put(BASE_URL + "/tasks/" + taskId + "/metadata", request);

        TaskUpdate taskUpdate = new TaskUpdate(taskId, eventType, eventName, status, requeueNum);
        UpdateTaskJSONConverter converter = new UpdateTaskJSONConverter();
        String json= converter.taskUpdateToJSON(taskUpdate);
        try {
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Updated Task Metadata: Task ID - " + taskId + ", New Event Type - " + eventType +
                ", New Event Name - " + eventName + ", New Status - " + status + ", New Requeue Number - " + requeueNum);
    }

}
