package com.graphbook.server.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

/**
 * The {@code PythonManager} class is responsible for managing and running
 * Python servers.
 * It retrieves configuration details from {@link GraphBookConfigManager} and
 * uses them to
 * start the specified Python servers.
 */
public class PythonManager {

    private final GraphBookConfigManager configManager;

    /**
     * Constructs a new {@code PythonManager} instance and initializes the
     * configuration manager.
     */
    public PythonManager() { // TODO Handle Null Paths
        configManager = new GraphBookConfigManager();
    }

    /**
     * Runs the AI Python server based on the configuration properties.
     * Retrieves the Python executable name, server directory, and file name from
     * the configuration.
     * Starts the Python process and redirects its output to the Java program's
     * output.
     * Throws a {@link RuntimeException} if any of the configuration properties are
     * null or empty.
     */
    public void runPythonAIServer() {
        if (isServerRunning(5000)) {
            return;
        }
        String pythonExecutableName = configManager.getProperty("Python", "PythonExecutable");
        String pythonServerDir = configManager.getProperty("Python", "AIPythonServerPath");
        String pythonFileName = configManager.getProperty("Python", "AIPythonServerFileName");

        if (pythonExecutableName == null || pythonExecutableName.isEmpty()) {
            throw new RuntimeException("Retrieved Executable Name was null or empty");
        }
        if (pythonServerDir == null || pythonServerDir.isEmpty()) {
            throw new RuntimeException("Retrieved Python Server Directory was null or empty");
        }
        if (pythonFileName == null || pythonFileName.isEmpty()) {
            throw new RuntimeException("Retrieved Python File Name was null or empty");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutableName, pythonFileName);
        processBuilder.directory(Paths.get(pythonServerDir).toFile());

        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        // Start the process and keep it running
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitForServerToStart();
    }

    /**
     * Runs the Plot Python server based on the configuration properties.
     * Retrieves the Python executable name, server directory, and file name from
     * the configuration.
     * Starts the Python process and redirects its output to the Java program's
     * output.
     * Throws a {@link RuntimeException} if any of the configuration properties are
     * null or empty.
     */
    public void runPythonPlotServer() {
        if (isServerRunning(50001)) {
            return;
        }
        String pythonExecutableName = configManager.getProperty("Python", "PythonExecutable");
        String pythonServerDir = configManager.getProperty("Python", "PlotPythonServerPath");
        String pythonFileName = configManager.getProperty("Python", "PlotPythonServerFileName");

        if (pythonExecutableName == null || pythonExecutableName.isEmpty()) {
            throw new RuntimeException("Retrieved Executable Name was null or empty");
        }
        if (pythonServerDir == null || pythonServerDir.isEmpty()) {
            throw new RuntimeException("Retrieved Python Server Directory was null or empty");
        }
        if (pythonFileName == null || pythonFileName.isEmpty()) {
            throw new RuntimeException("Retrieved Python File Name was null or empty");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutableName, pythonFileName);
        processBuilder.directory(Paths.get(pythonServerDir).toFile());

        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        // Start the process and keep it running
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitForServerToStart();
    }

    /**
     * Waits for the server to start by pausing the current thread for 5 seconds.
     * Handles {@link InterruptedException} by re-interrupting the current thread.
     */
    private void waitForServerToStart() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isServerRunning(int port) {
        try {
            // Create a URL object with the server's address
            URL url = new URL("http://localhost:" + port);
            // Open a connection to the server
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Connect to the server
            connection.connect();

            // Check the response code. If it's 200, the server is running
            if (connection.getResponseCode() == 200) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            // If an exception is caught, it means the server is not running or not
            // reachable
            return false;
        }
    }

    public static void main(String[] args) {
        new PythonManager().runPythonAIServer();
    }
}
