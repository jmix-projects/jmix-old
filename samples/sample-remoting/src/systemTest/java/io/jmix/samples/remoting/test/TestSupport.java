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

package io.jmix.samples.remoting.test;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TestSupport {

    private static final Logger log = LoggerFactory.getLogger(TestSupport.class);

    public static final String HEALTH_CHECK = "/health";

    private static class StreamConsumer implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamConsumer(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }

    public static Process startServer(int port) {
        log.debug("Starting server...");
        File binDir = getServerDir();
        Process process;
        try {
            process = new ProcessBuilder("java", "-jar",
                    "sample-remoting-0.0.1.jar",
                    "--server.port=" + port,
                    "--spring.profiles.active=remoting,server",
                    "--jmix.remoting.clientToken=123")
                    .directory(binDir)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StreamConsumer streamConsumer = new StreamConsumer(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamConsumer);

        sleep(2);
        URL url;
        try {
            url = new URL("http://localhost:" + port + HEALTH_CHECK);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        log.debug("Waiting for server on " + url + " ...");
        int count = 0;
        while (count < 3) {
            try {
                log.trace("Requesting {}", url);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(180 * 1000);
                connection.setReadTimeout(180 * 1000);
                connection.connect();
                String res = IOUtils.toString(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                if ("ok".equals(res)) {
                    return process;
                }
            } catch (IOException e) {
                log.trace("Exception connecting to {}: {}", url, e.toString());
                count++;
                log.debug("Waiting for Tomcat (on " + url + ") ...");
                sleep(2);
            }
        }
        throw new RuntimeException("Server didn't answer in appropriate time");
    }

    private static File getServerDir() {
        File currentDir = new File(System.getProperty("user.dir"));
        return new File(currentDir, "build/libs");
    }

    public static void sleep(int seconds) {
        log.debug("Sleeping " + seconds + " sec...");
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }
    }

}
