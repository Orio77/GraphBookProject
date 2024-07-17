package com.graphbook.frontend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.graphbook.Main;
import com.graphbook.backend.model.PDFText;
import com.graphbook.frontend.interfaces.IFileChooser;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The {@code JFxFileChooserReworked} class provides implementations for file
 * and directory
 * selection dialogs using JavaFX. It includes methods for selecting PDF, text,
 * and JSON files,
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
     * Opens a directory chooser dialog to select a directory, with an initial
     * directory.
     *
     * @param initialDirectory the initial directory to open in the directory
     *                         chooser.
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
            stage.getIcons().add(new Image(
                    "file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/icons/graphbook_icon.png"));

            // Create label and text field
            Label newLabel = new Label(label);
            newLabel.getStyleClass().add("label");
            // Ensure the label is visible against the background, adjust color if necessary
            TextField textField = new TextField();
            textField.getStyleClass().add("text-field");

            // Create submit button with the desired styling
            Button button = new Button("Submit");
            button.getStyleClass().add("glowing-button");

            button.setOnAction(event -> {
                result.set(textField.getText());
                stage.close();
                latch.countDown();
            });

            stage.setOnCloseRequest(event -> {
                result.set("");
                latch.countDown();
            });

            // Create VBox for layout
            VBox vbox = new VBox(newLabel, textField, button);
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(20));
            vbox.setAlignment(Pos.CENTER);

            // Create a stack pane to hold the background and content
            StackPane stackPane = new StackPane();

            // Add background image
            InputStream backgroundIconStream = Main.class.getResourceAsStream("/icons/elements_background.png");
            ImageView backgroundImageView = new ImageView(new Image(backgroundIconStream));
            backgroundImageView.setOpacity(0.3);
            backgroundImageView.fitWidthProperty().bind(stage.widthProperty());
            backgroundImageView.fitHeightProperty().bind(stage.heightProperty());
            stackPane.getChildren().add(backgroundImageView);

            // Add VBox to stack pane
            stackPane.getChildren().add(vbox);

            // Create scene and add to stage
            Scene scene = new Scene(stackPane, 300, 200);
            scene.getStylesheets()
                    .add("file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/css/upgradedStyle.css");
            stage.setScene(scene);
            stage.showAndWait(); // Changed to showAndWait to ensure the dialog is modal
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return result.get().isEmpty() ? "" : result.get();
    }

    /**
     * Opens a file chooser dialog with the specified extension filter and initial
     * directory.
     *
     * @param filter           the extension filter to apply in the file chooser.
     * @param initialDirectory the initial directory to open in the file chooser.
     * @return the selected file, or {@code null} if no file was selected.
     */
    private File chooseFile(ExtensionFilter filter, File initialDirectory) {
        if (filter == null) {
            throw new RuntimeException("Filter cannot be null");
        }
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
            stage.getIcons().add(new Image(
                    "file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/icons/graphbook_icon.png"));

            // Create a VBox for content
            VBox vbox = new VBox(15);
            vbox.setPadding(new Insets(15, 20, 15, 20));

            // Create a GridPane for the checkboxes
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setAlignment(Pos.CENTER);

            // Determine the number of columns based on the number of objects
            int columns = 3; // Number of columns for checkboxes
            for (int i = 0; i < objs.size(); i++) {
                CheckBox cb = new CheckBox(objs.get(i).toString());
                cb.getStyleClass().add("menu-bar");
                gridPane.add(cb, i % columns, i / columns);
            }

            // Create background image view
            InputStream backgroundIconStream = Main.class.getResourceAsStream("/icons/elements_background.png");
            ImageView backgroundImageView = new ImageView(new Image(backgroundIconStream));
            backgroundImageView.setOpacity(0.3);
            // Bind the ImageView size to the VBox size
            backgroundImageView.fitWidthProperty().bind(vbox.widthProperty());
            backgroundImageView.fitHeightProperty().bind(vbox.heightProperty());

            // Create a stack pane to hold the background and content
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(backgroundImageView, vbox);

            // Create confirm button
            Button button = new Button("Confirm");
            button.getStyleClass().add("glowing-button");
            button.setOnAction(event -> {
                List<Object> selectedObjects = new ArrayList<>();
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                        selectedObjects.add(objs.get(gridPane.getChildren().indexOf(node)));
                    }
                }
                result.set(selectedObjects);
                stage.close();
                latch.countDown();
            });

            // Add GridPane and button to the VBox
            vbox.getChildren().addAll(gridPane, button);
            vbox.setAlignment(Pos.CENTER);

            // Create the scene and show the stage
            Scene scene = new Scene(stackPane);
            scene.getStylesheets()
                    .add("file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/css/upgradedStyle.css");
            stage.setScene(scene);

            // Automatically adjust window size based on content
            stage.sizeToScene();
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
     * @param initialDirectory the initial directory to open in the directory
     *                         chooser.
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

    public void showInformationWindow(String title, String message) {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.getIcons().add(new Image(
                    "file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/icons/graphbook_icon.png"));

            // Create a label to display the message
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Create 'Ok' button with the desired styling
            Button okButton = new Button("Ok");
            okButton.getStyleClass().add("glowing-button");

            okButton.setOnAction(event -> {
                stage.close();
                latch.countDown();
            });

            stage.setOnCloseRequest(event -> {
                latch.countDown();
            });

            // Create VBox for layout
            VBox vbox = new VBox(messageLabel, okButton);
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(20));
            vbox.setAlignment(Pos.CENTER);

            // Create a stack pane to hold the background and content
            StackPane stackPane = new StackPane();

            // Add background image
            InputStream backgroundIconStream = Main.class.getResourceAsStream("/icons/elements_background.png");
            ImageView backgroundImageView = new ImageView(new Image(backgroundIconStream));
            backgroundImageView.setOpacity(0.3);
            backgroundImageView.fitWidthProperty().bind(stage.widthProperty());
            backgroundImageView.fitHeightProperty().bind(stage.heightProperty());
            stackPane.getChildren().add(backgroundImageView);

            // Add VBox to stack pane
            stackPane.getChildren().add(vbox);

            // Create scene and add to stage
            Scene scene = new Scene(stackPane, 400, 200);
            scene.getStylesheets()
                    .add("file:///C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/css/upgradedStyle.css");
            stage.setScene(scene);
            stage.sizeToScene(); // Automatically adjust window size based on content
            stage.show();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    /**
     * Checks if the specified file is a valid saved PDF.
     *
     * @param file the file to check.
     * @return {@code true} if the file is a valid saved PDF, {@code false}
     *         otherwise.
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

    public static void main(String[] args) {
        // new JFxFileChooserReworked().chooseConcepts(List.of(
        // "Concept1", "Concept2", "Concept3", "Concept4", "Concept5",
        // "Concept6", "Concept7", "Concept8", "Concept9", "Concept10",
        // "Concept11", "Concept12", "Concept13", "Concept14", "Concept15",
        // "Concept16", "Concept17", "Concept18", "Concept19", "Concept20",
        // "Concept21", "Concept22"));

        // new JFxFileChooserReworked().getUserInput("Some title", "Some label");

        new JFxFileChooserReworked().showInformationWindow("Some title", "Some info");
    }
}
