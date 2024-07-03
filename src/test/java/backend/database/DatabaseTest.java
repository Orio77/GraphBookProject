package backend.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import com.graphbook.backend.model.PDFText;
import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.impl.database.NeoDatabase;

@ExtendWith(MockitoExtension.class)
public class DatabaseTest {

    @Mock
    private Session mockSession;

    @Mock
    private Driver mockDriver;

    @Mock
    private Transaction mockTransaction;

    @InjectMocks
    private NeoDatabase database;

    @BeforeEach
    void setUp() {
        // No need to initialize mockDriver here, it's injected directly
    }

    @Test
    void testConnect() {
        // Arrange
        try (MockedStatic<GraphDatabase> graphDatabaseMockedStatic = Mockito.mockStatic(GraphDatabase.class)) {

            graphDatabaseMockedStatic.when(() -> GraphDatabase.driver(anyString(), any(Config.class)))
                    .thenReturn(mockDriver);
            when(mockDriver.session()).thenReturn(mockSession);

            // Act
            database.connect();

            // Assert
            verify(mockDriver, times(1)).verifyConnectivity();
        }
    }


    @Test
    public void testSave_Success() {
        // Arrange
        List<PDFText> texts = List.of(new PDFText("Text1"), new PDFText("Text2"));
        String label = "Label";
        
        try (MockedStatic<GraphDatabase> graphDatabaseMockedStatic = Mockito.mockStatic(GraphDatabase.class)) {
            graphDatabaseMockedStatic.when(() -> GraphDatabase.driver(anyString(), any(AuthToken.class)))
                    .thenReturn(mockDriver);
            when(mockDriver.session()).thenReturn(mockSession);
            when(mockSession.run(anyString(), anyMap())).thenReturn(mock(Result.class));

            // Act
            database.save(texts, label);

            // Assert
            verify(mockSession, times(1)).run(anyString(), anyMap());
        }
    }

    @Test
    public void testSave_NullTexts() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            database.save(null, "Label");
        });
        assertEquals("Texts cannot be null", thrown.getMessage());
    }

    @Test
    public void testSave_EmptyTexts() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            database.save(Collections.emptyList(), "Label");
        });
        assertEquals("Texts cannot be empty", thrown.getMessage()); // Corrected message to match the test case
    }

    @Test
    public void testSave_NullLabel() {
        List<PDFText> texts = List.of(new PDFText("Text1"));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            database.save(texts, null);
        });
        assertEquals("Label cannot be null", thrown.getMessage());
    }

    @Test
    public void testCreateEdges_WithResultMap() {
        Map<Integer, List<Pair<Integer, Double>>> result = new HashMap<>();
        result.put(1, List.of(new Pair<>(2, 0.5)));
        String label = "Label";

        when(mockSession.run(anyString(), anyMap())).thenReturn(mock(Result.class));

        database.createEdges(result, label);

        verify(mockSession, times(1)).run(anyString(), anyMap());
    }

    @Test
    public void testCreateEdges_WithConceptScores() {
        List<Pair<Integer, Double>> conceptScores = List.of(new Pair<>(1, 0.8));
        String concept = "Concept";
        String label = "Label";

        when(mockSession.run(anyString(), anyMap())).thenReturn(mock(Result.class));

        database.createEdges(concept, conceptScores, label);

        verify(mockSession, times(1)).run(anyString(), anyMap());
    }

    @Test
    public void testClearAllEdges() {
        when(mockSession.run(anyString())).thenReturn(mock(Result.class));
        
        database.clearAllEdges();
        
        verify(mockSession).run("MATCH ()-[r]-() DELETE r");
    }

    @Test
    public void testGetConceptList() {
        String query = "MATCH (n:Concept) RETURN n.text AS text";
        Result mockResult = mock(Result.class);
        when(mockSession.run(query)).thenReturn(mockResult);
        when(mockResult.list(record -> record.get("text").asString())).thenReturn(List.of("Concept1", "Concept2"));

        List<String> concepts = database.getConceptList();

        assertEquals(List.of("Concept1", "Concept2"), concepts);
    }

    @Test
    public void testGetConceptNodes() {
        List<String> chosenConcepts = List.of("Concept1");
        String query = "MATCH (c:Concept {text: $conceptName})-[r]->(n) RETURN n.elementId AS elementId, r.weight AS weight";
        Result mockResult = mock(Result.class);
        when(mockSession.run(query, Map.of("conceptName", "Concept1"))).thenReturn(mockResult);
        when(mockResult.list()).thenReturn(List.of(mock(Record.class)));

        Map<String, List<Pair<String, Double>>> conceptNodes = database.getConceptNodes(chosenConcepts);

        assertNotNull(conceptNodes);
        assertTrue(conceptNodes.containsKey("Concept1"));
    }

    @Test
    public void testReset() {
        when(mockSession.run(anyString())).thenReturn(mock(Result.class));
        
        database.reset();
        
        verify(mockSession).run("MATCH (n) DETACH DELETE n");
    }

    @Test
    public void testDisconnect() {
        database.disconnect();
        verify(mockDriver).close();
    }
}
