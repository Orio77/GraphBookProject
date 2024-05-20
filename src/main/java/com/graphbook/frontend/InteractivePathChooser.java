package com.graphbook.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.graphbook.elements.PDFText;
import com.graphbook.util.CONSTANTS;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO Update the Look
public class InteractivePathChooser { // TODO implement an interface

    public static File choosePDF() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        while (returnValue != JFileChooser.APPROVE_OPTION && returnValue != JFileChooser.CANCEL_OPTION) {
            returnValue = fileChooser.showOpenDialog(null);
        }
        return returnValue == JFileChooser.CANCEL_OPTION ? null : fileChooser.getSelectedFile();
    }

    public static File chooseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);

        while (returnValue != JFileChooser.APPROVE_OPTION && returnValue != JFileChooser.CANCEL_OPTION) {
            returnValue = fileChooser.showOpenDialog(null);
        }
        
        return returnValue == JFileChooser.CANCEL_OPTION ? null : fileChooser.getSelectedFile();
    }

    public static File chooseSavedPDF() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(CONSTANTS.SAVED_PDFS_PATH.toFile());

        int returnValue = fileChooser.showOpenDialog(null);
        while (returnValue != JFileChooser.APPROVE_OPTION && returnValue != JFileChooser.CANCEL_OPTION) {
            returnValue = fileChooser.showOpenDialog(null);
        }
        if (returnValue == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        File potentialPDF = fileChooser.getSelectedFile();
        if (!isValidSavedPDF(potentialPDF)) { // TODO handle case with throwing exceptions and giving messages, when chose file is not a saved pdf
            return null;
        }
        return potentialPDF;
    }

    @SuppressWarnings("unchecked")
    private static boolean isValidSavedPDF(File file) {
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

    public static String getPDFName() { 
        AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
    
        Platform.startup(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Enter PDF Name");
    
            Label label = new Label("Enter the Name for Your PDF:");
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
    
            VBox vbox = new VBox(label, textField, button);
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
    
        Platform.exit();
    
        return (result.get().isEmpty()) ? "unnamed_PDF" : result.get();
    }

    public static void main(String[] args) {
        System.out.println(InteractivePathChooser.getPDFName());
    }
}
