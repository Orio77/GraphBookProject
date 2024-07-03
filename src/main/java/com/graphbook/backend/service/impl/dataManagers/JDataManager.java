package com.graphbook.backend.service.impl.dataManagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.IDataManager;

/**
 * Implementation of the IDataManager interface, responsible for managing 
 * PDF and plot data, including saving, loading, and deleting operations.
 */
public class JDataManager implements IDataManager {
    private Logger logger = LogManager.getLogger(getClass());
    
    /**
     * Saves a PDF object to a specified location.
     *
     * @param o the object to save
     * @param pdfName the name of the PDF file
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the object or pdfName is null or empty
     * @throws RuntimeException if there is an error during saving
     */
    @Override
    public boolean savePDF(Object o, String pdfName) {
        if (o == null) {
            throw new IllegalArgumentException("Object to save cannot be null");
        }
        if (pdfName == null || pdfName.isEmpty()) {
            throw new IllegalArgumentException("pdfName cannot be null or empty");
        }
        long startTime = System.nanoTime();
        Path curPath = new GraphBookConfigManager().getSavedPdfsPath().resolve(pdfName);
        File freshlySavedPDF = curPath.toFile();
        if (!freshlySavedPDF.exists() && !freshlySavedPDF.mkdirs()) {
            throw new RuntimeException("Failed to create the directory.");
        }
        boolean res = writeObject(o, curPath, pdfName);
        if (!res) {
            throw new RuntimeException("Failed to save (serialize) the pdf into file");
        }
        long endTime = System.nanoTime();
        double secondsTaken = (double) (endTime - startTime) / 1000000000;
        createMetadata(curPath, o, secondsTaken);
        return true;
    }

    /**
     * Saves a plot object to a specified location.
     *
     * @param o the object to save
     * @param label the label of the plot file
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the object or label is null or empty
     * @throws RuntimeException if there is an error during saving
     */
    @Override
    public boolean savePlot(Object o, String label) {
        if (o == null) {
            throw new IllegalArgumentException("Object to save cannot be null");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("label cannot be null or empty");
        }
        String pathAsString = new GraphBookConfigManager().getProperty("GraphBookProject", "SavedPlotData");
        Path savedPlotsPath = Paths.get(pathAsString);

        long startTime = System.nanoTime();
        Path curPath = savedPlotsPath.resolve(label);
        File freshlySavedPlot = curPath.toFile();
        if (!freshlySavedPlot.exists() && !freshlySavedPlot.mkdirs()) {
            throw new RuntimeException("Failed to create the directory.");
        }
        boolean res = writeObject(o, curPath, label);
        if (!res) {
            throw new RuntimeException("Failed to save (serialize) the plot into file");
        }
        long endTime = System.nanoTime();
        double secondsTaken = (double) (endTime - startTime) / 1000000000;
        createMetadata(curPath, o, secondsTaken);
        return true;
    }

    /**
     * Creates a directory at the specified path.
     *
     * @param path the path where the directory should be created
     * @return the path of the newly created directory
     * @throws IllegalArgumentException if the path is null
     * @throws RuntimeException if there is an error during directory creation
     */
    public Path createDir(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        try {
            String dirName = getNewDirectoryName(path.toString());
            Files.createDirectories(Paths.get(dirName));
            return Paths.get(dirName);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while creating a directory. Check the error log for details.");
        }
    }

    /**
     * Creates a file at the specified path with the given name.
     *
     * @param pathAsString the path where the file should be created
     * @param fileName the name of the file to be created
     * @return the path of the newly created file
     * @throws IllegalArgumentException if the path or fileName is null or empty
     * @throws RuntimeException if there is an error during file creation
     */
    public Path createFile(String pathAsString, String fileName) {
        if (pathAsString == null || pathAsString.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName cannot be null or empty");
        }
        pathAsString = pathAsString.concat("\\" + fileName + ".txt");
        try {
            Path path = Paths.get(pathAsString);
            Files.createFile(path);
            Files.write(path, ("Creation date: " + getCreationTime().toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            return path;
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while creating a file. Check the error log for details.");
        }
    }

    /**
     * Gets the current creation time.
     *
     * @return the current LocalDateTime
     */
    private LocalDateTime getCreationTime() {
        return LocalDateTime.now();
    }

    /**
     * Generates a new directory name based on the given path.
     *
     * @param pathAsString the original path
     * @return the new directory name
     */
    private String getNewDirectoryName(String pathAsString) {
        int i = 1;
        String newDirName = pathAsString;
        while (true) {
            if (!Files.exists(Paths.get(newDirName))) {
                return newDirName;
            }
            newDirName = pathAsString.concat("_" + i++);
        }
    }

    /**
     * Creates metadata for the saved file.
     *
     * @param dir the directory where the metadata file should be created
     * @param o the object being saved
     * @param timeTaken the time taken to save the object
     * @throws RuntimeException if there is an error during metadata creation
     */
    private void createMetadata(Path dir, Object o, double timeTaken) {
        Path filePath = createFile(dir.toString(), "metadata");
        try {
            Files.write(filePath, ("Class: " + o.getClass().toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(filePath, ("Time taken: " + timeTaken + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(filePath, ("Time of creation: " + LocalDateTime.now() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while creating metadata. Check the error log for details.");
        }
    }

    /**
     * Adds metadata to an existing file.
     *
     * @param path the path of the file
     * @param label the metadata label
     * @param value the metadata value
     * @throws IllegalArgumentException if the path, label, or value is null or empty
     * @throws RuntimeException if there is an error during metadata addition
     */
    public void addToMetadata(Path path, String label, String value) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("label cannot be null or empty");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
        if (!Files.exists(path)) {
            throw new RuntimeException("The file, to add the metadata to, doesn't exist");
        }
        try {
            Files.write(path, new String(label + ": " + value + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while adding metadata to a file. Check the error log for details.");
        }
    }

    /**
     * Writes an object to a file.
     *
     * @param o the object to write
     * @param path the path of the file
     * @param pdfName the name of the PDF file
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the object, path, or pdfName is null or empty
     * @throws RuntimeException if there is an error during writing
     */
    private boolean writeObject(Object o, Path path, String pdfName) {
        if (o == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        if (pdfName == null || pdfName.isEmpty()) {
            throw new IllegalArgumentException("pdfName cannot be null or empty");
        }
        Path filePath = createFile(path.toString(), pdfName);
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(o);
            return true;
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while writing an object. Check the error log for details.");
        }
    }

    /**
     * Reads a saved PDF object from a file.
     *
     * @param file the file to read
     * @return the deserialized PDF object
     * @throws IllegalArgumentException if the file is null
     * @throws RuntimeException if there is an error during reading
     */
    @Override
    public Object readSavedPDF(File file) {
        return readSavedObject(file);
    }

    /**
     * Reads a saved object from a file.
     *
     * @param file the file to read
     * @return the deserialized object
     * @throws IllegalArgumentException if the file is null
     * @throws RuntimeException if there is an error during reading
     */
    private Object readSavedObject(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occurred while reading saved pdf. Check the error log for details.");
        } catch (ClassNotFoundException e) {
            logger.error(e);
            throw new RuntimeException("ClassNotFoundException occurred while reading saved pdf. Check the error log for details.");
        }
    }

    /**
     * Reads a saved plot object from a file.
     *
     * @param file the file to read
     * @return the deserialized plot object
     * @throws IllegalArgumentException if the file is null
     * @throws RuntimeException if there is an error during reading
     */
    @Override
    public Object readSavedPlot(File file) {
        return readSavedObject(file);
    }

    /**
     * Deletes all saved PDFs in the configured directory.
     *
     * @return true if the operation was successful
     * @throws RuntimeException if there is an error during deletion
     */
    public boolean deleteAllSavedPDFs() {
        Path dirPath = new GraphBookConfigManager().getSavedPdfsPath();
    
        try {
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
    
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(dirPath)) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while deleting all saved pdfs. Check the error log for details.");
        }
        return true;
    } 

    /**
     * Loads a saved PDF and returns it as a list of PDFText objects.
     *
     * @param savedPDF the saved PDF file
     * @return the list of PDFText objects
     * @throws IllegalArgumentException if the savedPDF file is null
     * @throws RuntimeException if there is an error during loading
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<PDFText> loadPDF(File savedPDF) {
        if (savedPDF == null) {
            throw new IllegalArgumentException("Saved PDF file cannot be null");
        }
        Object res = readSavedPDF(savedPDF); 
        System.out.println("Deserialization class: " + res.getClass().getName());
        List<PDFText> result = null;
        try {
            result = (List<PDFText>) res;
        } catch (ClassCastException e) {
            throw new RuntimeException("ClassCastException occurred while returning the result. Check the error log for details.");
        }
        return result;
    }
}
