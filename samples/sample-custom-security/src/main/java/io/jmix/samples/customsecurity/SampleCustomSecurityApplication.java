package io.jmix.samples.customsecurity;

import io.jmix.core.security.Security;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;

@SpringBootApplication
public class SampleCustomSecurityApplication implements CommandLineRunner {

    @Autowired
    private Security security;

    public static void main(String[] args) {
        SpringApplication.run(SampleCustomSecurityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Security implementation is " + security.getClass().getName());
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}

