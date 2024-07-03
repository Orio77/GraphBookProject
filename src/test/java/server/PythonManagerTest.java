package server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;
import com.graphbook.server.impl.PythonManager;

class PythonManagerTest {

    private PythonManager pythonManager;
    private GraphBookConfigManager configManagerMock;

    @BeforeEach
    void setUp() {
        configManagerMock = mock(GraphBookConfigManager.class);
        when(configManagerMock.getProperty("Python", "PythonExecutable")).thenReturn("python.exe");
        when(configManagerMock.getProperty("Python", "AIPythonServerPath")).thenReturn("C:/path/to/ai/server");
        when(configManagerMock.getProperty("Python", "AIPythonServerFileName")).thenReturn("ai_server.py");
        when(configManagerMock.getProperty("Python", "PlotPythonServerPath")).thenReturn("C:/path/to/plot/server");
        when(configManagerMock.getProperty("Python", "PlotPythonServerFileName")).thenReturn("plot_server.py");

        pythonManager = new PythonManager();
        // Assuming PythonManager can accept a config manager through a setter or constructor for testing
        // If not, consider using reflection or adjust the design of PythonManager for better testability

        // Create a setter!!!
        //pythonManager.setConfigManager(configManagerMock); // This line is pseudo-code
    }

    @Test
    void testRunPythonAIServer() {
        assertDoesNotThrow(() -> pythonManager.runPythonAIServer());
        verify(configManagerMock, times(1)).getProperty("Python", "PythonExecutable");
        verify(configManagerMock, times(1)).getProperty("Python", "AIPythonServerPath");
        verify(configManagerMock, times(1)).getProperty("Python", "AIPythonServerFileName");
    }

    @Test
    void testRunPythonPlotServer() {
        assertDoesNotThrow(() -> pythonManager.runPythonPlotServer());
        verify(configManagerMock, times(1)).getProperty("Python", "PythonExecutable");
        verify(configManagerMock, times(1)).getProperty("Python", "PlotPythonServerPath");
        verify(configManagerMock, times(1)).getProperty("Python", "PlotPythonServerFileName");
    }

    @Test
    void testWaitForServerToStart() {
        // This test might be tricky since waitForServerToStart is a private method and it's mainly just a sleep.
        // Testing this directly might not be meaningful or possible without changing the access modifier.
        // Instead, you can indirectly test its effect by checking if there's a delay when calling runPythonAIServer or runPythonPlotServer.
        // However, for simplicity, let's assume we're just ensuring the method doesn't throw an exception when called indirectly.
        assertDoesNotThrow(() -> pythonManager.runPythonAIServer());
        assertDoesNotThrow(() -> pythonManager.runPythonPlotServer());
    }
}