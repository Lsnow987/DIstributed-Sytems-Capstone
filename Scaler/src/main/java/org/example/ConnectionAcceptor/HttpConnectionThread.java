/*
 * HttpConnectionThread class is responsible for listening to incoming HTTP connections
 * and handling requests from nodes wishing to join the swarm.
 */
package org.example.ConnectionAcceptor;

import org.example.scaler.Scaler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HttpConnectionThread class implements a server that listens for HTTP connections
 * and handles requests from nodes wanting to join the swarm.
 */
public class HttpConnectionThread implements Runnable {
    private Scaler scaler; // Reference to the Scaler object for managing the swarm

    /**
     * Constructor to initialize HttpConnectionThread with a Scaler object.
     *
     * @param scaler The Scaler object for managing the swarm
     */
    public HttpConnectionThread(Scaler scaler) {
        this.scaler = scaler;
    }

    /**
     * Method to run the HTTP connection thread, listening for incoming connections
     * and handling requests from nodes.
     */
    @Override
    public void run() {
        try {
            // Create a server socket bound to port 8081
            ServerSocket serverSocket = new ServerSocket(8081);

            // Continuously listen for incoming connections
            while (true) {
                // Accept incoming connection
                Socket clientSocket = serverSocket.accept();

                // Start a new thread to handle the client request
                Thread requestHandlerThread = new Thread(new HttpRequestHandler(clientSocket));
                requestHandlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class HttpRequestHandler to handle incoming HTTP requests.
     */
    class HttpRequestHandler implements Runnable {
        private Socket clientSocket;

        /**
         * Constructor to initialize HttpRequestHandler with a client socket.
         *
         * @param clientSocket The client socket for handling the request
         */
        public HttpRequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Method to run the HTTP request handling thread.
         */
        @Override
        public void run() {
            try {
                // Get input and output streams from the client socket
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read the HTTP request
                StringBuilder requestBuilder = new StringBuilder();
                String line;
                String forwardedForIp = null; // Initialize forwardedForIp variable
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line).append("\r\n"); // Append CRLF for correct HTTP message format

                    // Check for X-Forwarded-For header
                    if (line.startsWith("X-Forwarded-For:")) {
                        // Extract the IP address from X-Forwarded-For header
                        forwardedForIp = line.substring(line.indexOf(":") + 1).trim();
                    }
                }
                String httpRequest = requestBuilder.toString();
                System.out.println("Request received:\n" + httpRequest);

                // Extract IP address from either X-Forwarded-For or the client socket
                String ipAddress;
                if (forwardedForIp != null && !forwardedForIp.isEmpty()) {
                    ipAddress = forwardedForIp;
                } else {
                    ipAddress = clientSocket.getInetAddress().getHostAddress();
                }

                // Call the addFreeIp method with the extracted IP address
                scaler.addFreeIp(ipAddress);

                // Print the extracted IP address
                if (forwardedForIp != null) {
                    System.out.println("X-Forwarded-For IP: " + forwardedForIp);
                } else {
                    System.out.println("Client IP: " + ipAddress);
                }

                // Send a simple response back to the client
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/plain");
                out.println();
                out.println("IP address added to freeIps list: " + ipAddress);

                // Close streams and socket
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}