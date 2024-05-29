import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.impl.dataManagers.PDFBoxHandler;

class PDFBoxHandlerTest {

    private PDFBoxHandler pdfBoxHandler;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        pdfBoxHandler = new PDFBoxHandler();
        testFile = createSamplePDF("Sample PDF content for testing purposes. This content is longer than 300 characters to test the read method properly. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFile.toPath());
    }

    @Test
    void testGetContent() {
        PDFText content = pdfBoxHandler.getContent(testFile);
        assertNotNull(content);
        assertTrue(content.getText().contains("Sample PDF content for testing purposes."));
    }

    @Test
    void testRead() {
        List<PDFText> pages = pdfBoxHandler.read(testFile);
        assertNotNull(pages);
        assertFalse(pages.isEmpty());
        assertTrue(pages.get(0).getText().contains("Sample PDF content for testing purposes."));
    }

    private File createSamplePDF(String text) throws IOException {
        File tempFile = File.createTempFile("test", ".pdf");
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText(text);
            contentStream.endText();
        }

        document.save(tempFile);
        document.close();

        return tempFile;
    }
}
