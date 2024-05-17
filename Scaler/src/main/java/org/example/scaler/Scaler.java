/*
 * Scaler class manages the scaling up and down of nodes and containers based on received thresholds.
 */
package org.example.scaler;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scaler class manages the scaling up and down of nodes and containers based on received thresholds.
 */
public class Scaler {
    private final int SCALE_UP_THRESHOLD; // Threshold to trigger scaling up
    private final int SCALE_DOWN_THRESHOLD; // Threshold to trigger scaling down
    List<String> freeIps = new ArrayList<>(); // List of available IP addresses
    List<String> usedIps = new ArrayList<>(); // List of used IP addresses
    private final int MAX_INSTANCES_PER_MACHINE;

    /**
     * Constructor to initialize Scaler with scale up and scale down thresholds.
     *
     * @param SCALE_UP_THRESHOLD   Threshold to trigger scaling up
     * @param SCALE_DOWN_THRESHOLD Threshold to trigger scaling down
     */
    public Scaler(int SCALE_UP_THRESHOLD, int SCALE_DOWN_THRESHOLD) {
        this.SCALE_UP_THRESHOLD = SCALE_UP_THRESHOLD;
        this.SCALE_DOWN_THRESHOLD = SCALE_DOWN_THRESHOLD;
        this.MAX_INSTANCES_PER_MACHINE = System.getenv("MAX_INSTANCES_PER_MACHINE") == null ? 4 : Integer.parseInt(System.getenv("MAX_INSTANCES_PER_MACHINE"));
        String commaSeparatedIps = System.getenv("IP_ADDRESSES");
        if(commaSeparatedIps == null){
            return;
        }
        // Turn comma-separated string into a list
        String[] ips = commaSeparatedIps.split(",");
        freeIps.addAll(Arrays.asList(ips));
    }

    /**
     * Method to scale up or down based on the received scaling factor and service name.
     *
     * @param scalingFactor Scaling factor to determine whether to scale up or down
     * @param name          Name of the service to scale
     * @return 1 if scaling up, -1 if scaling down, 0 if no scaling needed
     * @throws IOException If an I/O exception occurs while executing Docker commands
     */
    public int scale(int scalingFactor, String name) throws IOException {
        if ( (scalingFactor > SCALE_UP_THRESHOLD && checkRatioOfInstancesPerMachineScalingUp(name)) ) {
            String message = System.getenv("DOCKER_TOKEN");
            // Remove any quotes from the message
            message = message.replaceAll("\"", "");
//            if(!sendMessageToFastAPI(message)){
//                return 0;
//            }
            sendMessageToFastAPI(message);
        }
        if(scalingFactor < SCALE_DOWN_THRESHOLD && checkRatioOfInstancesPerMachineScalingDown(name)){
            scaleDownOneNode();
        }
        // Scale Docker Swarm services based on message count
        if (scalingFactor > SCALE_UP_THRESHOLD && !checkRatioOfInstancesPerMachineScalingUp(name)) {
            System.out.println("Scaling up");
            scaleServices(1, name); // Scale up by 1 instance
            return 1;
        } else if (scalingFactor < SCALE_DOWN_THRESHOLD) {
            System.out.println("Scaling down");
            scaleServices(-1, name); // Scale down by 1 instance
            return -1;
        } else {
            // Do nothing
            System.out.println("No scaling needed.");
            return 0;
        }
    }

    /**
     * Method to check the ratio of instances to machines before scaling up.
     *
     * @param name Name of the service to check the ratio for
     * @return true if the ratio is sufficient for scaling up, false otherwise
     * @throws IOException If an I/O exception occurs while executing Docker commands
     */
    private boolean checkRatioOfInstancesPerMachineScalingUp(String name) throws IOException {
        // Implement your logic to check the ratio of instances to machines here
        // Return true if the ratio is 4 or more, otherwise false
        int numInstances = getNumInstances(name) + 1;
        int numMachines = getNumMachines();
        if (numInstances / numMachines >= this.MAX_INSTANCES_PER_MACHINE) {
            return true;
        }
        return false;
    }

    /**
     * Method to check the ratio of instances to machines before scaling down.
     *
     * @param name Name of the service to check the ratio for
     * @return true if the ratio is sufficient for scaling down, false otherwise
     * @throws IOException If an I/O exception occurs while executing Docker commands
     */
    private boolean checkRatioOfInstancesPerMachineScalingDown(String name) throws IOException {
        // Implement your logic to check the ratio of instances to machines here
        // Return true if the ratio is 4 or more, otherwise false
        int numInstances = getNumInstances(name) - 1;
        int numMachines = getNumMachines();
        if (numInstances / numMachines <= 1) {
            return true;
        }
        return false;
    }

    /**
     * Method to get the current number of instances for a given service.
     *
     * @param name Name of the service
     * @return Number of instances for the service
     * @throws IOException If an I/O exception occurs while executing Docker commands
     */
    private int getNumInstances(String name) throws IOException {
        // Execute Docker command to get the current number of instances
        String command = "docker service inspect --format='{{.Spec.Mode.Replicated.Replicas}}' " + name;
        Process process = Runtime.getRuntime().exec(command);

        // Read the current number of instances from the output of the Docker command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.readLine();
        System.err.println("Output: " + output);
        output = output.replaceAll("'", ""); // Remove single quotes from the output
        return Integer.parseInt(output.trim());
    }

    /**
     * Method to get the current number of machines in the swarm.
     *
     * @return Number of machines in the swarm
     * @throws IOException If an I/O exception occurs while executing Docker commands
     */
    public int getNumMachines() throws IOException {
        int count = usedIps.size() + 1;
        System.out.println("Number of hosts: " + count);
        return count;
    }

    /**
     * Method to scale services based on the scaling factor.
     *
     * @param scalingFactor Scaling factor to determine the number of instances to scale by
     * @param name          Name of the service to scale
     */
    public void scaleServices(int scalingFactor, String name) {
        try {
            int currentInstances = getNumInstances(name);

            // Calculate the desired number of instances after scaling
            int desiredInstances = currentInstances + scalingFactor;
            if(desiredInstances < 1){
                System.out.println("Cannot scale below 1 instance");
                return;
            }

            // Execute Docker command to scale services
            String command = "docker service scale myapp_receiver=" + desiredInstances;
            Process process = Runtime.getRuntime().exec(command);

            // Capture both standard output and error output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Wait for the process to finish
            int exitCode = process.waitFor();

            System.err.println("Scaling by " + scalingFactor);

            // Print any output of the command
            String line;
            while ((line = stdInput.readLine()) != null) {
                System.out.println(line);
            }

            // Print any error output of the command
            while ((line = stdError.readLine()) != null) {
                System.err.println(line);
            }

            if (exitCode == 0) {
                System.out.println("Scaling successful");
            } else {
                System.err.println("Scaling failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to scale down one node from the swarm.
     */
    public void scaleDownOneNode() {
        try {
            // Execute Docker command to list running nodes
            ProcessBuilder listNodesProcessBuilder = new ProcessBuilder("docker", "node", "ls", "--format", "{{.ID}}");
            Process listNodesProcess = listNodesProcessBuilder.start();
            listNodesProcess.waitFor();

            // Read the output of the Docker command
            BufferedReader reader = new BufferedReader(new InputStreamReader(listNodesProcess.getInputStream()));
            String line = reader.readLine();
            System.out.println("Line: " + line);
            if (line != null) {
                String nodeToRemove = line.trim();

                // Get the IP address of the node

                String ipAddress = null;
                try {
                    ipAddress = usedIps.get(0);
                } catch (Exception e) {
                }
                if(ipAddress == null){
                    System.out.println("No IP address found for node: " + nodeToRemove);
                    return;
                }
                System.out.println("IP Address: " + ipAddress);

                // Execute Docker command to drain the node
                ProcessBuilder drainProcessBuilder = new ProcessBuilder("docker", "node", "update", "--availability", "drain", nodeToRemove);
                Process drainProcess = drainProcessBuilder.start();
                drainProcess.waitFor();

                // Update IP lists
                if (ipAddress != null) {
                    usedIps.remove(ipAddress);
                    freeIps.add(ipAddress);
                }

                sendMessageToFastAPI("docker swarm leave --force", ipAddress);

                System.out.println("Node " + nodeToRemove + " scaled down.");
            } else {
                System.out.println("No running nodes to scale down.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to retrieve the IP address of a node in the swarm.
     *
     * @param nodeId ID of the node
     * @return IP address of the node
     */
//    private static String getIpAddressOfNode(String nodeId) {
//        try {
//            ProcessBuilder inspectNodeProcessBuilder = new ProcessBuilder("docker", "node", "inspect", "--format", "{{.Status.Addr}}", nodeId);
//            Process inspectNodeProcess = inspectNodeProcessBuilder.start();
//            inspectNodeProcess.waitFor();
//
//            // Read the output of the Docker command to get the IP address
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inspectNodeProcess.getInputStream()));
//            String ipAddress;
//            if ((ipAddress = reader.readLine()) != null) {
//                return ipAddress.trim();
//            } else {
//                System.err.println("Failed to retrieve IP address of node: " + nodeId);
//                return null;
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Method to send a message to FastAPI.
     *
     * @param command Command to send to FastAPI
     */
    public boolean sendMessageToFastAPI(String command) {
        try {
            // Prepare JSON payload
            System.out.println("Sending message to FastAPI: " + command);
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("command", command);
            String ip = getAvailableIp();
            if (ip == null) {
                System.out.println("No free IPs available");
                return false;
            }
            String FASTAPI_ENDPOINT = "http://" + ip + ":8000/scale/";
            sendRequest(FASTAPI_ENDPOINT, jsonPayload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Method to retrieve an available IP address.
     *
     * @return An available IP address
     */
    private String getAvailableIp() {
        if (freeIps.isEmpty()) {
            return null;
        }
        String ip = freeIps.remove(0);
        usedIps.add(ip);
        return ip;
    }

    /**
     * Method to send a message to FastAPI with a specified IP address.
     *
     * @param command Command to send to FastAPI
     * @param ip      IP address to send the message to
     */
    private void sendMessageToFastAPI(String command, String ip) {
        try {
            // Prepare JSON payload
            System.out.println("Sending message to FastAPI: " + command);
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("command", command);
            String FASTAPI_ENDPOINT = "http://" + ip + ":8000/scale/";
            sendRequest(FASTAPI_ENDPOINT, jsonPayload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to send an HTTP request with JSON payload to a specified endpoint.
     *
     * @param endpoint    Endpoint to send the request to
     * @param jsonPayload JSON payload to include in the request
     * @throws IOException If an I/O exception occurs while sending the request
     */
    private void sendRequest(String endpoint, JSONObject jsonPayload) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Write JSON payload to the connection's output stream
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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
            System.out.println("Message sent to FastAPI successfully");
        } else {
            System.out.println("HTTP POST request to FastAPI failed with response code: " + responseCode);
            // get more details about the error
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Error: " + response.toString());
        }
    }

    /**
     * Method to add an IP address to the list of free IPs.
     *
     * @param ip IP address to add to the list of free IPs
     */
    public void addFreeIp(String ip) {
        freeIps.add(ip);
    }

    /**
     * Method to remove an IP address from the list of free IPs.
     *
     * @param ip IP address to remove from the list of free IPs
     */
    public void removeFreeIp(String ip) {
        freeIps.remove(ip);
    }
}
