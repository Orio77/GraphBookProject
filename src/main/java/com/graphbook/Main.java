package com.graphbook;

import java.io.InputStream;
import java.net.URL;

import com.graphbook.frontend.service.GraphBookGUIManager;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The {@code Main} class serves as the entry point for the Graph Book
 * application, a JavaFX-based desktop application.
 * It initializes the JavaFX platform, sets up the primary stage (window) with
 * various UI components, and manages the
 * application's lifecycle. This class is responsible for creating the user
 * interface, including buttons for various
 * functionalities such as reading, loading, and creating PDFs and graphs, as
 * well as adding concepts and creating charts.
 * It also handles the application's exit process.
 * 
 * <p>
 * Key features of the {@code Main} class include:
 * </p>
 * <ul>
 * <li>Initialization of the {@code GraphBookGUIManager} for managing
 * GUI-related actions.</li>
 * <li>Creation of a primary stage with a title, icon, and various UI components
 * arranged in a {@code BorderPane} layout.</li>
 * <li>Dynamic layout adjustments based on window size and state (fullscreen or
 * maximized).</li>
 * <li>Application of CSS for styling, with a fallback mechanism if the CSS file
 * is not found.</li>
 * <li>Use of multithreading for performing actions triggered by button clicks
 * to ensure a responsive UI.</li>
 * <li>Implementation of a glowing effect on the main application icon using a
 * {@code Timeline} animation.</li>
 * <li>Custom button creation method that standardizes the appearance and
 * behavior of buttons across the application.</li>
 * </ul>
 * 
 * <p>
 * The application's UI is divided into several sections:
 * </p>
 * <ul>
 * <li>A central area for displaying buttons in either a {@code GridPane} or
 * {@code VBox} layout, depending on the window state.</li>
 * <li>A right section dedicated to displaying the main application icon with a
 * glowing animation effect.</li>
 * <li>A background image that fills the entire window, with its opacity and
 * size adjusted for aesthetic purposes.</li>
 * </ul>
 * 
 * <p>
 * Listeners are added to various properties (e.g., window size, fullscreen,
 * maximized) to dynamically adjust the layout
 * and appearance of UI components based on the current state of the application
 * window.
 * </p>
 * 
 * <p>
 * This class demonstrates the use of JavaFX for building a desktop application,
 * showcasing various techniques such as
 * handling events, working with images and animations, and applying CSS for
 * styling.
 * </p>
 */
public class Main {

    private static GraphBookGUIManager manager;

    public static void main(String[] args) {
        // Initialize the JavaFX platform
        Platform.startup(() -> {
            manager = new GraphBookGUIManager();
            // Create the main stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Graph Book");
            primaryStage.getIcons().add(new Image("file:.\\src\\main\\resources\\icons\\graphbook_icon.png"));

            final int iconSize = 100;

            // Create buttons with larger icons and set actions
            Button readPDFButton = createButton("Read PDF", "/icons/read_pdf_icon.png", iconSize); // size increased
            readPDFButton.setOnAction(event -> new Thread(() -> manager.readPDF()).start());

            Button loadPDFButton = createButton("Load Saved PDF", "/icons/load_pdf_icon.png", iconSize); // size
                                                                                                         // increased
            loadPDFButton.setOnAction(event -> new Thread(() -> manager.loadSavedPDF()).start());

            Button createGraphButton = createButton("Create Graph", "/icons/create_graph_icon.png", iconSize); // size
                                                                                                               // increased
            createGraphButton.setOnAction(event -> new Thread(() -> manager.createGraph()).start());

            Button addConceptButton = createButton("Add Concept", "/icons/concept_icon.png", iconSize); // size
                                                                                                        // increased
            addConceptButton.setOnAction(event -> new Thread(() -> manager.addConceptToAll()).start());

            Button createChartButton = createButton("Create Chart", "/icons/create_chart_icon.png", iconSize); // size
                                                                                                               // increased
            createChartButton.setOnAction(event -> new Thread(() -> manager.createChart()).start());

            Button showChartButton = createButton("Show Chart", "/icons/show_chart_icon.png", iconSize); // size
                                                                                                         // increased
            showChartButton.setOnAction(event -> new Thread(() -> manager.showChart()).start());

            Button btnExit = createButton("Exit", "/icons/exit_icon.png", iconSize); // size increased
            btnExit.setOnAction(event -> Platform.exit());

            Button[] buttons = { readPDFButton, loadPDFButton, createGraphButton, addConceptButton, createChartButton,
                    showChartButton, btnExit };

            BorderPane root = new BorderPane();

            // Create and set scene
            Scene scene = new Scene(root); // window size increased
            primaryStage.setScene(scene);

            // Attempt to access the CSS file
            URL cssURL = Main.class.getResource("/css/upgradedStyle.css");
            if (cssURL != null) {
                // Convert URL to external form and apply CSS file
                String cssPath = cssURL.toExternalForm();
                scene.getStylesheets().add(cssPath);
            } else {
                System.out.println("CSS file not found.");
            }

            InputStream graphbookIconStream = Main.class
                    .getResourceAsStream("/icons/graphbook_icon.png");
            ImageView mainImageView = new ImageView(new Image(graphbookIconStream));

            Glow glow = new Glow(0.0);
            mainImageView.setEffect(glow);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.0)),
                    new KeyFrame(Duration.seconds(2), new KeyValue(glow.levelProperty(), 0.5)),
                    new KeyFrame(Duration.seconds(4), new KeyValue(glow.levelProperty(), 0.0)));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            // Step 1: Wrap the ImageView in a Pane for better control
            VBox imageContainer = new VBox(mainImageView);
            imageContainer.setAlignment(Pos.CENTER); // Center the image vertically
            imageContainer.setPadding(new Insets(10)); // Add some padding around the image

            // Optional: Adjust the size of the ImageView if necessary
            mainImageView.setFitHeight(400); // Adjust height as needed
            mainImageView.setFitWidth(400); // Adjust width as needed

            // Step 2: Add the Pane to the BorderPane on the right
            root.setRight(imageContainer);

            // Step 1: Create the background ImageView
            InputStream backgroundIconStream = Main.class
                    .getResourceAsStream("/icons/backround.png");
            ImageView backgroundImageView = new ImageView(new Image(backgroundIconStream));
            backgroundImageView.setOpacity(0.3); // Adjust the opacity to make it faded
            backgroundImageView.setFitHeight(scene.getHeight());
            backgroundImageView.setFitWidth(scene.getWidth());
            backgroundImageView.setPreserveRatio(false); // Ensure it fills the whole background

            // Step 2: Add the background ImageView to the BorderPane
            root.getChildren().add(0, backgroundImageView); // Add it as the first child to be in the background

            // Adjust the background image when the window is resized
            scene.widthProperty()
                    .addListener((obs, oldVal, newVal) -> backgroundImageView.setFitWidth(newVal.doubleValue()));
            scene.heightProperty()
                    .addListener((obs, oldVal, newVal) -> backgroundImageView.setFitHeight(newVal.doubleValue()));

            // Define method to create grid layout
            Runnable gridLayout = () -> {
                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(20));
                gridPane.setHgap(15); // increased spacing
                gridPane.setVgap(15); // increased spacing

                gridPane.add(readPDFButton, 0, 0);
                gridPane.add(loadPDFButton, 1, 0);
                gridPane.add(createGraphButton, 0, 1);
                gridPane.add(addConceptButton, 1, 1);
                gridPane.add(createChartButton, 0, 2);
                gridPane.add(showChartButton, 1, 2);
                gridPane.add(btnExit, 0, 3);

                for (Button button : buttons) {
                    button.setMinHeight(100); // Set the minimum height for the buttons
                    button.setMinWidth(250); // Set the minimum width for the buttons
                }

                root.setCenter(gridPane);
            };

            // Define method to create VBox layout
            Runnable vBoxLayout = () -> {
                VBox buttonBox = new VBox(15); // vertical box with spacing
                buttonBox.setPadding(new Insets(20));

                for (Button button : buttons) {
                    button.setMinHeight(100); // Set the minimum height for the buttons
                    button.setMinWidth(300); // Set the minimum width for the buttons
                    buttonBox.getChildren().add(button);
                }

                root.setCenter(buttonBox);
            };

            // Add listener to fullscreen property
            primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                if (isNowFullScreen) {
                    vBoxLayout.run();
                } else {
                    gridLayout.run();
                }
            });

            // Add listener to maximized property
            primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
                if (isNowMaximized) {
                    vBoxLayout.run();
                } else {
                    gridLayout.run();
                }
            });

            // Initial layout based on windowed mode
            gridLayout.run();

            // Show the stage
            primaryStage.show();
        });
    }

    /**
     * The {@code createButton} method is a utility function designed to standardize
     * the creation of buttons within the application.
     * It takes a button label, an icon path, and an icon size as parameters to
     * create a customized {@code Button} instance.
     * This method encapsulates the process of loading an icon from a specified
     * path, resizing it to the desired dimensions,
     * and attaching it to the button. Additionally, it applies a custom CSS class
     * to the button to ensure consistent styling
     * across the application.
     * 
     * @param text     The text label for the button.
     * @param iconPath The relative path to the icon image file.
     * @param iconSize The desired size for the icon's width and height.
     * @return A {@code Button} instance with the specified label, icon, and
     *         styling.
     */
    private static Button createButton(String text, String iconPath, double iconSize) {
        Button button = new Button(text);
        InputStream iconStream = Main.class.getResourceAsStream(iconPath);
        Image icon = new Image(iconStream);
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(iconSize); // Set desired width
        imageView.setFitHeight(iconSize); // Set desired height
        button.setGraphic(imageView);
        button.getStyleClass().add("button-large"); // Custom style class for larger buttons
        return button;
    }
}
