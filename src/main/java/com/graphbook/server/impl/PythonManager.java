package com.graphbook.server.impl;

import java.io.IOException;
import java.nio.file.Paths;

import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

public class PythonManager {
    private final GraphBookConfigManager configManager;

    public PythonManager() {
        configManager = new GraphBookConfigManager();
    }
    
    /*
     *   "Python" : {
    "PythonEnvPath" : "C:/Users/macie/anaconda3/envs/GraphBookProjectPyEnv/python.exe",
    "PythonExecutable" : "python.exe",
    "AIPythonServerPath" : "C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/server",
    "PlotPythonServerPath" : "C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/server",
    "AIPythonServerFileName" : "plot_python_server.py",
    "PlotPythonServerFileName" : "ai_python_server.py"
  },
     */
    public void runPythonAIServer() {
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


        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutableName, pythonFileName); // TODO Handle null paths
        processBuilder.directory(Paths.get(pythonServerDir).toFile());
    
        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    

        // Start the process and keep it running
        try {
            processBuilder.start(); // Possible to keep the Process object to control the running server | return it?
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitForServerToStart();
    }

    public void runPythonPlotServer() {
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


        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutableName, pythonFileName); // TODO Handle null paths
        processBuilder.directory(Paths.get(pythonServerDir).toFile());
    
        // Redirect the process's output to the Java program's output
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    

        // Start the process and keep it running
        try {
            processBuilder.start(); // Possible to keep the Process object to control the running server | return it?
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitForServerToStart();
    }

    private void waitForServerToStart() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        new PythonManager().runPythonAIServer();
    }
}
