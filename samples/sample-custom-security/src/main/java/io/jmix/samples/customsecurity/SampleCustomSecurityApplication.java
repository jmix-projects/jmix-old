package io.jmix.samples.customsecurity;

import io.jmix.core.security.Security;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;

@SpringBootApplication
public class SampleCustomSecurityApplication implements CommandLineRunner {

    @Inject
    private Security security;

    public static void main(String[] args) {
        SpringApplication.run(SampleCustomSecurityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Security implementation is " + security.getClass().getName());
    }
}

