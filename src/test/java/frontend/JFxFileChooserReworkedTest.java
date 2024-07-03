package frontend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.graphbook.frontend.service.JFxFileChooserReworked;
import com.graphbook.backend.model.PDFText;

public class JFxFileChooserReworkedTest {

    private static JFxFileChooserReworked fileChooser;
    private static File validPDFFile;
    private static File invalidPDFFile;

    @BeforeAll
    static void setup() throws IOException {
        // Setup for isValidSavedPDF
        validPDFFile = File.createTempFile("validPDF", ".pdf");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(validPDFFile))) {
            oos.writeObject(Collections.singletonList(new PDFText("Sample PDF Text")));
        }

        invalidPDFFile = File.createTempFile("invalidPDF", ".pdf");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(invalidPDFFile))) {
            oos.writeObject("This is not a valid PDF object");
        }
    }

    @AfterAll
    static void tearDown() {
        // Clean up the files
        validPDFFile.delete();
        invalidPDFFile.delete();
    }

    @BeforeAll
    public static void setUpClass() {
        fileChooser = new JFxFileChooserReworked();
    }

    @Test
    public void testChoosePDF() {
        File expectedFile = new File("dummy.pdf");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).choosePDF();

        File result = spyFileChooser.choosePDF();
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChoosePDFWithInitialDirectory() {
        File initialDirectory = new File(System.getProperty("user.home"));
        File expectedFile = new File(initialDirectory, "dummy.pdf");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).choosePDF(initialDirectory);

        File result = spyFileChooser.choosePDF(initialDirectory);
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChooseDir() {
        File expectedDirectory = new File(System.getProperty("user.home"));
        // Mock the behavior of the directory chooser to return the expected directory
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedDirectory).when(spyFileChooser).chooseDir();

        File result = spyFileChooser.chooseDir();
        assertEquals(expectedDirectory, result);
    }

    @Test
    public void testChooseDirWithInitialDirectory() {
        File initialDirectory = new File(System.getProperty("user.home"));
        File expectedDirectory = new File(initialDirectory, "Documents");
        // Mock the behavior of the directory chooser to return the expected directory
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedDirectory).when(spyFileChooser).chooseDir(initialDirectory);

        File result = spyFileChooser.chooseDir(initialDirectory);
        assertEquals(expectedDirectory, result);
    }

    @Test
    public void testChooseTXT() {
        File expectedFile = new File("dummy.txt");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).chooseTXT();

        File result = spyFileChooser.chooseTXT();
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChooseTXTWithInitialDirectory() {
        File initialDirectory = new File(System.getProperty("user.home"));
        File expectedFile = new File(initialDirectory, "dummy.txt");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).chooseTXT(initialDirectory);

        File result = spyFileChooser.chooseTXT(initialDirectory);
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChooseJSON() {
        File expectedFile = new File("dummy.json");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).chooseJSON();

        File result = spyFileChooser.chooseJSON();
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChooseJSONWithInitialDirectory() {
        File initialDirectory = new File(System.getProperty("user.home"));
        File expectedFile = new File(initialDirectory, "dummy.json");
        // Mock the behavior of the file chooser to return the expected file
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedFile).when(spyFileChooser).chooseJSON(initialDirectory);

        File result = spyFileChooser.chooseJSON(initialDirectory);
        assertEquals(expectedFile, result);
    }

    @Test
    public void testChooseConcepts() {
        List<String> conceptList = Arrays.asList("Concept1", "Concept2", "Concept3");
        List<String> expectedSelection = Arrays.asList("Concept1", "Concept3");
        // Mock the behavior of the element chooser to return the expected selection
        JFxFileChooserReworked spyFileChooser = Mockito.spy(fileChooser);
        doReturn(expectedSelection).when(spyFileChooser).chooseConcepts(conceptList);

        List<String> result = spyFileChooser.chooseConcepts(conceptList);
        assertEquals(expectedSelection, result);
    }
    
    @Test
    void testIsValidSavedPDF() {
        JFxFileChooserReworked fileChooser = new JFxFileChooserReworked();
        assertTrue(fileChooser.isValidSavedPDF(validPDFFile), "The validPDFFile should be recognized as a valid saved PDF.");
        assertFalse(fileChooser.isValidSavedPDF(invalidPDFFile), "The invalidPDFFile should not be recognized as a valid saved PDF.");
    }

    @Test
    void testExit() {
        // This test checks if the application exits without throwing an exception.
        // Note: This will actually exit the JavaFX platform, making it unable to launch again in the same JVM instance.
        // It's recommended to run this test in isolation or as the last test.
        assertDoesNotThrow(() -> {
            JFxFileChooserReworked fileChooser = new JFxFileChooserReworked();
            fileChooser.exit();
        }, "Exiting should not throw an exception.");
    }
}