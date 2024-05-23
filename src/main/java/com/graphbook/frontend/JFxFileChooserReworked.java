package com.graphbook.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class JFxFileChooserReworked implements IFileChooser {

    private final Stage primaryStage;

    public JFxFileChooserReworked(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public File choosePDF() {
       return chooseFile(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"), null);
    }

    @Override
    public File choosePDF(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"), initialDirectory);
    }

    @Override
    public File chooseDir() {
        return chooseDirectory(null);
        
    }

    @Override
    public File chooseDir(File initialDirectory) {
        return chooseDirectory(initialDirectory);
    }

    @Override
    public File chooseTXT() {
        return chooseFile(new FileChooser.ExtensionFilter("Text Files", "*.txt"), null);
    }

    @Override
    public File chooseTXT(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("Text Files", "*.txt"), initialDirectory);
    }

    @Override
    public String getUserInput(String title, String label) {
       AtomicReference<String> result = new AtomicReference<>();
       CountDownLatch latch = new CountDownLatch(1);

       Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);

            Label newLabel = new Label(label);
            TextField textField = new TextField();
            Button button = new Button();

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
            Thread.currentThread().interrupt();
            e.printStackTrace();    
        }

        return result.get().isEmpty() ? "unnamed_PDF" : result.get();
    }

    private File chooseFile(ExtensionFilter filter, File initialDirectory) {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(filter);

            if (validateDirectory(initialDirectory)) {
                fileChooser.setInitialDirectory(initialDirectory);
            }

            File file = fileChooser.showOpenDialog(primaryStage);
            result.set(file);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return result.get();
    }

    private File chooseDirectory(File initialDirectory) {
        AtomicReference<File> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            if (validateDirectory(initialDirectory)) {
                directoryChooser.setInitialDirectory(initialDirectory);
            }

            File directory = directoryChooser.showDialog(primaryStage);
            result.set(directory);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return result.get();
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public boolean isValidSavedPDF(File file) {
        try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file))) {
            Object potentialPDF = objIn.readObject();
            if (potentialPDF instanceof List<?>) {
                List<PDFText> pdfTexts = (List<PDFText>) potentialPDF;
                return true;
            }
            return false;
        } catch (ClassCastException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean validateDirectory(File directory) {
        return (directory != null && directory.isDirectory());
    }
    
}
