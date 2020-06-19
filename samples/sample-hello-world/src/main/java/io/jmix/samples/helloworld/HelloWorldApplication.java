package io.jmix.samples.helloworld;

import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticator;
import io.jmix.core.security.UserRepository;
import io.jmix.core.security.impl.CoreUser;
import io.jmix.core.security.impl.InMemoryUserRepository;
import io.jmix.samples.helloworld.entity.Greeting;
import io.jmix.samples.helloworld.role.OrderViewRole;
import io.jmix.security.model.Role;
import io.jmix.security.role.assignment.InMemoryRoleAssignmentProvider;
import io.jmix.security.role.assignment.RoleAssignment;
import io.jmix.securitydata.entity.RoleAssignmentEntity;
import io.jmix.securitydata.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class HelloWorldApplication {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Authenticator authenticator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InMemoryRoleAssignmentProvider inMemoryRoleAssignmentProvider;

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

            initDatabaseRoles();

            return null;
        });
        initUsers();
    }

    private void initDatabaseRoles() {
        RoleEntity dbRole1 = new RoleEntity();
        dbRole1.setName("DB role 1");
        dbRole1.setCode("dbRole1");
        dbRole1.setScope(Role.DEFAULT_SCOPE);

        RoleAssignmentEntity dbRole1AdminAssignment = new RoleAssignmentEntity();
        dbRole1AdminAssignment.setUserKey("admin");
        dbRole1AdminAssignment.setRoleCode(dbRole1.getCode());

        dataManager.save(dbRole1, dbRole1AdminAssignment);
    }

    private void initUsers() {
        CoreUser admin = new CoreUser("admin", "{noop}admin", "Admin");
        CoreUser user1 = new CoreUser("user1", "{noop}1", "User 1");
        if (userRepository instanceof InMemoryUserRepository) {
            ((InMemoryUserRepository) userRepository).createUser(admin);
            ((InMemoryUserRepository) userRepository).createUser(user1);
        }

        inMemoryRoleAssignmentProvider.addAssignment(new RoleAssignment(user1.getKey(), OrderViewRole.CODE));
    }
}
