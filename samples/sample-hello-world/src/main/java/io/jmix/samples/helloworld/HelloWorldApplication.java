package io.jmix.samples.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication implements CommandLineRunner {

	@Autowired
	private Greeter greeter;

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println(greeter.sayHello("there"));
	}
}