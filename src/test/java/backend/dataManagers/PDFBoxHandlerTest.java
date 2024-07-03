package backend.dataManagers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.service.impl.dataManagers.PDFBoxHandler;

class PDFBoxHandlerTest {

    @Mock
    private File mockPdfFile;

    @Mock
    private PDDocument mockPDDocument;

    @Mock
    private PDPageTree mockPDPageTree;

    @Mock
    private PDDocumentInformation mockDocumentInformation;

    private PDFBoxHandler pdfBoxHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pdfBoxHandler = new PDFBoxHandler();

        // Setup document information mock
        COSDictionary mockCOSDictionary = new COSDictionary();
        when(mockDocumentInformation.getCOSObject()).thenReturn(mockCOSDictionary);
        when(mockPDDocument.getDocumentInformation()).thenReturn(mockDocumentInformation);
    }

    @Test
    void getContent_whenPdfFileIsNull_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pdfBoxHandler.getContent(null);
        });

        String expectedMessage = "PDF file cannot be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getContent_whenPdfFileIsValid_returnsPDFText() throws IOException {
        URL testPdfUrl = getClass().getResource("/test.pdf");
        if (testPdfUrl == null) {
            throw new FileNotFoundException("Test PDF file not found in resources.");
        }
        File testPdfFile = new File(testPdfUrl.getFile());
        PDFTextStripper stripper = new PDFTextStripper();
        String expectedText = "This is a test PDF content";

        // Load the real PDF document
        try (PDDocument document = PDDocument.load(testPdfFile)) {
            String actualText = stripper.getText(document);
            assertTrue(actualText.contains(expectedText));
        }
    }

    @Test
    void read_whenChosenPdfFileIsNull_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pdfBoxHandler.read(null);
        });

        String expectedMessage = "Chosen PDF file cannot be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void read_whenChosenPdfFileIsValid_returnsListOfPDFText() throws IOException {
        when(mockPdfFile.exists()).thenReturn(true);
        when(mockPdfFile.getPath()).thenReturn("C:\\Users\\macie\\Desktop\\GBP\\graph-book-core\\src\\test\\resources\\test.pdf");

        try (MockedStatic<PDDocument> mocked = Mockito.mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(any(File.class))).thenReturn(mockPDDocument);
            when(mockPDDocument.getPages()).thenReturn(mockPDPageTree);
            when(mockPDDocument.getDocumentInformation()).thenReturn(mockDocumentInformation);

            // Mock the iterator for the page tree
            PDPage mockPDPage = Mockito.mock(PDPage.class);
            Iterator<PDPage> mockIterator = Collections.singletonList(mockPDPage).iterator();
            when(mockPDPageTree.iterator()).thenReturn(mockIterator);

            Splitter splitter = new Splitter();
            List<PDDocument> pages = new ArrayList<>();
            pages.add(mockPDDocument);
            when(splitter.split(mockPDDocument)).thenReturn(pages);

            PDFTextStripper stripper = new PDFTextStripper();
            String expectedText = "This is a test PDF content";
            when(stripper.getText(mockPDDocument)).thenReturn(expectedText);

            List<PDFText> result = pdfBoxHandler.read(mockPdfFile);

            assertTrue(result.size() == 1 && result.get(0).getText().equals(expectedText));
        }
    }

    @Test
    void read_whenIOExceptionOccursOnLoad_throwsRuntimeException() throws IOException {
        when(mockPdfFile.exists()).thenReturn(true);

        try (MockedStatic<PDDocument> mocked = Mockito.mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(mockPdfFile)).thenThrow(new IOException());

            Exception exception = assertThrows(RuntimeException.class, () -> {
                pdfBoxHandler.read(mockPdfFile);
            });

            String expectedMessage = "IOException occurred while loading a PDF. Check the error log for details.";
            assertTrue(exception.getMessage().contains(expectedMessage));
        }
    }

    @Test
    void read_whenIOExceptionOccursOnSplit_throwsRuntimeException() throws IOException {
        when(mockPdfFile.exists()).thenReturn(true);

        try (MockedStatic<PDDocument> mocked = Mockito.mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(mockPdfFile)).thenReturn(mockPDDocument);
            when(mockPDDocument.getPages()).thenReturn(mockPDPageTree);
            when(mockPDDocument.getDocumentInformation()).thenReturn(mockDocumentInformation);

            // Mock the iterator for the page tree
            PDPage mockPDPage = Mockito.mock(PDPage.class);
            Iterator<PDPage> mockIterator = Collections.singletonList(mockPDPage).iterator();
            when(mockPDPageTree.iterator()).thenReturn(mockIterator);

            Splitter splitter = new Splitter();
            when(splitter.split(mockPDDocument)).thenThrow(new IOException());

            Exception exception = assertThrows(RuntimeException.class, () -> {
                pdfBoxHandler.read(mockPdfFile);
            });

            String expectedMessage = "IOException occurred while splitting the PDF. Check the error log for details.";
            assertTrue(exception.getMessage().contains(expectedMessage));
        }
    }

    @Test
    void read_whenIOExceptionOccursOnStrip_throwsRuntimeException() throws IOException {
        when(mockPdfFile.exists()).thenReturn(true);

        try (MockedStatic<PDDocument> mocked = Mockito.mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(mockPdfFile)).thenReturn(mockPDDocument);
            when(mockPDDocument.getPages()).thenReturn(mockPDPageTree);
            when(mockPDDocument.getDocumentInformation()).thenReturn(mockDocumentInformation);

            // Mock the iterator for the page tree
            PDPage mockPDPage = Mockito.mock(PDPage.class);
            Iterator<PDPage> mockIterator = Collections.singletonList(mockPDPage).iterator();
            when(mockPDPageTree.iterator()).thenReturn(mockIterator);

            Splitter splitter = new Splitter();
            List<PDDocument> pages = new ArrayList<>();
            pages.add(mockPDDocument);
            when(splitter.split(mockPDDocument)).thenReturn(pages);

            PDFTextStripper stripper = new PDFTextStripper();
            when(stripper.getText(mockPDDocument)).thenThrow(new IOException());

            Exception exception = assertThrows(RuntimeException.class, () -> {
                pdfBoxHandler.read(mockPdfFile);
            });

            String expectedMessage = "IOException occurred while extracting text from a page. Check the error log for details.";
            assertTrue(exception.getMessage().contains(expectedMessage));
        }
    }

    @Test
    void read_omitsPagesWithLittleText() throws IOException {
        when(mockPdfFile.exists()).thenReturn(true);

        try (MockedStatic<PDDocument> mocked = Mockito.mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(mockPdfFile)).thenReturn(mockPDDocument);
            when(mockPDDocument.getPages()).thenReturn(mockPDPageTree);
            when(mockPDDocument.getDocumentInformation()).thenReturn(mockDocumentInformation);

            // Mock the iterator for the page tree
            PDPage mockPDPage = Mockito.mock(PDPage.class);
            Iterator<PDPage> mockIterator = Collections.singletonList(mockPDPage).iterator();
            when(mockPDPageTree.iterator()).thenReturn(mockIterator);

            Splitter splitter = new Splitter();
            List<PDDocument> pages = new ArrayList<>();
            pages.add(mockPDDocument);
            when(splitter.split(mockPDDocument)).thenReturn(pages);

            PDFTextStripper stripper = new PDFTextStripper();
            when(stripper.getText(mockPDDocument)).thenReturn("");

            List<PDFText> result = pdfBoxHandler.read(mockPdfFile);

            assertTrue(result.isEmpty(), "Expected no PDFText objects for pages with little to no text");
        }
    }
}
