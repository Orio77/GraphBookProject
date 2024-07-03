package com.graphbook.frontend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.graphbook.backend.model.PDFText;
import com.graphbook.frontend.interfaces.IFileChooser;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class JFxFileChooserReworked implements IFileChooser {
    private static Stage primaryStage;

    static {
        try {
            initializeJavaFX();
        } catch (Exception e) {
            System.out.println("Failed to initialize JavaFX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeJavaFX() {
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(() -> {
                primaryStage = new Stage();
                latch.countDown();
            });

            if (latch.getCount() > 0) {
                latch.countDown();
            }
            latch.await();  // Wait for the JavaFX initialization to complete.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Error during JavaFX initialization: " + e.getMessage());
            e.printStackTrace();
        }
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
    public File chooseJSON(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("Json Files", "*.json"), initialDirectory);
    }

    @Override
    public File chooseJSON() {
        return chooseFile(new FileChooser.ExtensionFilter("Json Files", "*.json"), null);
    }

    @Override
    public List<String> chooseConcepts(List<String> conceptList) {
        List<Object> res = chooseElements(new ArrayList<>(conceptList));
        return res.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
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

    private List<Object> chooseElements(List<Object> objs) {
        AtomicReference<List<Object>> result = new AtomicReference<>(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Choose Elements");

            VBox vbox = new VBox();
            vbox.setSpacing(10);

            // Create a list of checkboxes for each object
            List<CheckBox> checkBoxes = objs.stream()
                                        .map(obj -> new CheckBox(obj.toString()))
                                        .collect(Collectors.toList());
            vbox.getChildren().addAll(checkBoxes);

            Button button = new Button("Confirm");
            button.setOnAction(event -> {
                List<Object> selectedObjects = checkBoxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(cb -> objs.get(checkBoxes.indexOf(cb)))
                        .collect(Collectors.toList());
                result.set(selectedObjects);
                stage.close();
                latch.countDown();
            });

            vbox.getChildren().add(button);

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
    
    public void exit() {
        Platform.exit();
    }
}