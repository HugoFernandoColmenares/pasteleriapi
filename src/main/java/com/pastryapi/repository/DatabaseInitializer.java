package com.pastryapi.repository;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void createTable() {
        synchronized (connection) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS ingredients (
                            guid TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            brand TEXT,
                            price REAL NOT NULL,
                            status INTEGER NOT NULL,
                            observations TEXT,
                            unit TEXT NOT NULL DEFAULT 'kg',
                            quantity REAL NOT NULL DEFAULT 0,
                            minimum_quantity REAL NOT NULL DEFAULT 0,
                            updated_at TEXT NOT NULL DEFAULT ''
                        )
                        """);
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS recipes (
                            guid TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            description TEXT,
                            servings INTEGER NOT NULL,
                            active INTEGER NOT NULL,
                            updated_at TEXT NOT NULL
                        )
                        """);
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS recipe_lines (
                            recipe_guid TEXT NOT NULL,
                            ingredient_guid TEXT NOT NULL,
                            quantity REAL NOT NULL,
                            PRIMARY KEY (recipe_guid, ingredient_guid),
                            FOREIGN KEY (recipe_guid) REFERENCES recipes(guid) ON DELETE CASCADE,
                            FOREIGN KEY (ingredient_guid) REFERENCES ingredients(guid)
                        )
                        """);
                ensureColumn(stmt, "ingredients", "unit", "TEXT NOT NULL DEFAULT 'kg'");
                ensureColumn(stmt, "ingredients", "quantity", "REAL NOT NULL DEFAULT 0");
                ensureColumn(stmt, "ingredients", "minimum_quantity", "REAL NOT NULL DEFAULT 0");
                ensureColumn(stmt, "ingredients", "updated_at", "TEXT NOT NULL DEFAULT ''");
            } catch (SQLException e) {
                throw new RuntimeException("Could not create database tables", e);
            }
        }
    }

    private void ensureColumn(Statement stmt, String table, String column, String definition) throws SQLException {
        boolean exists = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }
}
