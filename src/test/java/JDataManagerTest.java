

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.impl.dataManagers.JDataManager;

class JDataManagerTest {

    private JDataManager jDataManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        jDataManager = new JDataManager();
        // CONSTANTS.SAVED_PDFS_PATH = tempDir; // Make this field not final for it to work
    }

    @Test
    void testSavePDF() {
        List<PDFText> pdfTexts = Arrays.asList(new PDFText("Sample text"));
        String pdfName = "testPDF";

        boolean result = jDataManager.savePDF(pdfTexts, pdfName);

        assertTrue(result);
        assertTrue(Files.exists(tempDir.resolve(pdfName)));
    }

    @Test
    void testCreateDir() throws IOException {
        Path newDir = jDataManager.createDir(tempDir);

        assertNotNull(newDir);
        assertTrue(Files.exists(newDir));
        assertTrue(Files.isDirectory(newDir));
    }

    @Test
    void testCreateFile() throws IOException {
        String fileName = "testFile";
        Path newFile = jDataManager.createFile(tempDir.toString(), fileName);

        assertNotNull(newFile);
        assertTrue(Files.exists(newFile));
        assertTrue(Files.isRegularFile(newFile));
    }

    @Test
    void testAddToMetadata() throws IOException {
        Path metadataFile = jDataManager.createFile(tempDir.toString(), "metadata");
        jDataManager.addToMetadata(metadataFile, "Label", "Value");

        List<String> lines = Files.readAllLines(metadataFile);
        assertTrue(lines.contains("Label: Value"));
    }

    @Test
    void testWriteObjectAndReadSavedPDF() throws IOException, ClassNotFoundException {
        List<PDFText> pdfTexts = Arrays.asList(new PDFText("Sample text"));
        String pdfName = "testPDF";

        boolean saveResult = jDataManager.savePDF(pdfTexts, pdfName);
        assertTrue(saveResult);

        File savedPDF = tempDir.resolve(pdfName).resolve(pdfName + ".txt").toFile();
        Object readObject = jDataManager.readSavedPDF(savedPDF);

        assertNotNull(readObject);
        assertTrue(readObject instanceof List);
        assertEquals(pdfTexts, readObject);
    }

    @Test
    void testDeleteAllSavedPDFs() throws IOException {
        jDataManager.savePDF(Arrays.asList(new PDFText("Sample text")), "testPDF1");
        jDataManager.savePDF(Arrays.asList(new PDFText("Sample text")), "testPDF2");

        boolean deleteResult = jDataManager.deleteAllSavedPDFs();

        assertTrue(deleteResult);
        assertTrue(Files.list(tempDir).noneMatch(Files::isDirectory));
    }

    @Test
    void testLoadPDF() throws IOException {
        List<PDFText> pdfTexts = Arrays.asList(new PDFText("Sample text"));
        String pdfName = "testPDF";

        jDataManager.savePDF(pdfTexts, pdfName);

        File savedPDF = tempDir.resolve(pdfName).resolve(pdfName + ".txt").toFile();
        List<PDFText> loadedPDF = jDataManager.loadPDF(savedPDF);

        assertEquals(pdfTexts, loadedPDF);

    }

    
}
