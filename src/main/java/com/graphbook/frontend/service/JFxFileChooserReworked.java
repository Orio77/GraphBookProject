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

/**
 * The {@code JFxFileChooserReworked} class provides implementations for file and directory 
 * selection dialogs using JavaFX. It includes methods for selecting PDF, text, and JSON files,
 * as well as directories and user input prompts.
 * <p>
 * This class implements the {@code IFileChooser} interface.
 * </p>
 */
public class JFxFileChooserReworked implements IFileChooser {
    /**
     * The primary stage for JavaFX dialogs.
     * TODO Make private.
     */
    public static Stage primaryStage;

    static {
        try {
            initializeJavaFX();
        } catch (Exception e) {
            System.out.println("Failed to initialize JavaFX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the JavaFX environment.
     */
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

    /**
     * Opens a file chooser dialog to select a PDF file.
     *
     * @return the selected PDF file, or {@code null} if no file was selected.
     */
    @Override
    public File choosePDF() {
        return chooseFile(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"), null);
    }

    /**
     * Opens a file chooser dialog to select a PDF file, with an initial directory.
     *
     * @param initialDirectory the initial directory to open in the file chooser.
     * @return the selected PDF file, or {@code null} if no file was selected.
     */
    @Override
    public File choosePDF(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"), initialDirectory);
    }

    /**
     * Opens a directory chooser dialog to select a directory.
     *
     * @return the selected directory, or {@code null} if no directory was selected.
     */
    @Override
    public File chooseDir() {
        return chooseDirectory(null);
    }

    /**
     * Opens a directory chooser dialog to select a directory, with an initial directory.
     *
     * @param initialDirectory the initial directory to open in the directory chooser.
     * @return the selected directory, or {@code null} if no directory was selected.
     */
    @Override
    public File chooseDir(File initialDirectory) {
        return chooseDirectory(initialDirectory);
    }

    /**
     * Opens a file chooser dialog to select a text file.
     *
     * @return the selected text file, or {@code null} if no file was selected.
     */
    @Override
    public File chooseTXT() {
        return chooseFile(new FileChooser.ExtensionFilter("Text Files", "*.txt"), null);
    }

    /**
     * Opens a file chooser dialog to select a text file, with an initial directory.
     *
     * @param initialDirectory the initial directory to open in the file chooser.
     * @return the selected text file, or {@code null} if no file was selected.
     */
    @Override
    public File chooseTXT(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("Text Files", "*.txt"), initialDirectory);
    }

    /**
     * Opens a file chooser dialog to select a JSON file.
     *
     * @return the selected JSON file, or {@code null} if no file was selected.
     */
    @Override
    public File chooseJSON() {
        return chooseFile(new FileChooser.ExtensionFilter("Json Files", "*.json"), null);
    }

    /**
     * Opens a file chooser dialog to select a JSON file, with an initial directory.
     *
     * @param initialDirectory the initial directory to open in the file chooser.
     * @return the selected JSON file, or {@code null} if no file was selected.
     */
    @Override
    public File chooseJSON(File initialDirectory) {
        return chooseFile(new FileChooser.ExtensionFilter("Json Files", "*.json"), initialDirectory);
    }

    /**
     * Opens a dialog to choose multiple concepts from a provided list.
     *
     * @param conceptList the list of concepts to choose from.
     * @return a list of selected concepts.
     */
    @Override
    public List<String> chooseConcepts(List<String> conceptList) {
        List<Object> res = chooseElements(new ArrayList<>(conceptList));
        return res.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Opens a dialog to get user input with a specified title and label.
     *
     * @param title the title of the dialog.
     * @param label the label to display in the dialog.
     * @return the user input, or "unnamed_PDF" if no input was provided.
     */
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
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return result.get().isEmpty() ? "unnamed_PDF" : result.get();
    }

    /**
     * Opens a file chooser dialog with the specified extension filter and initial directory.
     *
     * @param filter the extension filter to apply in the file chooser.
     * @param initialDirectory the initial directory to open in the file chooser.
     * @return the selected file, or {@code null} if no file was selected.
     */
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

    /**
     * Opens a dialog to choose multiple elements from a provided list.
     *
     * @param objs the list of elements to choose from.
     * @return a list of selected elements.
     */
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

    /**
     * Opens a directory chooser dialog with the specified initial directory.
     *
     * @param initialDirectory the initial directory to open in the directory chooser.
     * @return the selected directory, or {@code null} if no directory was selected.
     */
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

    /**
     * Checks if the specified file is a valid saved PDF.
     *
     * @param file the file to check.
     * @return {@code true} if the file is a valid saved PDF, {@code false} otherwise.
     */
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

    /**
     * Validates if the specified file is a directory.
     *
     * @param directory the file to validate.
     * @return {@code true} if the file is a directory, {@code false} otherwise.
     */
    private boolean validateDirectory(File directory) {
        return (directory != null && directory.isDirectory());
    }

    /**
     * Exits the JavaFX application.
     */
    public void exit() {
        Platform.exit();
    }
}
