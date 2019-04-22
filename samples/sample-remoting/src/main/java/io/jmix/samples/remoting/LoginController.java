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

import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionManager;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@Profile("!remoting || client")
public class LoginController {

    @Inject
    UserSessionManager userSessionManager;

    @PostMapping("/login")
    String login(@RequestParam String username, @RequestParam String password) {
        UserSession session = userSessionManager.createSession(new UsernamePasswordAuthenticationToken(username, password));
        return session.getId().toString();
    }
}
