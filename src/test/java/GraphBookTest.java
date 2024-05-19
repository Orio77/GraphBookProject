import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.graphbook.GraphBook;
import com.graphbook.frontend.InteractivePathChooser;
import com.graphbook.util.CONSTANTS;

public class GraphBookTest {

    @Test
    public void testSetProjectPath() {
        // Mock the InteractivePathChooser
        InteractivePathChooser mockChooser = mock(InteractivePathChooser.class);
        Path expectedPath = Paths.get("C:\\Users\\macie\\GraphBookTestDir\\");
        when(mockChooser.chooseDirectory()).thenReturn(expectedPath.toFile());

        // Instantiate GraphBook and call setProjectPath
        GraphBook graphBook = new GraphBook();
        
        // Uncomment
        // graphBook.setProjectPath(); 

        // Assert that CONSTANTS.PROJECT_PATH is equal to the predefined directory
        assertEquals(expectedPath, CONSTANTS.PROJECT_PATH);
    }
}