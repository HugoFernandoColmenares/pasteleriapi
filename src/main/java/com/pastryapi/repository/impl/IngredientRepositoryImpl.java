package com.pastryapi.repository.impl;

import com.pastryapi.model.Ingredient;
import com.pastryapi.repository.IngredientRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {

    private final Connection connection;

    public IngredientRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        String sql = """
                INSERT INTO ingredients(guid, name, brand, price, status, observations, unit, quantity, minimum_quantity, updated_at)
                VALUES(?,?,?,?,?,?,?,?,?,?)
                """;
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                bindIngredient(pstmt, ingredient, true);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error saving ingredient: " + e.getMessage(), e);
            }
        }
        return ingredient;
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        String sql = "SELECT * FROM ingredients WHERE guid = ?";
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, id.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapRow(rs));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding ingredient by id: " + e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Ingredient update(Ingredient ingredient) {
        String sql = """
                UPDATE ingredients
                SET name = ?, brand = ?, price = ?, status = ?, observations = ?, unit = ?, quantity = ?, minimum_quantity = ?, updated_at = ?
                WHERE guid = ?
                """;
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, ingredient.getName());
                pstmt.setString(2, ingredient.getBrand());
                pstmt.setDouble(3, ingredient.getPrice());
                pstmt.setBoolean(4, ingredient.isStatus());
                pstmt.setString(5, ingredient.getObservations());
                pstmt.setString(6, ingredient.getUnit());
                pstmt.setDouble(7, ingredient.getQuantity());
                pstmt.setDouble(8, ingredient.getMinimumQuantity());
                pstmt.setString(9, ingredient.getUpdatedAt().toString());
                pstmt.setString(10, ingredient.getGuid().toString());
                int affected = pstmt.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Updating ingredient failed, no rows affected.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error updating ingredient: " + e.getMessage(), e);
            }
        }
        return ingredient;
    }

    @Override
    public void deleteById(UUID id) {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM ingredients WHERE guid = ?")) {
                pstmt.setString(1, id.toString());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error deleting ingredient: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public long count(String name, Boolean status, Boolean lowStock) {
        String sql = "SELECT COUNT(*) FROM ingredients" + whereClause(name, status, lowStock);
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                bindFilters(pstmt, name, status);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() ? rs.getLong(1) : 0;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error counting ingredients: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<Ingredient> search(String name, Boolean status, Boolean lowStock, int offset, int limit) {
        String sql = "SELECT * FROM ingredients" + whereClause(name, status, lowStock)
                + " ORDER BY name COLLATE NOCASE LIMIT ? OFFSET ?";
        List<Ingredient> ingredients = new ArrayList<>();
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                int index = bindFilters(pstmt, name, status);
                pstmt.setInt(index++, limit);
                pstmt.setInt(index, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ingredients.add(mapRow(rs));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error searching ingredients: " + e.getMessage(), e);
            }
        }
        return ingredients;
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT 1 FROM ingredients WHERE lower(name) = lower(?) LIMIT 1";
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error checking ingredient name: " + e.getMessage(), e);
            }
        }
    }

    private String whereClause(String name, Boolean status, Boolean lowStock) {
        List<String> clauses = new ArrayList<>();
        if (name != null && !name.isBlank()) {
            clauses.add("lower(name) LIKE lower(?)");
        }
        if (status != null) {
            clauses.add("status = ?");
        }
        if (Boolean.TRUE.equals(lowStock)) {
            clauses.add("quantity <= minimum_quantity");
        }
        if (clauses.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", clauses);
    }

    private int bindFilters(PreparedStatement pstmt, String name, Boolean status) throws SQLException {
        int index = 1;
        if (name != null && !name.isBlank()) {
            pstmt.setString(index++, "%" + name.trim() + "%");
        }
        if (status != null) {
            pstmt.setBoolean(index++, status);
        }
        return index;
    }

    private void bindIngredient(PreparedStatement pstmt, Ingredient ingredient, boolean includeId) throws SQLException {
        int i = 1;
        if (includeId) {
            pstmt.setString(i++, ingredient.getGuid().toString());
        }
        pstmt.setString(i++, ingredient.getName());
        pstmt.setString(i++, ingredient.getBrand());
        pstmt.setDouble(i++, ingredient.getPrice());
        pstmt.setBoolean(i++, ingredient.isStatus());
        pstmt.setString(i++, ingredient.getObservations());
        pstmt.setString(i++, ingredient.getUnit());
        pstmt.setDouble(i++, ingredient.getQuantity());
        pstmt.setDouble(i++, ingredient.getMinimumQuantity());
        pstmt.setString(i, ingredient.getUpdatedAt().toString());
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        String updated = rs.getString("updated_at");
        Instant updatedAt = (updated == null || updated.isBlank()) ? Instant.EPOCH : Instant.parse(updated);
        return new Ingredient(
                UUID.fromString(rs.getString("guid")),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getDouble("price"),
                rs.getBoolean("status"),
                rs.getString("observations"),
                rs.getString("unit"),
                rs.getDouble("quantity"),
                rs.getDouble("minimum_quantity"),
                updatedAt
        );
    }
}
