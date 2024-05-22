package com.graphbook.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import com.graphbook.GraphBook;
import com.graphbook.util.interfaces.IGraphBookInitializer;

public class SimpleGraphBookInitializer implements IGraphBookInitializer {
    

    public void setProjectPath(File chosenDir) {
        // TODO null handling
        Properties properties = new Properties();
        properties.setProperty("PROJECT_PATH", chosenDir.toPath().toString());
        File configDir = new File(CONSTANTS.CONFIG_PATH.toString());
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        try (FileOutputStream out = new FileOutputStream(CONSTANTS.CONFIG_PATH.toString())) {
            properties.store(out, null);
        } catch (IOException e) {
            LogManager.getLogger(GraphBook.class).error("IOException occured while writing file into the properties file", e.getMessage(), e);
        }
    }

    public void createNecessaryDirectories() {
        File savedPdfsDirectory = new File(CONSTANTS.SAVED_PDFS_PATH.toString());
        if (!savedPdfsDirectory.exists()) {
            savedPdfsDirectory.mkdir();
        }
    }

    
}
