import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.impl.database.NeoDatabase;

class NeoDatabaseTest {
    @Mock
    private Driver mockDriver;
    @Mock
    private Session mockSession;
    @Mock
    private Transaction mockTransaction;

    private NeoDatabase neoDatabase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        neoDatabase = new NeoDatabase();
        // neoDatabase.driver = mockDriver; // Make the driver public for it to work

        when(mockDriver.session()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
    }

    @Test
    void testSave_withNullTexts() {
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.save(null, "label");
        });
    }

    @Test
    void testSave_withEmptyTexts() {
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.save(Collections.emptyList(), "label");
        });
    }

    @Test
    void testSave_withNullLabel() {
        List<PDFText> texts = Arrays.asList(new PDFText("text1"));
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.save(texts, null);
        });
    }

    @Test
    void testSave_withEmptyLabel() {
        List<PDFText> texts = Arrays.asList(new PDFText("text1"));
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.save(texts, "");
        });
    }

    @Test
    void testCreateEdge_withNullText1() {
        PDFText text2 = new PDFText("text2");

        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdge(null, text2, 0.8, 1);
        });
    }

    @Test
    void testCreateEdge_withNullText2() {
        PDFText text1 = new PDFText("text1");

        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdge(text1, null, 0.8, 1);
        });
    }

    @Test
    void testCreateEdges_withNullResult() {
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdges(null, "label");
        });
    }

    @Test
    void testCreateEdges_withEmptyResult() {
        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdges(new HashMap<>(), "label");
        });
    }

    @Test
    void testCreateEdges_withNullLabel() {
        Map<Integer, List<Pair<Integer, Double>>> result = new HashMap<>();
        result.put(1, Arrays.asList(new Pair<>(2, 0.5)));

        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdges(result, null);
        });
    }

    @Test
    void testCreateEdges_withEmptyLabel() {
        Map<Integer, List<Pair<Integer, Double>>> result = new HashMap<>();
        result.put(1, Arrays.asList(new Pair<>(2, 0.5)));

        assertThrows(IllegalArgumentException.class, () -> {
            neoDatabase.createEdges(result, "");
        });
    }
}
