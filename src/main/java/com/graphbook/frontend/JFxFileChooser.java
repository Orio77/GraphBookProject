package com.graphbook.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.graphbook.elements.PDFText;
import com.graphbook.frontend.interfaces.IFileChooser;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO Update the Look
public class JFxFileChooser implements IFileChooser { // TODO implement an interface

    public File choosePDF() {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            // Create a temporary stage to serve as the parent for the file chooser
            Stage stage = new Stage();
            // TODO Create GraphBook logo and set it for the app
            stage.setTitle("Select PDF File");
    
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open PDF File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
    
            // Show open dialog and wait for user to select a file
            File file = fileChooser.showOpenDialog(stage);
    
            result.set(file);  // This will be null if no file is selected
    
            stage.close();
            latch.countDown();  // Signal that the file selection is complete
        });
    
        try {
            latch.await();  // Wait until the file selection process is complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore the interrupted status
            e.printStackTrace();
        } 
    
        return result.get();  // Return the selected file, or null if no file was selected
    }

    public File choosePDF(File initialDirectory) {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            // Create a temporary stage to serve as the parent for the file chooser
            Stage stage = new Stage();
            // TODO Create GraphBook logo and set it for the app
            stage.setTitle("Select PDF File");
    
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open PDF File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            if (validateDirectory(initialDirectory)) {
                fileChooser.setInitialDirectory(initialDirectory);
            }
    
            // Show open dialog and wait for user to select a file
            File file = fileChooser.showOpenDialog(stage);
    
            result.set(file);  // This will be null if no file is selected
    
            stage.close();
            latch.countDown();  // Signal that the file selection is complete
        });
    
        try {
            latch.await();  // Wait until the file selection process is complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore the interrupted status
            e.printStackTrace();
        } 
    
        return result.get();  // Return the selected file, or null if no file was selected
    }

    public File chooseDir() {
    AtomicReference<File> result = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Platform.startup(() -> {
        // Create a temporary stage to act as the parent for the directory chooser
        Stage stage = new Stage();
        stage.setTitle("Select Directory");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");

        // Show directory selection dialog
        File selectedDirectory = directoryChooser.showDialog(stage);

        result.set(selectedDirectory);  // This will be null if no directory is selected or if the dialog is cancelled

        stage.close();
        latch.countDown();  // Signal that the directory selection is complete
    });

    try {
        latch.await();  // Wait until the directory selection process is complete
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // Restore the interrupted status
        e.printStackTrace();
    } 

    return result.get();  // Return the selected directory, or null if cancelled or not selected
}

    public File chooseDir(File initialDirectory) {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(() -> {
            // Create a temporary stage to act as the parent for the directory chooser
            Stage stage = new Stage();
            stage.setTitle("Select Directory");

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");

            if (validateDirectory(initialDirectory)) {
                directoryChooser.setInitialDirectory(initialDirectory);
            }

            // Show directory selection dialog
            File selectedDirectory = directoryChooser.showDialog(stage);

            result.set(selectedDirectory);  // This will be null if no directory is selected or if the dialog is cancelled

            stage.close();
            latch.countDown();  // Signal that the directory selection is complete
        });

        try {
            latch.await();  // Wait until the directory selection process is complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore the interrupted status
            e.printStackTrace();
        } 

        return result.get();  // Return the selected directory, or null if cancelled or not selected
    }

    public File chooseTXT() {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            Stage stage = new Stage();
            stage.setTitle("Select Text File");
    
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Text File");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
            fileChooser.getExtensionFilters().add(filter);
    
            // Show open file dialog
            File selectedFile = fileChooser.showOpenDialog(stage);
    
            if (selectedFile != null && isValidSavedPDF(selectedFile)) {  // Validate selected file
                result.set(selectedFile);
            } else {
                result.set(null);
            }
    
            stage.close();
            latch.countDown();  // Decrement latch count to continue
        });
    
        try {
            latch.await();  // Wait until the file selection and validation is complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore interrupted status
            e.printStackTrace();
        } 
    
        return result.get();  // Return the selected and validated file or null
    }

    public File chooseTXT(File initialDirectory) {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            Stage stage = new Stage();
            stage.setTitle("Select Text File");
    
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Text File");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
            fileChooser.getExtensionFilters().add(filter);
            
            if (validateDirectory(initialDirectory)) {
                fileChooser.setInitialDirectory(initialDirectory);
            }
    
            // Show open file dialog
            File selectedFile = fileChooser.showOpenDialog(stage);
    
            if (selectedFile != null && isValidSavedPDF(selectedFile)) {  // Validate selected file
                result.set(selectedFile);
            } else {
                result.set(null);
            }
    
            stage.close();
            latch.countDown();  // Decrement latch count to continue
        });
    
        try {
            latch.await();  // Wait until the file selection and validation is complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore interrupted status
            e.printStackTrace();
        } 
    
        return result.get();  // Return the selected and validated file or null
    }

    @SuppressWarnings("unchecked")
    private boolean isValidSavedPDF(File file) {
        try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file))) {
            Object potentialPDF = objIn.readObject();
            try {
                potentialPDF = (List<PDFText>) potentialPDF;
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        } catch (Exception e) { // TODO handle exception properly
            return false;
        }
    }

    public String getUserInput(String title, String label) { 
        AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            // stage.setTitle("Enter PDF Name");
            stage.setTitle(title);
    
            // Label label = new Label("Enter the Name for Your PDF:");
            Label newLabel = new Label(label);
            TextField textField = new TextField();
            Button button = new Button("Submit");
    
            button.setOnAction(event -> {
                result.set(textField.getText());
                stage.close();
                latch.countDown();
            });
    
            stage.setOnCloseRequest(event -> {
                result.set("unnamed_PDF");
                latch.countDown();
            });
    
            VBox vbox = new VBox(newLabel, textField, button);
            vbox.setSpacing(10);
    
            Scene scene = new Scene(vbox, 300, 200);
            stage.setScene(scene);
            stage.show();
        });
    
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        return (result.get().isEmpty()) ? "unnamed_PDF" : result.get();
    }

    public void cleanUp() {
        Platform.exit();
    }

    private boolean validateDirectory(File initialDirectory) {
        return (initialDirectory != null && initialDirectory.isDirectory());
    }
}
