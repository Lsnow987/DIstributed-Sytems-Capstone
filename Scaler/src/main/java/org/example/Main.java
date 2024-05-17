/*
 * Main class orchestrates the scaling process based on message counts in RabbitMQ queue and handles HTTP connections.
 */
package org.example;

import org.example.ConnectionAcceptor.HttpConnectionThread;
import org.example.rabbitmq.RabbitMQHandler;
import org.example.scaler.Scaler;

import static java.lang.Thread.sleep;

/**
 * Main class orchestrates the scaling process based on message counts in RabbitMQ queue and handles HTTP connections.
 */
public class Main {
    private static Scaler scaler;

    /**
     * Main method starts the scaling process and handles HTTP connections.
     *
     * @param args Command-line arguments
     * @throws InterruptedException If the thread sleep is interrupted
     */
    public static void main(String[] args) throws InterruptedException {

        int DELAY_TIME = Integer.parseInt(System.getenv("DELAY_TIME"));
        try {
            sleep(DELAY_TIME); // Delay before starting to allow other services to initialize
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Retrieve environment variables
        String RABBITMQ_USERNAME = System.getenv("RABBITMQ_USERNAME");
        String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD");
        String QUEUE_NAME = System.getenv("RABBITMQ_QUEUE_NAME");
        String RABBITMQ_HOST = System.getenv("RABBITMQ_HOST");
        String RABBITMQ_PORT = System.getenv("RABBITMQ_PORT");
        String RABBITMQ_URL = "http://" + RABBITMQ_HOST + ":" + RABBITMQ_PORT;
        int SLEEP_TIME = Integer.parseInt(System.getenv("SLEEP_TIME")); // Time to sleep between each iteration
        String containerName = System.getenv("CONTAINER_NAME");
        int SCALE_UP_THRESHOLD = Integer.parseInt(System.getenv("SCALE_UP_THRESHOLD"));
        int SCALE_DOWN_THRESHOLD = Integer.parseInt(System.getenv("SCALE_DOWN_THRESHOLD"));

        // Initialize RabbitMQ handler and Scaler
        RabbitMQHandler rabbitMQManager = new RabbitMQHandler(RABBITMQ_URL, RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
        scaler = new Scaler(SCALE_UP_THRESHOLD, SCALE_DOWN_THRESHOLD);

        // Start a new thread to handle HTTP connections
        Thread httpConnectionThread = new Thread(new HttpConnectionThread(scaler));
        httpConnectionThread.start();
        System.out.println("HTTP connection thread started.");


//        sleep(60_000);
//        String message = System.getenv("DOCKER_TOKEN");
//        // Remove any quotes from the message
//        message = message.replaceAll("\"", "");
//        scaler.sendMessageToFastAPI(message);
//        scaler.scaleServices(1, containerName);
//        scaler.scaleServices(1, containerName);
//        scaler.scaleServices(1, containerName);
//        System.out.println("Services scaled up - done now");
//        sleep(250_000);
//        System.out.println("Services scaled up - done now - gonna start scaling down now");




        // Continuously check the message count in the RabbitMQ queue
        while (true) {
            try {
                // Get message count from RabbitMQ queue
                int messageCount = rabbitMQManager.getMessageCount(QUEUE_NAME);
                System.out.println("Message count: " + messageCount);
                // Scale based on message count
                int success = scaler.scale(messageCount, containerName);
                System.out.println("Scaling: " + success + " 0 means no scaling, 1 means scaling up, -1 means scaling down.");
                Thread.sleep(SLEEP_TIME); // Wait for a specified time before making the next scaling decision
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(10000); // Wait for 10 seconds before retrying in case of exception
            }
        }
    }
}
