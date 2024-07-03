package backend.initializer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.graphbook.backend.service.impl.initializer.SafeGraphBookInitializer;

class SafeGraphBookInitializerTest {

    private SafeGraphBookInitializer safeGraphBookInitializer;

    @Mock
    private File mockInitialDir;

    @Mock
    private Path mockPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockInitialDir.toPath()).thenReturn(mockPath);
        when(mockPath.resolve(any(String.class))).thenReturn(mockPath);
        safeGraphBookInitializer = new SafeGraphBookInitializer(mockInitialDir);
    }

    @Test
    void constructor_whenInitialDirIsValid_initializesConfigManager() {
        assertNotNull(safeGraphBookInitializer);
    }

    @Test
    void setProjectPath_whenChosenDirIsValid_updatesConfigProperties() {
        // Simulate valid directory by not throwing an exception
        assertDoesNotThrow(() -> safeGraphBookInitializer.setProjectPath(mockInitialDir));
    }

    @Test
    void setProjectPath_whenChosenDirIsInvalid_throwsException() {
        // Simulate invalid directory by throwing an exception
        doThrow(RuntimeException.class).when(mockPath).toUri();
        assertThrows(RuntimeException.class, () -> safeGraphBookInitializer.setProjectPath(mockInitialDir));
    }

    @Test
    void createNecessaryDirectories_createsDirectoriesIfNotExist() {
        // Assume directories do not exist and can be created
        File mockDirectory = mock(File.class);
        when(mockPath.toFile()).thenReturn(mockDirectory);
        when(mockDirectory.exists()).thenReturn(false);
        when(mockDirectory.mkdir()).thenReturn(true);

        assertDoesNotThrow(() -> safeGraphBookInitializer.createNecessaryDirectories());
    }

    @Test
    void createNecessaryDirectories_whenDirectoryCannotBeCreated_throwsException() {
        // Assume directories do not exist and cannot be created
        File mockDirectory = mock(File.class);
        when(mockPath.toFile()).thenReturn(mockDirectory);
        when(mockDirectory.exists()).thenReturn(false);
        when(mockDirectory.mkdir()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> safeGraphBookInitializer.createNecessaryDirectories());
    }

    // MAKE getConfigFilePath() PUBLIC FOR TESTS !!!

    // @Test
    // void getConfigFilePath_whenCalled_returnsCorrectPath() {
    //     // Assuming getConfigFilePath() is correctly implemented and returns a valid path
    //     Path expectedPath = Paths.get("expected/path/to/config.json");
    //     when(mockPath.resolve("src/main/resources")).thenReturn(expectedPath);

    //     Path actualPath = safeGraphBookInitializer.getConfigFilePath("config.json");
    //     assertEquals(expectedPath, actualPath);
    // }

    // @Test
    // void getConfigFilePath_whenProjectRootNotFound_throwsRuntimeException() {
    //     // Simulate getConfigFilePath() failing to find the project root and throwing an exception
    //     doThrow(RuntimeException.class).when(mockPath).getParent();
    //     assertThrows(RuntimeException.class, () -> safeGraphBookInitializer.getConfigFilePath("config.json"));
    // }
}