package com.pastryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.pastryapi.repository.DatabaseInitializer;

@SpringBootApplication
public class PastryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PastryApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(DatabaseInitializer databaseInitializer) {
        return args -> {
            databaseInitializer.createTable();
        };
    }
}
