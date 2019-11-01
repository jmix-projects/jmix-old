package io.jmix.samples.helloworld;

import io.jmix.core.DataManager;
import io.jmix.security.entity.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class HelloWorldApplication {

	@Autowired
	private DataManager dataManager;

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:hsqldb:mem:testdb");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@EventListener(ApplicationStartedEvent.class)
	private void onStartup() {
		User user = new User();
		user.setLogin("u1");
		user.setName("User 1");
		dataManager.commit(user);

		List<User> users = dataManager.load(User.class).list();
		System.out.println(">>> users: " + users);
	}
}