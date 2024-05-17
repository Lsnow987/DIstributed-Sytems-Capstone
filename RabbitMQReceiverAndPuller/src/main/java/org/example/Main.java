package org.example;

import com.rabbitmq.client.*;
import edu.yu.capstone.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;
import static java.lang.Thread.sleep;

public class Main {

    // Define the size of the thread pool for concurrent processing
    private static final int THREAD_POOL_SIZE = 3;

    // Initialize the TaskSaverImpl instance
    static TaskSaverImpl taskSaver = new TaskSaverImpl();

    public static void main(String[] args) {
        try{
            // Pause for 15 seconds to allow environment setup
            sleep(15_000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        // Begin processing messages
        processMessages();
    }

    // Method to process messages received from RabbitMQ
    private static void processMessages() {
        // Create a fixed-size thread pool for concurrent processing
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // RabbitMQ connection parameters
        ConnectionFactory factory = new ConnectionFactory();
        String host = System.getenv("RABBITMQ_HOST");
        String port = System.getenv("RABBITMQ_PORT");
        String username = System.getenv("RABBITMQ_USERNAME");
        String password = System.getenv("RABBITMQ_PASSWORD");
        String queueName = System.getenv("RABBITMQ_QUEUE_NAME");
        factory.setHost(host);
        factory.setPort(Integer.parseInt(port));
        factory.setUsername(username);
        factory.setPassword(password);

        try {
            // Establish connection and channel
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            // Declare the queue
            channel.queueDeclare(queueName, false, false, false, null);
            try{
                taskSaver.setUp(connection);
            } catch(Exception e) {
                e.printStackTrace();
            }

            // Keep the program running until interrupted
            while (true) {
                // Wait for a message
                GetResponse response = channel.basicGet(queueName, false);
                if (response == null) {
                    // No message available, sleep for a while before trying again
                    sleep(1000);
                    continue;
                }

                // Convert message to string
                String file = new String(response.getBody(), StandardCharsets.UTF_8);
//                System.out.println(" [x] Received '" + file + "'");


                // Convert JSON message to TranscodingTask object
                TaskJSONConversionUtility converter = new TaskJSONConverter();
                TranscodingTask task = converter.jsonTask(file);
                System.out.println("received task: " + task.toString());

                // Save the received task event
                //taskSaver.save(task/*, EventType.STANDARD, EventName.TASK_RECEIVED, "Task received"*/);
                taskSaver.updateTask(task.getTaskId(), EventType.STANDARD, EventName.TASK_RECEIVED, TaskStatus.IN_PROGRESS, task.getRequeueNumber() +1);

                // Retrieve task details
                String bucketPullname = task.getBucketPull();
                String bucketPushname = task.getBucketPush();
                String resolution = task.getResolution();
                String uploadName = task.getUploadName();
                Map<String, String> segmentDetails = task.getMap();

                // Pull files from S3 bucket
                FilePuller fp = new S3FilePuller(bucketPullname, task);
//                System.err.println("segment details: " +segmentDetails.toString());
                List<File> fileList = fp.download(segmentDetails);
//                System.err.println("file list: " + fileList.toString());
                // Save the downloaded event
                taskSaver.updateTask(task.getTaskId(), EventType.STANDARD, EventName.VIDEO_PULLED, TaskStatus.IN_PROGRESS, task.getRequeueNumber() +1);

                // List to store output paths
                List<String> outputsPath = new ArrayList<>();
                VideoTranscoder vt = new VideoTranscoderImpl(bucketPushname, task);
                // Transcode each file and add its output path to the list
                for(File f: fileList) {
                    String dir = "/home/lsnow/transcode/";
                    String outputPath = f.getName().replace(".mp4", ".ts");
                    outputPath = dir + outputPath;
                    outputsPath.add(outputPath);
                    vt.transcodeVideo(resolution, f.getAbsolutePath(), f.getName());
                }
                // Save the transcoded event
                //taskSaver.save(task/*, EventType.STANDARD, EventName.VIDEO_PROCESSED, "Task transcoded"*/);
                taskSaver.updateTask(task.getTaskId(), EventType.STANDARD, EventName.VIDEO_PROCESSED, TaskStatus.IN_PROGRESS, task.getRequeueNumber() +1);

                // Path for the zip file
                String zipFilePath = "/home/lsnow/transcode/" + uploadName + ".zip";

                try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                     ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                    // Add each transcoded file to the zip
                    for (String filePath : outputsPath) {
//                        System.out.println("Adding file to upload zip: " + filePath);
                        File fileToZip = new File(filePath);
                        try (FileInputStream fis = new FileInputStream(fileToZip)) {
                            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
//                            System.out.println("Adding upload zip entry: " + zipEntry.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                        }
                        // Delete the original file after adding it to the zip
                        if (!fileToZip.delete()) {
                            throw new RuntimeException("File not deleted");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Upload the zip file to the bucket
                executorService.submit(() -> {
                    try {
                        FileUploader fu = new S3FileUploader(bucketPushname, task);
                        fu.upload(new File(zipFilePath), uploadName + ".zip");
                        // Acknowledge the message from RabbitMQ
                        channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                        // Save the uploaded event
//                        taskSaver.save(task/*, EventType.STANDARD, EventName.VIDEO_PUSHED, "Task uploaded"*/);
                        taskSaver.updateTask(task.getTaskId(), EventType.STANDARD, EventName.VIDEO_PUSHED, TaskStatus.IN_PROGRESS, task.getRequeueNumber() +1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


////    // Method to consume messages from RabbitMQ
//    private static void processMessages() {
//        ConnectionFactory factory = new ConnectionFactory();
//        PropertiesLoader configLoader = new PropertiesLoader();
//        String host = configLoader.getRabbitHost();
//        String port = configLoader.getRabbitPort();
//        String username = configLoader.getRabbitUsername();
//        String password = configLoader.getRabbitPassword();
//        factory.setHost(host);
//        factory.setPort(Integer.parseInt(port));
//        factory.setUsername(username);
//        factory.setPassword(password);
//        try {
//            // Establish connection and channel
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//
//            // Declare the queue
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//
//            // Callback to handle received messages
//            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                // Convert message to string
//                String file = new String(delivery.getBody(), StandardCharsets.UTF_8);
//                System.out.println(" [x] Received '" + file + "'");
//
//                // Convert received string back to JSON object
//                JSONObject jsonObject = new JSONObject(file);
//
//                // Retrieve the parameters from JSON object
//                String keyToMongo = jsonObject.getString("keyToMongo");
//                String bucketPullname = jsonObject.getString("bucketPullname");
//                String videoKey = jsonObject.getString("videoKey");
//                String bucketPushname = jsonObject.getString("bucketPushname");
//                String resolution = jsonObject.getString("resolution");
//                String uploadName = jsonObject.getString("uploadName");
//
//                // Now you can use these parameters as needed
//                System.out.println("keyToMongo: " + keyToMongo);
//                System.out.println("bucketPullname: " + bucketPullname);
//                System.out.println("videoKey: " + videoKey);
//                System.out.println("bucketPushname: " + bucketPushname);
//                System.out.println("resolution: " + resolution);
//                System.out.println("uploadName: " + uploadName);
//
//                // Perform S3 operations and transcoding using these parameters
//                pullFromS3(bucketPullname, videoKey);
//                transcodeFile(resolution);
//                pushToS3(bucketPushname, uploadName);
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            };
//
//            // Start consuming messages
//            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
//
//            // Keep the program running until interrupted
//            while (true) {
//                Thread.sleep(1000);
//            }
//
//        } catch (IOException | TimeoutException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
