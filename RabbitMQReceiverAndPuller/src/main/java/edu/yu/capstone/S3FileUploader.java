package edu.yu.capstone;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * Implementation of the FileUploader interface for uploading files to an AWS S3 bucket.
 */
public class S3FileUploader implements FileUploader {
    private String bucketName;
    private AmazonS3 s3Client;
    private final int MAX_RETRIES; // Define the maximum number of retries
    TranscodingTask task;
    String DB_HOST = System.getenv("DB_HOST");
    String DB_PORT = System.getenv("DB_PORT");
    private final String ENDPOINT = "http://" + DB_HOST + ":" + DB_PORT;

    /**
     * Constructor to initialize an S3FileUploader object.
     * @param bucketName Name of the AWS S3 bucket to which files will be uploaded.
     * @param task The transcoding task associated with the files being uploaded.
     */
    public S3FileUploader(String bucketName, TranscodingTask task) {
        this.bucketName = bucketName;
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, ""))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
//                .withRegion(Regions.US_EAST_2)
                .build();
        this.MAX_RETRIES = Integer.parseInt(System.getenv("MAX_RETRIES"));
        this.task = task;
    }

    /**
     * Uploads a file to the AWS S3 bucket.
     * @param file The local file to upload.
     */
    public void upload(File file, String uploadFileName) {
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount < MAX_RETRIES) {
            try {
                // Upload the file to the S3 bucket
                s3Client.putObject(new PutObjectRequest(bucketName, uploadFileName, file));
                System.out.println("Successfully uploaded " + uploadFileName + " to " + bucketName);
                success = true;
                // Delete the uploaded file from the local system
                if (file.delete()) {
                    System.out.println("Deleted file: " + file.getPath());
                } else {
                    System.err.println("Failed to delete file: " + file.getPath());
                }
            } catch (AmazonServiceException e) {
                System.err.println("Error uploading " + file.getName() + ": " + e);
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    System.out.println("Retrying in " + (retryCount * 1000) + " milliseconds...");
                    try {
                        Thread.sleep(retryCount * 1000L);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry backoff", ex);
                    }
                }
            }
        }

        if (!success) {
            // Handle failure to upload the file after maximum retries
            TaskSaverImpl taskSaver = new TaskSaverImpl();
//            taskSaver.save(task/*, EventType.ERROR, EventName.FAILED_TO_PUSH_VIDEO, "Failed to upload " + file.getName()*/);
            taskSaver.updateTask(task.getTaskId(), EventType.ERROR, EventName.FAILED_TO_PUSH_VIDEO, TaskStatus.FAILED, task.getRequeueNumber() +1);
        }
    }
}
