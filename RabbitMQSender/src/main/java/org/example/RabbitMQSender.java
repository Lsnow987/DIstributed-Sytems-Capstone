/*
 * RabbitMQSender class sends messages to a RabbitMQ queue for processing by worker classes.
 */
package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.yu.capstone.*;

import static java.lang.Thread.sleep;

/**
 * RabbitMQSender class sends messages to a RabbitMQ queue for processing by worker classes.
 */
public class RabbitMQSender {
    // Delay between sending messages (in seconds)
    private static final int DELAY_SECONDS = 30;

    /**
     * Main method to start sending messages to RabbitMQ queue.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        try{
            sleep(15_000); // Delay before starting to allow other services to initialize
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        // Retrieve RabbitMQ connection parameters from environment variables
        String host = System.getenv("RABBITMQ_HOST");
        String port = System.getenv("RABBITMQ_PORT");
        String username = System.getenv("RABBITMQ_USERNAME");
        String password = System.getenv("RABBITMQ_PASSWORD");
        String QUEUE_NAME = System.getenv("RABBITMQ_QUEUE_NAME");

        // RabbitMQ connection parameters
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(Integer.parseInt(port));
        factory.setUsername(username);
        factory.setPassword(password);

        // Executor service for scheduling message sending task
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        try {
            // Establish connection and channel
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            System.out.println(" [*] Sending message every " + DELAY_SECONDS + " seconds. To exit press CTRL+C");

            // Declare the queue
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // Schedule the message sending task
            executorService.scheduleAtFixedRate(() -> sendMessage(channel, QUEUE_NAME), 0, DELAY_SECONDS, TimeUnit.SECONDS);

            // Keep the main thread running to handle user interruption (CTRL+C)
            Thread.currentThread().join();
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shutdown the executor service when done
            executorService.shutdown();
        }
    }

    /**
     * Method to send a message to the RabbitMQ queue.
     *
     * @param channel   The RabbitMQ channel
     * @param QUEUE_NAME The name of the RabbitMQ queue
     */
    private static void sendMessage(Channel channel, String QUEUE_NAME) {
        TranscodingTask task = null;
        try {
            // Create a transcoding task and convert it to JSON
            Map<String,String> map= new HashMap();
            map.put("segment_000.mp4", "segments.zip");
            map.put("segment_001.mp4", "segments.zip");
            map.put("segment_002.mp4", "segments.zip");
            TaskStatus status= TaskStatus.PENDING;
            int jobId=1;
            int taskId=1;
            int requeueNumber=1;
            task= new Task(jobId,taskId,map,"testing2-yonatan-reiter","testing2-yonatan-reiter","480p","MergedZip11",status,0 );
            TaskJSONConversionUtility converter= new TaskJSONConverter();
            String json= converter.taskToJSON(task);

            // Publish the JSON message to the RabbitMQ queue
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes());
            System.out.println(" [x] Sent '" + json.toString() + "'");

            // Log the event of task being sent to the queue
            EventType eventType= EventType.STANDARD;
            EventName eventName= EventName.TASK_SENT;
            TaskSaver taskSaver = new TaskSaverImpl();
            taskSaver.save(task);
        } catch (IOException e) {
            e.printStackTrace();
//            // Log the event of failure to send task to the queue
//            TaskSaver taskSaver = new TaskSaverImpl();
//            taskSaver.save(task, EventType.ERROR, EventName.TASK_FALED_TO_SEND, "Failed to send task to queue");
        }
    }
}
