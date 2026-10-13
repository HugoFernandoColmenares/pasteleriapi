package com.pastryapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    @Value("${app.datasource.url:jdbc:sqlite:pastry_shop.db}")
    private String dbUrl;

    @Bean
    public Connection connection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(dbUrl);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA busy_timeout = 5000");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
    }
}
