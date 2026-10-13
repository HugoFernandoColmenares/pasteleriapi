package com.pastryapi.service.impl;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.dto.PageResponse;
import com.pastryapi.dto.ProduceRequest;
import com.pastryapi.dto.ProduceResponse;
import com.pastryapi.dto.RecipeDto;
import com.pastryapi.dto.RecipeLineDto;
import com.pastryapi.exception.ConflictException;
import com.pastryapi.exception.InsufficientStockException;
import com.pastryapi.exception.ResourceNotFoundException;
import com.pastryapi.exception.StockShortage;
import com.pastryapi.model.Ingredient;
import com.pastryapi.model.Recipe;
import com.pastryapi.model.RecipeLine;
import com.pastryapi.repository.IngredientRepository;
import com.pastryapi.repository.RecipeRepository;
import com.pastryapi.service.RecipeService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public RecipeDto createRecipe(RecipeDto dto) {
        if (recipeRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("A recipe named '" + dto.getName() + "' already exists");
        }
        Recipe recipe = toEntity(dto);
        recipe.setGuid(UUID.randomUUID());
        recipe.setUpdatedAt(Instant.now());
        recipe.setLines(resolveLines(dto.getLines()));
        return toDto(recipeRepository.save(recipe));
    }

    @Override
    public RecipeDto getRecipeById(UUID id) {
        return toDto(requireRecipe(id));
    }

    @Override
    public RecipeDto updateRecipe(UUID id, RecipeDto dto) {
        Recipe existing = requireRecipe(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setServings(dto.getServings());
        existing.setActive(dto.isActive());
        existing.setUpdatedAt(Instant.now());
        existing.setLines(resolveLines(dto.getLines()));
        return toDto(recipeRepository.update(existing));
    }

    @Override
    public void deleteRecipe(UUID id) {
        requireRecipe(id);
        recipeRepository.deleteById(id);
    }

    @Override
    public PageResponse<RecipeDto> list(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        long total = recipeRepository.count();
        List<RecipeDto> content = recipeRepository.findPage(safePage * safeSize, safeSize).stream()
                .map(this::toDto)
                .toList();
        int totalPages = (int) Math.ceil(total / (double) safeSize);
        return new PageResponse<>(content, safePage, safeSize, total, totalPages);
    }

    @Override
    public ProduceResponse produce(UUID id, ProduceRequest request) {
        Recipe recipe = requireRecipe(id);
        if (!recipe.isActive()) {
            throw new ConflictException("Recipe '" + recipe.getName() + "' is inactive");
        }
        int batches = request.batches();
        List<StockShortage> shortages = new ArrayList<>();
        for (RecipeLine line : recipe.getLines()) {
            Ingredient ingredient = ingredientRepository.findById(line.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + line.getIngredientId()));
            if (!ingredient.isStatus()) {
                throw new ConflictException("Ingredient '" + ingredient.getName() + "' is discontinued");
            }
            double required = line.getQuantity() * batches;
            if (ingredient.getQuantity() + 1e-9 < required) {
                shortages.add(new StockShortage(
                        ingredient.getGuid(),
                        ingredient.getName(),
                        ingredient.getUnit(),
                        required,
                        ingredient.getQuantity()
                ));
            }
        }
        if (!shortages.isEmpty()) {
            throw new InsufficientStockException(
                    "Not enough stock to produce " + batches + " batch(es) of " + recipe.getName(),
                    shortages
            );
        }
        List<IngredientDto> updated = new ArrayList<>();
        for (RecipeLine line : recipe.getLines()) {
            double required = line.getQuantity() * batches;
            recipeRepository.decrementIngredientQuantity(line.getIngredientId(), required);
            Ingredient after = ingredientRepository.findById(line.getIngredientId()).orElseThrow();
            updated.add(toIngredientDto(after));
        }
        return new ProduceResponse(recipe.getGuid(), recipe.getName(), batches, updated);
    }

    private Recipe requireRecipe(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    private List<RecipeLine> resolveLines(List<RecipeLineDto> lines) {
        List<RecipeLine> resolved = new ArrayList<>();
        for (RecipeLineDto line : lines) {
            Ingredient ingredient = ingredientRepository.findById(line.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + line.getIngredientId()));
            resolved.add(new RecipeLine(
                    ingredient.getGuid(),
                    ingredient.getName(),
                    ingredient.getUnit(),
                    line.getQuantity()
            ));
        }
        return resolved;
    }

    private RecipeDto toDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setGuid(recipe.getGuid());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setServings(recipe.getServings());
        dto.setActive(recipe.isActive());
        dto.setUpdatedAt(recipe.getUpdatedAt());
        dto.setLines(recipe.getLines().stream().map(this::toLineDto).toList());
        return dto;
    }

    private RecipeLineDto toLineDto(RecipeLine line) {
        RecipeLineDto dto = new RecipeLineDto();
        dto.setIngredientId(line.getIngredientId());
        dto.setIngredientName(line.getIngredientName());
        dto.setUnit(line.getUnit());
        dto.setQuantity(line.getQuantity());
        return dto;
    }

    private Recipe toEntity(RecipeDto dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName().trim());
        recipe.setDescription(dto.getDescription());
        recipe.setServings(dto.getServings());
        recipe.setActive(dto.isActive());
        return recipe;
    }

    private IngredientDto toIngredientDto(Ingredient ingredient) {
        IngredientDto dto = new IngredientDto();
        dto.setGuid(ingredient.getGuid());
        dto.setName(ingredient.getName());
        dto.setBrand(ingredient.getBrand());
        dto.setPrice(ingredient.getPrice());
        dto.setStatus(ingredient.isStatus());
        dto.setObservations(ingredient.getObservations());
        dto.setUnit(ingredient.getUnit());
        dto.setQuantity(ingredient.getQuantity());
        dto.setMinimumQuantity(ingredient.getMinimumQuantity());
        dto.setUpdatedAt(ingredient.getUpdatedAt());
        dto.setLowStock(ingredient.isLowStock());
        return dto;
    }
}
