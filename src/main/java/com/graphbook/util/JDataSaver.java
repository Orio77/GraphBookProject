package com.graphbook.util;

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

import com.graphbook.elements.PDFText;
import com.graphbook.frontend.InteractivePathChooser;
import com.graphbook.util.interfaces.IDataSaver;

public class JDataSaver implements IDataSaver {
    
    @Override
    public boolean savePDF(Object o, String pdfName) { // TODO add a UI Interaction with question to save PDF, if yes, Suggest the changing the name of the pdf for easier retrieval when working with database
        long startTime = System.nanoTime();
        Path curPath = CONSTANTS.SAVED_PDFS_PATH;
        Path dirName = createDir(curPath);
        boolean res = writeObject(o, dirName, pdfName);
        if (!res) {
            return false;
        }
        long endTime = System.nanoTime();
        double secondsTaken = (double) (endTime - startTime) / 1000000000;
        createMetadata(dirName, o, secondsTaken);
        return true;
    }

    public Path createDir(Path path) {
        try {
            String dirName = getNewDirectoryName(path.toString());
            Files.createDirectories(Paths.get(dirName));
            return Paths.get(dirName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Path createFile(String pathAsString, String fileName) {
        pathAsString = pathAsString.concat("\\" + fileName + ".txt");
        try {
            Path path = Paths.get(pathAsString);
            Files.createFile(path);
            Files.write(path, ("Creation date: " + getCreationTime().toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
        }
    }

    private boolean writeObject(Object o, Path path, String pdfName) {
        Path filePath = createFile(path.toString(), pdfName);
        System.out.println(filePath.toString());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(o);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object readSavedPDF(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteAllSavedPDFs() {
        Path dirPath = CONSTANTS.SAVED_OBJECTS_PATH;
    
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
            e.printStackTrace();
            return false;
        }
        return true;
    } 

    @SuppressWarnings("unchecked")
    @Override
    public List<PDFText> loadPDF() {
        File savedPDF = new InteractivePathChooser().chooseSavedPDF();
        return (List<PDFText>) readSavedPDF(savedPDF); // TODO Check if the pdf is read correctly
    }

    // TODO Improve Saver

    // choose the directory to store pdfs ready to use

    // create folders with name of pdfs, store the pdf inside *check if the file is already saved - delete yes/no

}
