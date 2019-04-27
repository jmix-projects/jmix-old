/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.samples.remoting;

import io.jmix.samples.remoting.test.TestSupport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.profiles.active=remoting,client",
                "jmix.remoting.serverUrl=http://localhost:8765",
                "jmix.remoting.clientToken=123"
        }
)
public class TrustedClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private static Process process;

    @BeforeClass
    public static void startServer() throws Exception {
        process = TestSupport.startServer(8765);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (process != null) {
            process.destroyForcibly();
        }
    }

    @Test
    public void test_noAuth() {
        ResponseEntity<SampleController.Result> response = rest.getForEntity(getBaseUrl() + "/no-auth/abc", SampleController.Result.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_systemAuth() {
        ResponseEntity<SampleController.Result> response = rest.getForEntity(getBaseUrl() + "/system-auth/abc", SampleController.Result.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc", requireNonNull(response.getBody()).echo);
    }

    @Test
    public void test_systemUserAuth() {
        ResponseEntity<SampleController.Result> response = rest.getForEntity(getBaseUrl() + "/system-user-auth/abc", SampleController.Result.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc", requireNonNull(response.getBody()).echo);
    }

    @Test
    public void test_userAuth() {
        ResponseEntity<SampleController.Result> response = rest.getForEntity(getBaseUrl() + "/user-auth/abc", SampleController.Result.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc", requireNonNull(response.getBody()).echo);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/sample";
    }
}
