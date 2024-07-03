package backend.dataManagers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.graphbook.backend.service.impl.dataManagers.JDataManager;

class JDataManagerTest {

    private JDataManager jDataManager;

    @Mock
    private File mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jDataManager = new JDataManager();
    }

    @AfterEach
    void tearDown() {
        try {
            Path path1 = Paths.get("C:\\Users\\macie\\GraphBookDirTest\\saved\\plots\\plot\\plot.txt");
            Path path2 = Paths.get("C:\\Users\\macie\\GraphBookDirTest\\saved\\plots\\plot\\metadata.txt");
            Path path3 = Paths.get("C:\\Users\\macie\\GraphBookDirTest\\saved\\plots\\plot");
            Files.deleteIfExists(path1);
            Files.deleteIfExists(path2);
            Files.deleteIfExists(path3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void savePDF_whenObjectIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.savePDF(null, "test.pdf");
        });
        assertEquals("Object to save cannot be null", exception.getMessage());
    }

    @Test
    void savePDF_whenPdfNameIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.savePDF(new Object(), "");
        });
        assertEquals("pdfName cannot be null or empty", exception.getMessage());
    }

    @Test
    void savePlot_whenObjectIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.savePlot(null, "plot");
        });
        assertEquals("Object to save cannot be null", exception.getMessage());
    }

    @Test
    void savePlot_whenLabelIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.savePlot(new Object(), "");
        });
        assertEquals("pdfName cannot be null or empty", exception.getMessage());
    }

    @Test
    void savePlot_successfullySavesPlot_returnsTrue() {
        boolean result = jDataManager.savePlot(new String(), "plot");
        assertTrue(result);
    }

    @Test
    void createDir_whenPathIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.createDir(null);
        });
        assertEquals("Path cannot be null", exception.getMessage());
    }

    @Test
    void createFile_whenPathAsStringIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.createFile("", "testFile");
        });
        assertEquals("Path cannot be null or empty", exception.getMessage());
    }

    @Test
    void createFile_whenFileNameIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.createFile("path/to/dir", "");
        });
        assertEquals("fileName cannot be null or empty", exception.getMessage());
    }

    @Test
    void addToMetadata_whenPathIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.addToMetadata(null, "label", "value");
        });
        assertEquals("Path cannot be null", exception.getMessage());
    }

    @Test
    void addToMetadata_whenLabelIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.addToMetadata(Paths.get("path/to/file"), "", "value");
        });
        assertEquals("label cannot be null or empty", exception.getMessage());
    }

    @Test
    void addToMetadata_whenValueIsEmpty_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.addToMetadata(Paths.get("path/to/file"), "label", "");
        });
        assertEquals("value cannot be null or empty", exception.getMessage());
    }

    @Test
    void readSavedPDF_whenFileIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.readSavedPDF(null);
        });
        assertEquals("File cannot be null", exception.getMessage());
    }

    @Test
    void deleteAllSavedPDFs_successfullyDeletesAllPdfs_returnsTrue() {
        boolean result = jDataManager.deleteAllSavedPDFs();
        assertTrue(result);
    }

    @Test
    void loadPDF_whenSavedPDFIsNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jDataManager.loadPDF(null);
        });
        assertEquals("Saved PDF file cannot be null", exception.getMessage());
    }
}
