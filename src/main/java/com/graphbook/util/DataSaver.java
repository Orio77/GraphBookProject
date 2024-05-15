package com.graphbook.util;

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

public class DataSaver {
    
    public boolean saveObject(Object o) {
        long startTime = System.nanoTime();
        Path curPath = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object");
        Path dirName = createDir(curPath);
        boolean res = writeObject(o, dirName);
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

    private boolean writeObject(Object o, Path path) {
        Path filePath = createFile(path.toString(), "saved_object");
        System.out.println(filePath.toString());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(o);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object readObject(Path path) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAll(String pathAsString) {
        Path dirPath = Paths.get(pathAsString);
    
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
        }
    }
}
