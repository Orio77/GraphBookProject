package backend.plotTools;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.graphbook.backend.model.Pair;
import com.graphbook.backend.service.impl.plotTools.PlotManager;

class PlotManagerTest {

    private PlotManager plotManager;

    @Mock
    private Map<String, List<Pair<String, Double>>> mockScores;
    @Mock
    private File mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        plotManager = new PlotManager();
    }

    @Test
    void showGraph_whenScoresAreValid_doesNotThrowException() {
    }

    @Test
    void sendPlotData_whenScoresAreValid_returnsJsonResponse() {
    }

    @Test
    void sendPlotData_whenScoresAreInvalid_throwsException() {
    }

    @Test
    void savePlotData_whenScoresAndLabelAreValid_doesNotThrowException() {
    }

    @Test
    void savePlotData_whenScoresAreInvalid_throwsException() {
    }

    @Test
    void savePlotData_whenLabelIsInvalid_throwsException() {
    }

    @Test
    void loadPlot_whenLabelIsValid_loadsPlot() {
    }

    @Test
    void loadPlot_whenLabelIsInvalid_throwsException() {
    }

    @Test
    void loadPlot_whenFileIsValid_loadsPlot() {
    }

    @Test
    void loadPlot_whenFileIsInvalid_throwsException() {
    }
}