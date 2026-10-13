package com.pastryapi;

import com.pastryapi.repository.DatabaseInitializer;
import com.pastryapi.repository.DemoDataSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PastryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PastryApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(DatabaseInitializer databaseInitializer, DemoDataSeeder demoDataSeeder) {
        return args -> {
            databaseInitializer.createTable();
            demoDataSeeder.seedIfEmpty();
        };
    }
}
