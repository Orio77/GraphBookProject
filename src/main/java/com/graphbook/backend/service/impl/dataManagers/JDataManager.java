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

public class JDataManager implements IDataManager {
    private Logger logger = LogManager.getLogger(getClass());
    
    @Override
    public boolean savePDF(Object o, String pdfName) { // TODO add a UI Interaction with question to save PDF, if yes, Suggest the changing the name of the pdf for easier retrieval when working with database
        if (o == null) {
            throw new IllegalArgumentException("Object to save cannot be null");
        }
        if (pdfName == null || pdfName.isEmpty()) {
            throw new IllegalArgumentException("pdfName cannot be null or empty");
        }
        long startTime = System.nanoTime();
        Path curPath = new GraphBookConfigManager().getSavedPdfsPath().resolve(pdfName);
        File freshlySavedPDF = curPath.toFile();
        if (!freshlySavedPDF.exists() && !freshlySavedPDF.mkdir()) {
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

    @Override
    public boolean savePlot(Object o, String label) {
        if (o == null) {
            throw new IllegalArgumentException("Object to save cannot be null");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("pdfName cannot be null or empty");
        }
        String pathAsString = new GraphBookConfigManager().getProperty("GraphBookProject", "SavedPlotData");
        Path savedPlotsPath = Paths.get(pathAsString);

        long startTime = System.nanoTime();
        Path curPath = savedPlotsPath.resolve(label);
        File freshlySavedPDF = curPath.toFile();
        if (!freshlySavedPDF.exists() && !freshlySavedPDF.mkdirs()) {
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
            throw new RuntimeException("IOException occured while creating a directory. Check the error log for details.");
        }
    }

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
            throw new RuntimeException("IOException occured while creating a file. Check the error log for details.");
        }
    }

    private LocalDateTime getCreationTime() {
        return LocalDateTime.now();
    }

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

    private void createMetadata(Path dir, Object o, double timeTaken) {
        Path filePath = createFile(dir.toString(), "metadata");
        try {
            Files.write(filePath, ("Class: " + o.getClass().toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(filePath, ("Time taken: " + timeTaken + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(filePath, ("Time of creation: " + LocalDateTime.now() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while creating metadata. Check the error log for details.");
        }
    }

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
            throw new RuntimeException("IOException occured while adding metadata to a file. Check the error log for details.");
        }
    }

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
            throw new RuntimeException("IOException occured while writing an object. Check the error log for details.");
        }
    }

    @Override
    public Object readSavedPDF(File file) {
        return readSavedObject(file);
    }

    private Object readSavedObject(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("IOException occured while reading saved pdf. Check the error log for details.");
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
            throw new RuntimeException("ClassNotFoundException occured while reading saved pdf. Check the error log for details.");
        } 
    }

    @Override
    public Object readSavedPlot(File file) {
        return readSavedObject(file); 
    }

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
            throw new RuntimeException("IOException occured while deleting all saved pdfs. Check the error log for details.");
        }
        return true;
    } 

    @SuppressWarnings("unchecked")
    @Override
    public List<PDFText> loadPDF(File savedPDF) {
        if (savedPDF == null) {
            throw new IllegalArgumentException("Saved PDF file cannot be null");
        }
        // TODO add a window informing the user what he is tasked with (frontend)
        Object res = readSavedPDF(savedPDF); 
        System.out.println("Deserialization class: " + res.getClass().getName());
        List<PDFText> result = null;
        try {
            result = (List<PDFText>) res;
        } catch (ClassCastException e) {
            throw new RuntimeException("ClassCastException occured while returning the result. Check the error log for details.");
        }
        return result;
    }
}
