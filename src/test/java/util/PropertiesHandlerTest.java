package util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.graphbook.util.PropertiesHandler;

class PropertiesHandlerTest {

    @Test
    void testGetURIWithNullURI() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            PropertiesHandler.getURI(null);
        }, "A RuntimeException is expected when URI label is null");

        String expectedMessage = "URI creation failed. Provided String was null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected text");
    }

    @Test
    void testGetURIWithInvalidURI() {
        // Assuming "invalidURI" is an invalid URI label that violates RFC 2396
        String invalidURI = "http:// this is not a valid URI";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            PropertiesHandler.getURI(invalidURI);
        }, "A RuntimeException is expected when URI label is invalid");

        String expectedMessage = "No uri with such label in the config";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected text");
    }
}