import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.graphbook.util.PDFReader;
import com.graphbook.elements.PDFText;

public class PDFReadingTest {

    @Test
    public void testReadValidPDF() throws URISyntaxException {
        PDFReader reader = new PDFReader();
        URI uri = new File("path/to/valid.pdf").toURI();
        PDFText text = reader.read(uri);
        // Assuming the content of valid.pdf is "Hello, World!"
        assertEquals("Hello, World!", text.getText());
    }

    @Test
    public void testReadInvalidPath() throws URISyntaxException {
        PDFReader reader = new PDFReader();
        URI uri = new File("path/to/nonexistent.pdf").toURI();
        PDFText text = reader.read(uri);
        assertTrue(text.getText().contains("No such file or directory"));
    }

    @Test
    public void testReadNonPDF() throws URISyntaxException {
        PDFReader reader = new PDFReader();
        URI uri = new File("path/to/non-pdf.txt").toURI();
        PDFText text = reader.read(uri);
        assertTrue(text.getText().contains("Error: End-of-File, expected line"));
    }
}