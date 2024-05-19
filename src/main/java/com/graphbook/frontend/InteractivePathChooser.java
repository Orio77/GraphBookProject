package com.graphbook.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.graphbook.elements.PDFText;
import com.graphbook.util.CONSTANTS;

public class InteractivePathChooser { // TODO implement an interface

    public File choosePDF() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        while (returnValue != JFileChooser.APPROVE_OPTION && returnValue != JFileChooser.CANCEL_OPTION) {
            returnValue = fileChooser.showOpenDialog(null);
        }
        return returnValue == JFileChooser.CANCEL_OPTION ? null : fileChooser.getSelectedFile();
    }

    public File chooseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);

        while (returnValue != JFileChooser.APPROVE_OPTION && returnValue != JFileChooser.CANCEL_OPTION) {
            returnValue = fileChooser.showOpenDialog(null);
        }
        
        return returnValue == JFileChooser.CANCEL_OPTION ? null : fileChooser.getSelectedFile();
    }

    public File chooseSavedPDF() {
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

    public boolean isValidSavedPDF(File file) {
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

    public static void main(String[] args) {
        new InteractivePathChooser().chooseSavedPDF();
    }
}
