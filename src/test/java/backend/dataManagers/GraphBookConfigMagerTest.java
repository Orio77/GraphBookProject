// package backend.dataManagers;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.nio.file.Path;
// import java.nio.file.Paths;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import
// com.graphbook.backend.service.impl.dataManagers.GraphBookConfigManager;

// @ExtendWith(MockitoExtension.class)
// class GraphBookConfigManagerTest {

// @Mock
// private Path mockPath;

// private GraphBookConfigManager configManager;

// @BeforeEach
// void setUp() {
// // Mock necessary objects and configurations
// mockPath = Paths.get("test/config/path");
// }

// @Test
// void constructor_withPath_initializesCorrectly() {
// assertDoesNotThrow(() -> new GraphBookConfigManager(mockPath));
// }

// @Test
// void constructor_noArgs_usesDefaultPath() {
// assertDoesNotThrow(() -> new GraphBookConfigManager());
// }

// @Test
// void getProperty_existingProperty_returnsCorrectValue() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("Test", "Key", "Value");
// assertEquals("Value", configManager.getProperty("Test", "Key"));
// }

// @Test
// void getProperty_nonExistingProperty_returnsNull() {
// configManager = new GraphBookConfigManager(mockPath);
// assertNull(configManager.getProperty("NonExisting", "Key"));
// }

// @Test
// void addProperty_newProperty_addsSuccessfully() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("NewCategory", "NewKey", "NewValue");
// assertEquals("NewValue", configManager.getProperty("NewCategory", "NewKey"));
// }

// @Test
// void addProperty_existingProperty_updatesValue() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("Category", "Key", "InitialValue");
// configManager.addProperty("Category", "Key", "UpdatedValue");
// assertEquals("UpdatedValue", configManager.getProperty("Category", "Key"));
// }

// @Test
// void getProperties_returnsAllProperties() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("Category", "Key", "Value");
// String properties = configManager.getProperties();
// assertTrue(properties.contains("Category"));
// assertTrue(properties.contains("Key"));
// assertTrue(properties.contains("Value"));
// }

// @Test
// void saveToConfigFile_savesChanges() {
// configManager = new GraphBookConfigManager(mockPath);
// assertDoesNotThrow(() -> configManager.saveToConfigFile());
// }

// @Test
// void getSavedPdfsPath_validPath_returnsPath() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("GraphBookProject", "SavedPDFs",
// "test/saved/pdfs/path");
// assertEquals(Paths.get("test/saved/pdfs/path"),
// configManager.getSavedPdfsPath());
// }

// @Test
// void getSavedPdfsPath_noPath_throwsException() {
// configManager = new GraphBookConfigManager(mockPath);
// Exception exception = assertThrows(RuntimeException.class, () ->
// configManager.getSavedPdfsPath());
// assertTrue(exception.getMessage().contains("There is no saved pdfs path
// property just yet"));
// }

// @Test
// void getResultsPath_validPath_returnsPath() {
// configManager = new GraphBookConfigManager(mockPath);
// configManager.addProperty("GraphBookProject", "Scores", "test/results/path");
// assertEquals(Paths.get("test/results/path"), configManager.getResultsPath());
// }

// @Test
// void getResultsPath_noPath_throwsException() {
// configManager = new GraphBookConfigManager(mockPath);
// Exception exception = assertThrows(RuntimeException.class, () ->
// configManager.getResultsPath());
// assertTrue(exception.getMessage().contains("There is no scores path property
// just yet"));
// }
// }