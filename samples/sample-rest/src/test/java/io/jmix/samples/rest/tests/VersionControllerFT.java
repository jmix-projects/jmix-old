package io.jmix.samples.rest.tests;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Ignore;
import org.junit.Test;

import static io.jmix.samples.rest.tools.RestTestUtils.*;
import static org.junit.Assert.*;

//todo version
@Ignore
public class VersionControllerFT extends AbstractRestControllerFT {

    @Test
    public void getApiVersion() throws Exception {
        String url = baseUrl + "/version";
        try (CloseableHttpResponse response = sendGet(url, oauthToken, null)) {
            assertEquals(HttpStatus.SC_OK, statusCode(response));

            String version = responseToString(response);
            assertNotNull(version);
            assertTrue(version.length() > 0);
        }
    }
}
