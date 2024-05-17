package edu.yu.capstone;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implementation of the FilePuller interface for pulling files from an AWS S3 bucket.
 */
public class S3FilePuller implements FilePuller {
    private String bucketName;
    private AmazonS3 s3Client;
    private final int MAX_RETRIES;
    private TranscodingTask task;
    String DB_HOST = System.getenv("DB_HOST");
    String DB_PORT = System.getenv("DB_PORT");
    private final String ENDPOINT = "http://" + DB_HOST + ":" + DB_PORT;

    /**
     * Constructor to initialize an S3FilePuller object.
     * @param bucketName Name of the AWS S3 bucket from which files will be pulled.
     * @param task The transcoding task associated with the files being pulled.
     */
    public S3FilePuller(String bucketName, TranscodingTask task) {
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
     * Downloads files from the AWS S3 bucket based on the provided map of segment names to object keys.
     * @param segmentsMap A map containing segment names as keys and corresponding object keys as values.
     * @return A list of downloaded files.
     */
    @Override
    public List<File> download(Map<String, String> segmentsMap) {
        List<File> downloadedFiles = new ArrayList<>();
        Set<String> processedObjectKeys = new HashSet<>();

        for (Map.Entry<String, String> entry : segmentsMap.entrySet()) {
            String segmentName = entry.getKey();
            String objectKey = entry.getValue();

            // Skip if this object key has already been processed
            if (processedObjectKeys.contains(objectKey)) {
                continue;
            }
            processedObjectKeys.add(objectKey);

            int retryCount = 0;
            boolean success = false;

            while (!success && retryCount < MAX_RETRIES) {
                try {
                    // Get the S3 object corresponding to the object key
                    S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey + ".zip"));
                    try (ZipInputStream zipStream = new ZipInputStream(s3Object.getObjectContent())) {
                        ZipEntry zipEntry;
                        while ((zipEntry = zipStream.getNextEntry()) != null) {
//                            System.out.println("Made it into the while loop for zipEntry: " + zipEntry.getName());
                            String nameNoExtension= zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf('.'));
                            // Check if this zipEntry matches any segment name for this object key
//                            System.out.println("segmentsMap.containsKey(nameNoExtension): " + segmentsMap.containsKey(nameNoExtension));
//                            System.out.println("segmentsMap.get(nameNoExtension): " + segmentsMap.get(nameNoExtension));
//                            System.out.println("segmentsMap.get(nameNoExtension).equals(objectKey): " + segmentsMap.get(nameNoExtension).equals(objectKey));
                            if (segmentsMap.containsKey(nameNoExtension) && segmentsMap.get(nameNoExtension).equals(objectKey)) {
//                                System.out.println("Made it into the if statement for zipEntry: " + nameNoExtension);
                                File outputFile = new File("/home/lsnow/upload/" + nameNoExtension + ".mp4");

                                // Ensure parent directories exist
                                File parentDir = outputFile.getParentFile();
                                if (!parentDir.exists()) {
//                                    System.out.println("Creating parent directory: " + parentDir);
                                    parentDir.mkdirs(); // This will create the directory structure if it doesn't exist
                                }

                                // Write the contents of the zip entry to the output file
                                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = zipStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, length);
                                    }
                                    downloadedFiles.add(outputFile);
                                    System.out.println("Downloaded file: " + outputFile.getAbsolutePath());
                                }
                            }
                            zipStream.closeEntry();
                        }
                    }
                    success = true; // Mark success if no exceptions were thrown
                } catch (AmazonServiceException | IOException e) {
                    e.printStackTrace();
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        System.out.println("Retrying in " + (retryCount * 2000) + " milliseconds...");
                        try {
                            Thread.sleep(retryCount * 2000L);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Thread interrupted during retry backoff", ex);
                        }
                    }
                }
            }

            if (!success) {
                // Handle failure to download the file after maximum retries
                TaskSaverImpl taskSaver = new TaskSaverImpl();
                //taskSaver.save(task/*, EventType.ERROR, EventName.FAILED_TO_PULL_VIDEO, "Failed to download " + objectKey + " from " + bucketName + " after " + MAX_RETRIES + " retries."*/);
                taskSaver.updateTask(task.getTaskId(), EventType.ERROR, EventName.FAILED_TO_PULL_VIDEO, TaskStatus.FAILED, task.getRequeueNumber() +1);

            }
        }

        return downloadedFiles;
    }
}
