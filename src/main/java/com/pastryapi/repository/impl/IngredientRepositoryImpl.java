package com.pastryapi.repository.impl;

import com.pastryapi.model.Ingredient;
import com.pastryapi.repository.IngredientRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
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
        String sql = "INSERT INTO ingredients(guid, name, brand, price, status, observations) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ingredient.getGuid().toString());
            pstmt.setString(2, ingredient.getName());
            pstmt.setString(3, ingredient.getBrand());
            pstmt.setDouble(4, ingredient.getPrice());
            pstmt.setBoolean(5, ingredient.isStatus());
            pstmt.setString(6, ingredient.getObservations());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving ingredient: " + e.getMessage(), e);
        }
        return ingredient;
    }

    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(mapRowToIngredient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all ingredients: " + e.getMessage(), e);
        }
        return ingredients;
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        String sql = "SELECT * FROM ingredients WHERE guid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToIngredient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ingredient by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Ingredient update(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET name = ?, brand = ?, price = ?, status = ?, observations = ? WHERE guid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setString(2, ingredient.getBrand());
            pstmt.setDouble(3, ingredient.getPrice());
            pstmt.setBoolean(4, ingredient.isStatus());
            pstmt.setString(5, ingredient.getObservations());
            pstmt.setString(6, ingredient.getGuid().toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ingredient failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ingredient: " + e.getMessage(), e);
        }
        return ingredient;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM ingredients WHERE guid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // This could mean the ingredient was already deleted, or never existed.
                // For a simple API, we can choose to not throw an error.
                // For a stricter API, you might throw an exception.
                System.out.println("No ingredient found with ID: " + id + " to delete.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ingredient: " + e.getMessage(), e);
        }
    }

    private Ingredient mapRowToIngredient(ResultSet rs) throws SQLException {
        return new Ingredient(
                UUID.fromString(rs.getString("guid")),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getDouble("price"),
                rs.getBoolean("status"),
                rs.getString("observations")
        );
    }
}
