package com.pastryapi.repository.impl;

import com.pastryapi.model.Recipe;
import com.pastryapi.model.RecipeLine;
import com.pastryapi.repository.RecipeRepository;
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
public class RecipeRepositoryImpl implements RecipeRepository {

    private final Connection connection;

    public RecipeRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Recipe save(Recipe recipe) {
        synchronized (connection) {
            try {
                insertRecipe(recipe);
                insertLines(recipe);
            } catch (SQLException e) {
                throw new RuntimeException("Error saving recipe: " + e.getMessage(), e);
            }
        }
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM recipes WHERE guid = ?")) {
                pstmt.setString(1, id.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Recipe recipe = mapRecipe(rs);
                        recipe.setLines(loadLines(id));
                        return Optional.of(recipe);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding recipe: " + e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Recipe update(Recipe recipe) {
        synchronized (connection) {
            try {
                String sql = """
                        UPDATE recipes SET name = ?, description = ?, servings = ?, active = ?, updated_at = ?
                        WHERE guid = ?
                        """;
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, recipe.getName());
                    pstmt.setString(2, recipe.getDescription());
                    pstmt.setInt(3, recipe.getServings());
                    pstmt.setBoolean(4, recipe.isActive());
                    pstmt.setString(5, recipe.getUpdatedAt().toString());
                    pstmt.setString(6, recipe.getGuid().toString());
                    if (pstmt.executeUpdate() == 0) {
                        throw new SQLException("Updating recipe failed, no rows affected.");
                    }
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM recipe_lines WHERE recipe_guid = ?")) {
                    delete.setString(1, recipe.getGuid().toString());
                    delete.executeUpdate();
                }
                insertLines(recipe);
            } catch (SQLException e) {
                throw new RuntimeException("Error updating recipe: " + e.getMessage(), e);
            }
        }
        return recipe;
    }

    @Override
    public void deleteById(UUID id) {
        synchronized (connection) {
            try (PreparedStatement lines = connection.prepareStatement("DELETE FROM recipe_lines WHERE recipe_guid = ?");
                 PreparedStatement recipe = connection.prepareStatement("DELETE FROM recipes WHERE guid = ?")) {
                lines.setString(1, id.toString());
                lines.executeUpdate();
                recipe.setString(1, id.toString());
                recipe.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error deleting recipe: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public long count() {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM recipes");
                 ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error counting recipes: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<Recipe> findPage(int offset, int limit) {
        List<Recipe> recipes = new ArrayList<>();
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT * FROM recipes ORDER BY name COLLATE NOCASE LIMIT ? OFFSET ?")) {
                pstmt.setInt(1, limit);
                pstmt.setInt(2, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Recipe recipe = mapRecipe(rs);
                        recipe.setLines(loadLines(recipe.getGuid()));
                        recipes.add(recipe);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error listing recipes: " + e.getMessage(), e);
            }
        }
        return recipes;
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT 1 FROM recipes WHERE lower(name) = lower(?) LIMIT 1")) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error checking recipe name: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isIngredientUsed(UUID ingredientId) {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT 1 FROM recipe_lines WHERE ingredient_guid = ? LIMIT 1")) {
                pstmt.setString(1, ingredientId.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error checking ingredient usage: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void decrementIngredientQuantity(UUID ingredientId, double amount) {
        synchronized (connection) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE ingredients SET quantity = quantity - ?, updated_at = ? WHERE guid = ?")) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, Instant.now().toString());
                pstmt.setString(3, ingredientId.toString());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error decrementing stock: " + e.getMessage(), e);
            }
        }
    }

    private void insertRecipe(Recipe recipe) throws SQLException {
        String sql = """
                INSERT INTO recipes(guid, name, description, servings, active, updated_at)
                VALUES(?,?,?,?,?,?)
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, recipe.getGuid().toString());
            pstmt.setString(2, recipe.getName());
            pstmt.setString(3, recipe.getDescription());
            pstmt.setInt(4, recipe.getServings());
            pstmt.setBoolean(5, recipe.isActive());
            pstmt.setString(6, recipe.getUpdatedAt().toString());
            pstmt.executeUpdate();
        }
    }

    private void insertLines(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipe_lines(recipe_guid, ingredient_guid, quantity) VALUES(?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (RecipeLine line : recipe.getLines()) {
                pstmt.setString(1, recipe.getGuid().toString());
                pstmt.setString(2, line.getIngredientId().toString());
                pstmt.setDouble(3, line.getQuantity());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private List<RecipeLine> loadLines(UUID recipeId) throws SQLException {
        String sql = """
                SELECT rl.ingredient_guid, rl.quantity, i.name, i.unit
                FROM recipe_lines rl
                JOIN ingredients i ON i.guid = rl.ingredient_guid
                WHERE rl.recipe_guid = ?
                ORDER BY i.name COLLATE NOCASE
                """;
        List<RecipeLine> lines = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, recipeId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(new RecipeLine(
                            UUID.fromString(rs.getString("ingredient_guid")),
                            rs.getString("name"),
                            rs.getString("unit"),
                            rs.getDouble("quantity")
                    ));
                }
            }
        }
        return lines;
    }

    private Recipe mapRecipe(ResultSet rs) throws SQLException {
        return new Recipe(
                UUID.fromString(rs.getString("guid")),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("servings"),
                rs.getBoolean("active"),
                Instant.parse(rs.getString("updated_at")),
                new ArrayList<>()
        );
    }
}
