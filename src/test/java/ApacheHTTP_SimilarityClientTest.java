import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.graphbook.server.ApacheHTTP_SimilarityClient;
import com.graphbook.util.interfaces.IAIResponseSimilarityScoreExtractor;
import com.graphbook.util.interfaces.IDataManager;

public class ApacheHTTP_SimilarityClientTest {

    @Test
    public void testGetSimilarityResponse() throws IOException {
    // Arrange
    CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class);
    HttpPost post = Mockito.mock(HttpPost.class);
    CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
    StatusLine statusLine = Mockito.mock(StatusLine.class);
    HttpEntity entity = Mockito.mock(HttpEntity.class);
    IAIResponseSimilarityScoreExtractor extractor = Mockito.mock(IAIResponseSimilarityScoreExtractor.class);

    Mockito.when(client.execute(post)).thenReturn(response);
    Mockito.when(response.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(200);
    Mockito.when(response.getEntity()).thenReturn(entity);
    Mockito.when(EntityUtils.toString(entity)).thenReturn("{\"similarity\": \"90.0\"}");
    Mockito.when(extractor.extract("90.0")).thenReturn(90.0);

    // ApacheHTTP_SimilarityClient clientUnderTest = new ApacheHTTP_SimilarityClient(extractor);

    // Act
    // double result = (double) clientUnderTest.getSimilarityResponse("text1", "text2");

    // Assert
    // assertEquals(90.0, result);
    }

    @Test
    public void testLogError() throws IOException {
    // Arrange
    IDataManager saver = Mockito.mock(IDataManager.class);
    Path dirPath = Mockito.mock(Path.class);
    Path filePath = Mockito.mock(Path.class);

    Mockito.when(saver.createDir(Paths.get(Mockito.anyString()))).thenReturn(dirPath);
    Mockito.when(saver.createFile(Mockito.anyString(), Mockito.anyString())).thenReturn(filePath);

    // ApacheHTTP_SimilarityClient clientUnderTest = new ApacheHTTP_SimilarityClient(null);

    // Assert
    Mockito.verify(saver, Mockito.times(1)).createDir(Paths.get(Mockito.anyString()));
    Mockito.verify(saver, Mockito.times(1)).createFile(Mockito.anyString(), Mockito.anyString());
    }
}