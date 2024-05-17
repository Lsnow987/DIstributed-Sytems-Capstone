/*
 * RabbitMQHandler class is responsible for interacting with RabbitMQ
 * to retrieve the number of messages in a specified queue.
 */
package org.example.rabbitmq;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * RabbitMQHandler class provides methods to interact with RabbitMQ
 * and retrieve information about queues.
 */
public class RabbitMQHandler {
    private final String rabbitMQUrl; // URL of the RabbitMQ server
    private final String username;    // Username for authentication
    private final String password;    // Password for authentication

    /**
     * Constructor to initialize RabbitMQHandler with RabbitMQ URL,
     * username, and password.
     *
     * @param rabbitMQUrl URL of the RabbitMQ server
     * @param username    Username for authentication
     * @param password    Password for authentication
     */
    public RabbitMQHandler(String rabbitMQUrl, String username, String password) {
        this.rabbitMQUrl = rabbitMQUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Method to get the count of messages in the specified queue.
     *
     * @param queueName Name of the queue to retrieve message count from
     * @return The number of messages in the queue, or -1 if an error occurs
     * @throws IOException If an I/O exception occurs while communicating with RabbitMQ
     */
    public int getMessageCount(String queueName) throws IOException {
        // Construct URL for RabbitMQ API to get queue information
        URL url = new URL(rabbitMQUrl + "/api/queues/%2F/" + queueName);
        System.out.println("URL: " + url);

        // Open connection to RabbitMQ API
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Set basic authentication
        String auth = username + ":" + password;
        String authHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", authHeaderValue);

        // Check response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getInt("messages");
        } else {
            // Handle error response
            System.err.println("HTTP GET request failed with response code: " + responseCode);
            return -1; // Indicate error
        }
    }
}
