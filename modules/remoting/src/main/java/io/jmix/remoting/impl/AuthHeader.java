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

package io.jmix.remoting.impl;

import org.springframework.security.authentication.BadCredentialsException;

import javax.annotation.Nullable;
import java.util.Base64;

public class AuthHeader {

    public static final String SCHEME = "JmixRmt";

    private String sessionId;
    private String user;
    private String clientToken;

    public String getSessionId() {
        return sessionId;
    }

    public String getUser() {
        return user;
    }

    public String getClientToken() {
        return clientToken;
    }

    public static String encode(Object sessionId) {
        return SCHEME + " s=" + sessionId;
    }

    public static String encode(String clientToken, @Nullable String user) {
        String result = SCHEME + " c=" + Base64.getEncoder().encodeToString(clientToken.getBytes());
        if (user != null) {
            result += ",u=" + user;
        }
        return result;
    }

    private AuthHeader() {
    }

    public static AuthHeader decode(String input) {
        try {
            AuthHeader header = new AuthHeader();
            int idx = input.indexOf(' ');
            if (SCHEME.equals(input.substring(0, idx))) {
                String[] params = input.substring(idx + 1).split(",");
                for (String param : params) {
                    String[] paramParts = param.split("=");
                    if ("s".equals(paramParts[0])) {
                        header.sessionId = paramParts[1];
                    } else if ("u".equals(paramParts[0])) {
                        header.user = paramParts[1];
                    } else if ("c".equals(paramParts[0])) {
                        header.clientToken = new String(Base64.getDecoder().decode(paramParts[1]));
                    }
                }
                return header;
            } else {
                throw new UnsupportedOperationException("Unsupported scheme");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Error parsing authorization header: " + input, e);
        }
    }

}
