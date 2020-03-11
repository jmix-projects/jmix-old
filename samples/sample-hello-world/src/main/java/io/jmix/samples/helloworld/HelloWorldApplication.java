package io.jmix.samples.helloworld;

import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticator;
import io.jmix.security.entity.Group;
import io.jmix.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@SpringBootApplication
public class HelloWorldApplication {

    @Autowired
    private DataManager dataManager;
    @Autowired
    Authenticator authenticator;

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    private void onStartup() {
        authenticator.withSystem(() -> {
//            Group group = new Group();
//            group.setName("Root");
//            User user = new User();
//            user.setLogin("u1");
//            user.setName("User 1");
//            user.setGroup(group);
//            dataManager.save(group, user);
//
//            List<User> users = dataManager.load(User.class).list();
//            System.out.println(">>> users: " + users);

            Greeting greeting = new Greeting();
            greeting.setText("Hello");
            dataManager.save(greeting);

            return null;
        });
    }
}
