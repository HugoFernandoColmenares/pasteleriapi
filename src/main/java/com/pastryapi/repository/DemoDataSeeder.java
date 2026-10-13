package com.pastryapi.repository;

import com.pastryapi.model.Ingredient;
import com.pastryapi.model.Recipe;
import com.pastryapi.model.RecipeLine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DemoDataSeeder {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    @Value("${app.seed-data:true}")
    private boolean seedData;

    public DemoDataSeeder(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public void seedIfEmpty() {
        if (!seedData || ingredientRepository.count(null, null, null) > 0) {
            return;
        }

        Instant now = Instant.now();
        Ingredient flour = ingredient("Flour", "King Arthur", 2.40, "kg", 10, 2, "All-purpose", now);
        Ingredient sugar = ingredient("Sugar", "Domino", 1.80, "kg", 5, 1, "White granulated", now);
        Ingredient butter = ingredient("Butter", "Plugrá", 8.50, "kg", 4, 0.5, "Unsalted", now);
        Ingredient eggs = ingredient("Eggs", "Vital Farms", 0.35, "unit", 60, 12, "Large", now);
        Ingredient milk = ingredient("Milk", "Local dairy", 1.10, "L", 8, 2, "Whole milk", now);

        ingredientRepository.save(flour);
        ingredientRepository.save(sugar);
        ingredientRepository.save(butter);
        ingredientRepository.save(eggs);
        ingredientRepository.save(milk);

        Recipe sponge = new Recipe();
        sponge.setGuid(UUID.randomUUID());
        sponge.setName("Vanilla sponge");
        sponge.setDescription("Light vanilla cake used as a base for layered desserts.");
        sponge.setServings(8);
        sponge.setActive(true);
        sponge.setUpdatedAt(now);
        sponge.setLines(List.of(
                new RecipeLine(flour.getGuid(), flour.getName(), flour.getUnit(), 0.25),
                new RecipeLine(sugar.getGuid(), sugar.getName(), sugar.getUnit(), 0.20),
                new RecipeLine(butter.getGuid(), butter.getName(), butter.getUnit(), 0.10),
                new RecipeLine(eggs.getGuid(), eggs.getName(), eggs.getUnit(), 3),
                new RecipeLine(milk.getGuid(), milk.getName(), milk.getUnit(), 0.10)
        ));
        recipeRepository.save(sponge);
    }

    private Ingredient ingredient(
            String name,
            String brand,
            double price,
            String unit,
            double quantity,
            double minimum,
            String observations,
            Instant now
    ) {
        return new Ingredient(UUID.randomUUID(), name, brand, price, true, observations, unit, quantity, minimum, now);
    }
}
