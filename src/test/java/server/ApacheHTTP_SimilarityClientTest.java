package server;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.server.impl.ApacheHTTP_SimilarityClient;
import com.graphbook.server.impl.PythonManager;

class ApacheHTTP_SimilarityClientTest {

    private ApacheHTTP_SimilarityClient similarityClient;
    private PythonManager pyManager;

    @BeforeEach
    void setUp() {
        similarityClient = new ApacheHTTP_SimilarityClient();
        pyManager = new PythonManager();
        pyManager.runPythonAIServer();
    }

    @Test
    void testGetSimilarityBatchResponseWithValidInput() {
        List<PDFText> pdfTexts = List.of(new PDFText("Sample PDF Text"), new PDFText("Another Sample PDF Text"));
        Map<Integer, List<Pair<Integer, Double>>> expected = new HashMap<>();
        expected.put(0, List.of(new Pair<>(1, anyDouble())));

        // Run the python server!!!
        Map<Integer, List<Pair<Integer, Double>>> result = similarityClient.getSimilarityBatchResponse(pdfTexts, "label");

        assertNotNull(result);
        assertNotNull(result.get(0));
        assertInstanceOf(ArrayList.class, result.get(0));
        assertInstanceOf(Pair.class, result.get(0).get(0));
        assertInstanceOf(Double.class, result.get(0).get(0).getEl2());
    }

    @Test
    void testGetSimilarityBatchResponseWithNullInput() {
        assertThrows(RuntimeException.class, () -> similarityClient.getSimilarityBatchResponse(null, "label"));
    }

    @Test
    void testGetSimilarityBatchResponseWithEmptyList() {
        List<PDFText> pdfTexts = new ArrayList<>();
        assertThrows(RuntimeException.class, () -> similarityClient.getSimilarityBatchResponse(pdfTexts, "label"));
    }

    @Test
    void testGetConceptScoresWithValidInput() {
        List<PDFText> pdfTexts = List.of(new PDFText("Sample PDF Text"));

        // Assuming the method call returns the expected result
        List<Pair<Integer, Double>> result = similarityClient.getConceptScores(pdfTexts, "label", "concept");

        // Run Python Server!!!
        assertNotNull(result);
        assertNotNull(result.get(0));
        assertInstanceOf(Pair.class, result.get(0));
        assertInstanceOf(Integer.class, result.get(0).getEl1());
        assertInstanceOf(Double.class, result.get(0).getEl2());
    }

    @Test
    void testGetConceptScoresWithNullInput() {
        assertThrows(RuntimeException.class, () -> similarityClient.getConceptScores(null, "label", "concept"));
    }

    @Test
    void testGetConceptScoresWithEmptyList() {
        List<PDFText> pdfTexts = new ArrayList<>();
        assertThrows(RuntimeException.class, () -> similarityClient.getConceptScores(pdfTexts, "label", "concept"));
    }

    // MAKE sendPDF() PUBLIC FOR TESTS!!!

    // @Test
    // void testSendPDFWithValidInput() {
    //     List<PDFText> pdfTexts = List.of(new PDFText("Sample PDF Text"));
    //     String response = similarityClient.sendPDF(pdfTexts, "label", "concept", "ConceptURI");

    //     assertNotNull(response);
    //     try {
    //         new ObjectMapper().readTree(response);
    //         // If parsing succeeds, the response is valid JSON
    //     } catch (JsonProcessingException e) {
    //         fail("Response is not in valid JSON format");
    //     }
    // }

    // @Test
    // void testSendPDFWithNullInput() {
    //     assertThrows(RuntimeException.class, () -> similarityClient.sendPDF(null, "label", "concept", "URI"));
    // }

    // @Test
    // void testSendPDFWithEmptyList() {
    //     List<PDFText> pdfTexts = new ArrayList<>();
    //     assertThrows(RuntimeException.class, () -> similarityClient.sendPDF(pdfTexts, "label", "concept", "URI"));
    // }
}