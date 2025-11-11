package com.pastryapi.repository;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ingredients (
                guid TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                brand TEXT,
                price REAL NOT NULL,
                status BOOLEAN NOT NULL,
                observations TEXT
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'ingredients' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            // In a real app, you'd want more robust error handling
            throw new RuntimeException("Could not create database table", e);
        }
    }
}
