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

import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticator;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionManager;
import io.jmix.samples.remoting.entity.Foo;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("sample")
@Profile("!remoting || client")
public class SampleController {

    static class Result {
        String echo;
        List<Foo> entities;

        public String getEcho() {
            return echo;
        }

        public void setEcho(String echo) {
            this.echo = echo;
        }

        public List<Foo> getEntities() {
            return entities;
        }

        public void setEntities(List<Foo> entities) {
            this.entities = entities;
        }

        public Result(String echo, List<Foo> entities) {
            this.echo = echo;
            this.entities = entities;
        }
    }

    @Inject
    private EchoService echoService;

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSessionManager userSessionManager;

    @Inject
    private Authenticator authenticator;

    @GetMapping(path = "/no-auth/{input}")
    Result echoNoAuth(@PathVariable String input) {

        String echo = echoService.echo(input);

        return new Result(echo, null);
    }

    @GetMapping(path = "/user-auth/{input}")
    Result echoUserAuth(@PathVariable String input) {

        UserSession session = userSessionManager.createSession(new UsernamePasswordAuthenticationToken("admin", "admin123"));

        String echo = echoService.echo(input);

        List<Foo> data = createAndLoadData();

        userSessionManager.removeSession();

        return new Result(echo, data);
    }

    @GetMapping(path = "/system-auth/{input}")
    Result echoSystemAuth(@PathVariable String input) {

        String echo;
        List<Foo> data;
        authenticator.begin();
        try {
            echo = echoService.echo(input);
            data = createAndLoadData();
        } finally {
            authenticator.end();
        }

        return new Result(echo, data);
    }

    @GetMapping(path = "/system-user-auth/{input}")
    Result echoSystemUserAuth(@PathVariable String input) {

        String echo;
        List<Foo> data;
        authenticator.begin("admin");
        try {
            echo = echoService.echo(input);
            data = createAndLoadData();
        } finally {
            authenticator.end();
        }

        return new Result(echo, data);
    }

    private List<Foo> createAndLoadData() {
        Foo foo = dataManager.create(Foo.class);
        foo.setName("Foo-" + LocalDateTime.now().toString());
        dataManager.commit(foo);

        return dataManager.load(Foo.class).list();
    }
}
