package com.graphbook.util;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CONSTANTS {
    public static final Path SERIALIZED_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/");
    public static final Path ERROR_LOG_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/server/logs/errorLog");
    public static final URI MY_URI = URI.create("http://localhost:5000/similarity");
    public static final Path SAVED_OBJECTS_PATH = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object");
    public static Path STORED_PDFS_PATH = null; // TODO Set this var with Improved Saver
}
